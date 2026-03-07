package org.ecnumc.ecnu.common.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 瘙痒效果 - 基于禁用效果，额外提供力量和急迫效果
 * 等级越高，禁用的盔甲槽位越多，同时获得更强的力量和急迫效果
 */
public class ItchingEffect extends BanEffect {

    public ItchingEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF69B4); // 粉红色
        // 注册tick事件来处理复合效果
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);

        // 应用额外的效果
        if (entity instanceof Player player) {
            applyAdditionalEffects(player, amplifier);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;

            if (player.hasEffect(this)) {
                int level = Objects.requireNonNull(player.getEffect(this)).getAmplifier() + 1;

                // 禁用盔甲槽位（继承自BanEffect）
                disableArmorSlots(player, level);

                // 应用额外效果
                applyAdditionalEffects(player, level - 1);
            }
        }
    }

    /**
     * 根据等级应用额外的效果
     * @param player 玩家实体
     * @param level 效果等级（0开始）
     */
    private void applyAdditionalEffects(Player player, int level) {
        // 移除之前的效果（避免叠加）
        player.removeEffect(MobEffects.DIG_SPEED);
        player.removeEffect(MobEffects.DAMAGE_BOOST);

        // 根据等级应用急迫和力量效果
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                MobEffects.DIG_SPEED,
                200, // 10秒持续时间（会在下次tick时重新应用）
                Math.max(level, 0 ),
                false, // 不显示粒子效果
                true,  
                false
        ));

        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                MobEffects.DAMAGE_BOOST,
                200, // 10秒持续时间
                Math.max(level, 0 ),
                false, // 不显示粒子效果
                true,
                false
        ));
    }
}
