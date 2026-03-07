package org.ecnumc.ecnu.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * 惹怒效果 - 玩家受到的伤害翻倍
 */
public class RilingEffect extends MobEffect {
    public RilingEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4500); // 橙红色
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 持续应用
    }
}
