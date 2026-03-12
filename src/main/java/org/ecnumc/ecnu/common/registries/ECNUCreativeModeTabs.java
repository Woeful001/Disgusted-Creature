package org.ecnumc.ecnu.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static org.ecnumc.ecnu.ECNUForge.MODID;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;

/**
 * Mod Creative Mode Tabs
 * @author liudongyu
 */
public final class ECNUCreativeModeTabs {
	private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

	public static final RegistryObject<CreativeModeTab> ECNU = REGISTER.register(
			"main", () -> CreativeModeTab.builder()
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
					.title(Component.translatable("itemGroup.disgusted_creature.main"))
					.icon(() -> new ItemStack(ECNUItems.DISGUSTED_STONE.get()))
					.displayItems((flags, output) ->
							{
								output.accept(ECNUItems.NULLSET.get());
								output.accept(ECNUItems.DISGUSTED_STONE.get());
								output.accept(ECNUItems.MOSQUITO_SPAWN_EGG.get());
								output.accept(ECNUItems.MOSQUITO_MOUTH.get());
								output.accept(ECNUItems.DISGUSTED_HEART.get());
								output.accept(ECNUItems.DISGUSTED_EGG.get());
								output.accept(ECNUItems.DISGUSTED_WOODEN_SWORD.get());
								output.accept(ECNUItems.DISGUSTED_STONE_SWORD.get());
								output.accept(ECNUItems.DISGUSTED_IRON_SWORD.get());
								output.accept(ECNUItems.DISGUSTED_DIAMOND_SWORD.get());
								output.accept(ECNUItems.DISGUSTED_NETHERITE_SWORD.get());

								// Add custom potion ItemStacks (with Potion NBT) so they show up in the creative tab
								output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ECNUPotions.ITCHING.get()));
								output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ECNUPotions.ITCHING.get()));
								output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ECNUPotions.ITCHING.get()));

								output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ECNUPotions.STRONG_ITCHING.get()));
								output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ECNUPotions.STRONG_ITCHING.get()));
								output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ECNUPotions.STRONG_ITCHING.get()));

								output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ECNUPotions.LONGER_ITCHING.get()));
								output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ECNUPotions.LONGER_ITCHING.get()));
								output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ECNUPotions.LONGER_ITCHING.get()));
							}
						)
						.build()
	);

	private ECNUCreativeModeTabs() {
	}

	/**
	 * Register the creative mode tabs on mod constructing.
	 * @param modBus	the mod bus
	 */
	public static void init(IEventBus modBus) {
		REGISTER.register(modBus);
	}
}
