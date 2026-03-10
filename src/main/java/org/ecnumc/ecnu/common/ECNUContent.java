package org.ecnumc.ecnu.common;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.ecnumc.ecnu.common.entities.MosquitoEntity;
import org.ecnumc.ecnu.common.entities.NullsetEntity;
import org.ecnumc.ecnu.common.entities.ShalltearBloodfallenEntity;
import org.ecnumc.ecnu.common.registries.*;

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
		ECNUEntityTypes.init(modBus);
		ECNUItems.init(modBus);
		ECNUBlocks.init(modBus);
		ECNUCreativeModeTabs.init(modBus);
		ECNUEffects.init(modBus);
		ECNUSounds.init(modBus);
		ECNUPotions.init(modBus);
	}

	private ECNUContent() {
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		// BrewingRecipeRegistry 非线程安全，建议加入队列在主线程执行
		event.enqueueWork(ECNUBrewingRecipe::register);
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(ECNUEntityTypes.NULLSET.get(), NullsetEntity.createAttributes().build());
		event.put(ECNUEntityTypes.MOSQUITO.get(), MosquitoEntity.createAttributes().build());
		event.put(ECNUEntityTypes.SHALLTEAR_BLOODFALLEN.get(), ShalltearBloodfallenEntity.createAttributes().build());
	}

	@SubscribeEvent
	public static void onSpawnPlacementRegister(SpawnPlacementRegisterEvent event) {
		ECNUEntityTypes.registerSpawnPlacements(event);
	}
}
