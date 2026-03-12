package org.ecnumc.ecnu.common.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeSpawnEggItem; // 使用Forge的SpawnEggItem
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ecnumc.ecnu.common.item.DisgustHeartItem;
import org.ecnumc.ecnu.common.item.DisgustSwordItem;
import org.ecnumc.ecnu.common.items.NullsetItem;

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
	public static final RegistryObject<Item> DISGUSTED_HEART = REGISTER.register("disgusted_heart",
			() -> new DisgustHeartItem(new Item.Properties().food(ECNUFoods.DISGUSTED_HEART)));
	public static final RegistryObject<Item> DISGUSTED_EGG = REGISTER.register("disgusted_egg",
			() -> new BlockItem(ECNUBlocks.DISGUSTED_EGG.get(), new Item.Properties().fireResistant()));

	//武器
	public static final RegistryObject<DisgustSwordItem> DISGUSTED_WOODEN_SWORD = REGISTER.register("disgusted_wooden_sword",
			() -> new DisgustSwordItem(Tiers.WOOD, 1, -2.4F, new Item.Properties(), 0.1F));
	public static final RegistryObject<DisgustSwordItem> DISGUSTED_STONE_SWORD = REGISTER.register("disgusted_stone_sword",
			() -> new DisgustSwordItem(Tiers.STONE, 3, -2.4F, new Item.Properties(), 0.2F));
	public static final RegistryObject<DisgustSwordItem> DISGUSTED_IRON_SWORD = REGISTER.register("disgusted_iron_sword",
			() -> new DisgustSwordItem(Tiers.IRON, 5, -2.4F, new Item.Properties(), 0.3F));
	public static final RegistryObject<DisgustSwordItem> DISGUSTED_DIAMOND_SWORD = REGISTER.register("disgusted_diamond_sword",
			() -> new DisgustSwordItem(Tiers.DIAMOND, 7, -2.4F, new Item.Properties(), 0.4F));
	public static final RegistryObject<DisgustSwordItem> DISGUSTED_NETHERITE_SWORD = REGISTER.register("disgusted_netherite_sword",
			() -> new DisgustSwordItem(Tiers.NETHERITE, 9, -2.4F, new Item.Properties().fireResistant(), 0.5F));

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
