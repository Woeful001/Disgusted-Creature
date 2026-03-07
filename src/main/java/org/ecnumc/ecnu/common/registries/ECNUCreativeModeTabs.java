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
