package org.ecnumc.ecnu.common.registries;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * Register mod Potions (potion types that carry mob effect instances).
 */
@SuppressWarnings("unused")
public final class ECNUPotions {
    private static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);

    // Itching I: 45s (900 ticks), amplifier 0
    public static final RegistryObject<Potion> ITCHING = REGISTER.register("itching",
            () -> new Potion(new MobEffectInstance(ECNUEffects.ITCHING.get(), 900, 0)));

    // Strong Itching II: 22s (440 ticks), amplifier 1
    public static final RegistryObject<Potion> STRONG_ITCHING = REGISTER.register("strong_itching",
            () -> new Potion(new MobEffectInstance(ECNUEffects.ITCHING.get(), 440, 1)));

    // longer itching: 90s (1800 ticks), amplifier 0
    public static final RegistryObject<Potion> LONGER_ITCHING = REGISTER.register("longer_itching",
            () -> new Potion(new MobEffectInstance(ECNUEffects.ITCHING.get(), 1800, 0)));

    public static void init(IEventBus modBus) {
        REGISTER.register(modBus);
    }

    private ECNUPotions() {
    }
}
