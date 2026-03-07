package org.ecnumc.ecnu.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 阴暗效果 - 造成伤害的部分转化为生命值恢复
 */
public class EvilEffect extends MobEffect {
    private final float healPercentage; // 生命值恢复百分比

    public EvilEffect() {
        this(0.5f); // 默认50%伤害转化为治疗
    }

    public EvilEffect(float healPercentage) {
        super(MobEffectCategory.BENEFICIAL, 0x2F4F4F); // 深灰绿色
        this.healPercentage = healPercentage;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 持续应用
    }

    public float getHealPercentage() {
        return healPercentage;
    }
}
