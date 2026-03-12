package org.ecnumc.ecnu.common.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.ecnumc.ecnu.common.registries.ECNUEffects;

import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * 管理作呕之心给予的永久作呕效果。
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DisgustedHeartBuffHandler {
    private static final String HAS_DISGUSTED_HEART_BUFF = "HasDisgustedHeartBuff";
    private static final int PERMANENT_EFFECT_DURATION = Integer.MAX_VALUE;
    private static final int PERMANENT_EFFECT_AMPLIFIER = 0;

    private DisgustedHeartBuffHandler() {
    }

    public static void grantPermanentBuff(ServerPlayer player) {
        getPersistedData(player).putBoolean(HAS_DISGUSTED_HEART_BUFF, true);
        applyPermanentBuff(player);
    }

    public static boolean hasPermanentBuff(Player player) {
        return getPersistedData(player).getBoolean(HAS_DISGUSTED_HEART_BUFF);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag originalData = event.getOriginal().getPersistentData();
        if (!originalData.contains(Player.PERSISTED_NBT_TAG, Tag.TAG_COMPOUND)) {
            return;
        }

        event.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG,
                originalData.getCompound(Player.PERSISTED_NBT_TAG).copy());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && hasPermanentBuff(player)) {
            applyPermanentBuff(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && hasPermanentBuff(player)) {
            applyPermanentBuff(player);
        }
    }

    private static void applyPermanentBuff(ServerPlayer player) {
        MobEffectInstance currentEffect = player.getEffect(ECNUEffects.DISGUSTED.get());
        if (currentEffect != null) {
            if (currentEffect.getAmplifier() > PERMANENT_EFFECT_AMPLIFIER) {
                return;
            }
            if (currentEffect.getAmplifier() == PERMANENT_EFFECT_AMPLIFIER
                    && currentEffect.getDuration() >= PERMANENT_EFFECT_DURATION / 2) {
                return;
            }
        }

        player.addEffect(new MobEffectInstance(
                ECNUEffects.DISGUSTED.get(),
                PERMANENT_EFFECT_DURATION,
                PERMANENT_EFFECT_AMPLIFIER,
                false,
                true,
                true
        ));
    }

    private static CompoundTag getPersistedData(Player player) {
        CompoundTag playerData = player.getPersistentData();
        if (!playerData.contains(Player.PERSISTED_NBT_TAG, Tag.TAG_COMPOUND)) {
            playerData.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return playerData.getCompound(Player.PERSISTED_NBT_TAG);
    }
}

