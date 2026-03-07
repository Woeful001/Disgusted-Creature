package org.ecnumc.ecnu.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.ecnumc.ecnu.common.effects.EvilEffect;
import org.ecnumc.ecnu.common.registries.ECNUEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 阴暗效果生命值转化mixin
 * 当攻击者有阴暗效果时，将部分伤害转化为生命值恢复
 */
@Mixin(LivingEntity.class)
public class LifeStealMixin {
    
    @Inject(method = "actuallyHurt", at = @At("TAIL"))
    private void onActuallyHurtTail(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        // 检查伤害来源是否为另一个生物
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            if (attacker.hasEffect(ECNUEffects.Evil.get()) ||
                    attacker.hasEffect(ECNUEffects.DISGUSTED.get())) {
                // 获取阴暗效果实例
                var effect = attacker.getEffect(ECNUEffects.Evil.get());
                if(effect == null)
                {
                    effect = attacker.getEffect(ECNUEffects.DISGUSTED.get());
                }
                if (effect != null) {
                    // 安全地获取治疗百分比
                    float healPercentage = 0.5f; // 默认值
                    
                    // 尝试获取EvilEffect实例来获得准确的治疗百分比
                    try {
                        if (ECNUEffects.Evil.get() instanceof EvilEffect evilEffect ) {
                            healPercentage = evilEffect.getHealPercentage();
                        }
                    } catch (Exception e) {
                        // 如果转换失败，使用默认值
                        healPercentage = 0.5f;
                    }
                    
                    // 计算治疗量
                    float healAmount = damageAmount * healPercentage * (effect.getAmplifier() + 1);
                    
                    // 为攻击者恢复生命值
                    attacker.heal(healAmount);
                }
            }
        }
    }
}
