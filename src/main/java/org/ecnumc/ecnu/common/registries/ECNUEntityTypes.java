package org.ecnumc.ecnu.common.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ecnumc.ecnu.common.entities.NullsetEntity;
import org.ecnumc.ecnu.common.entities.MosquitoEntity;

import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * Mod Entity Types
 * @author liudongyu
 */
public final class
ECNUEntityTypes {
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

	/**
	 * Entity Type for the President of ECNUMC.
	 */
	public static final RegistryObject<EntityType<NullsetEntity>> NULLSET = ENTITY_TYPES.register(
			"nullset", () -> EntityType.Builder.<NullsetEntity>of(NullsetEntity::new, MobCategory.MISC)
					.sized(0.6F, 1.8F)
					.clientTrackingRange(10)
					.build(new ResourceLocation(MODID, "nullset").toString())
	);

	public static final RegistryObject<EntityType<MosquitoEntity>> MOSQUITO = ENTITY_TYPES.register(
			"mosquito", () -> EntityType.Builder.<MosquitoEntity>of(MosquitoEntity::new, MobCategory.MONSTER)
					.sized(0.3F, 0.3F)
					.clientTrackingRange(8)
					.fireImmune()
					// 添加自然生成配置
					.canSpawnFarFromPlayer()
					.build(new ResourceLocation(MODID, "mosquito").toString())
	);

	/**
	 * Register spawn placements for entities
	 * @param event spawn placement register event
	 */
	public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
		// 使用简化的检查方法确保生成
		event.register(
			MOSQUITO.get(),
			SpawnPlacements.Type.ON_GROUND,
			Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
			(entityType, level, spawnType, pos, random) -> true,
			SpawnPlacementRegisterEvent.Operation.REPLACE
		);
	}

	private ECNUEntityTypes() {
	}

	/**
	 * Register the entity types on mod constructing.
	 * @param modBus	the mod bus
	 */
	public static void init(IEventBus modBus) {
		ENTITY_TYPES.register(modBus);
	}
}
