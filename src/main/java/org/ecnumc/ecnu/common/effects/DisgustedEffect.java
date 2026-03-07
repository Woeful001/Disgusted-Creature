package org.ecnumc.ecnu.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * 作呕效果 - riling和evil的复合效果
 */
public class DisgustedEffect extends MobEffect {
    public DisgustedEffect() {
        super(MobEffectCategory.HARMFUL, 0x800080); // 紫色
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
