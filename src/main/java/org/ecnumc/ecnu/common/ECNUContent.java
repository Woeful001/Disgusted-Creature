package org.ecnumc.ecnu.common;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.ecnumc.ecnu.common.entities.NullsetEntity;
import org.ecnumc.ecnu.common.entities.MosquitoEntity;

import org.ecnumc.ecnu.common.registries.*;

import static com.mojang.text2speech.Narrator.LOGGER;
import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * Mod Content
 * @author liudongyu
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ECNUContent {
	/**
	 * Called on mod constructing.
	 * @param modBus	the mod bus
	 */
	public static void modConstruction(IEventBus modBus) {
		ECNUEntityTypes.init(modBus);  // 实体类型优先注册
		ECNUItems.init(modBus);        // 物品其次（包含spawn egg）
		ECNUBlocks.init(modBus);
		ECNUCreativeModeTabs.init(modBus);
		ECNUEffects.init(modBus);
		ECNUSounds.init(modBus);
	}

	private ECNUContent() {
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		 // 延迟到事件触发时才获取RegistryObject的值
		 event.put(ECNUEntityTypes.NULLSET.get(), NullsetEntity.createAttributes().build());
		 event.put(ECNUEntityTypes.MOSQUITO.get(), MosquitoEntity.createAttributes().build());
	}
	
	// 添加生成规则注册
	@SubscribeEvent
	public static void onSpawnPlacementRegister(SpawnPlacementRegisterEvent event) {
		ECNUEntityTypes.registerSpawnPlacements(event);
	}
}
