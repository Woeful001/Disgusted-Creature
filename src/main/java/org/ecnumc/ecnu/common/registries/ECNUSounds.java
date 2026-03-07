package org.ecnumc.ecnu.common.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static org.ecnumc.ecnu.ECNUForge.MODID;

public class ECNUSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> MOSQUITO_FLY = SOUNDS.register("mosquito.fly",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "mosquito.fly")));

    public static void init(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
