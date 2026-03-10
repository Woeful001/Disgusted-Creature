package org.ecnumc.ecnu.common.registries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ecnumc.ecnu.common.items.NullsetItem;
import net.minecraftforge.common.ForgeSpawnEggItem; // 使用Forge的SpawnEggItem

import org.ecnumc.ecnu.common.registries.ECNUEntityTypes;
import org.ecnumc.ecnu.common.registries.ECNUBlocks;

import java.util.function.Supplier; // 添加Supplier导入

import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * Mod Items
 * @author liudongyu
 */
public final class ECNUItems {
	private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	/**
	 * Item for the President of ECNUMC.
	 */
	public static final RegistryObject<NullsetItem> NULLSET = REGISTER.register("nullset", () -> new NullsetItem(new Item.Properties()));
	public static final RegistryObject<BlockItem> DISGUSTED_STONE = REGISTER.register("disgusted_stone",
			() -> new BlockItem(ECNUBlocks.DISGUSTED_STONE.get(), new Item.Properties().fireResistant()));
	public static final RegistryObject<ForgeSpawnEggItem> MOSQUITO_SPAWN_EGG = REGISTER.register("mosquito_spawn_egg",
			() -> new ForgeSpawnEggItem(
                    ECNUEntityTypes.MOSQUITO::get,
					0x2C2C2C,
					0xFF69B4,
					new Item.Properties()
			));
	public static final RegistryObject<Item> MOSQUITO_MOUTH = REGISTER.register("mosquito_mouth", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> DISGUSTED_EGG = REGISTER.register("disgusted_egg",
			() -> new BlockItem(ECNUBlocks.DISGUSTED_EGG.get(), new Item.Properties().fireResistant()));



	private ECNUItems() {
	}

	/**
	 * Register the items on mod constructing.
	 * @param modBus	the mod bus
	 */
	public static void init(IEventBus modBus) {
		REGISTER.register(modBus);
	}
}
