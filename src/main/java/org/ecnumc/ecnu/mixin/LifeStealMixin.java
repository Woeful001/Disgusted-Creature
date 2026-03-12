package org.ecnumc.ecnu.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.ecnumc.ecnu.common.effects.EvilEffect;
import org.ecnumc.ecnu.common.entities.ShalltearBloodfallenEntity;
import org.ecnumc.ecnu.common.item.DisgustSwordItem;
import org.ecnumc.ecnu.common.registries.ECNUEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        if (!(damageSource.getEntity() instanceof LivingEntity attacker) || attacker.level().isClientSide || damageAmount <= 0.0F) {
            return;
        }

        float healAmount = ecnu$getEffectLifeSteal(attacker, damageAmount);
        healAmount += ecnu$getWeaponLifeSteal(attacker, damageSource, damageAmount);

        if (healAmount > 0.0F) {
            attacker.heal(healAmount);
        }
    }

    @Unique
    private static float ecnu$getWeaponLifeSteal(LivingEntity attacker, DamageSource damageSource, float damageAmount) {
        if (attacker instanceof ShalltearBloodfallenEntity) {
            return 0.0F;
        }
        if (!ecnu$isWeaponLifeStealDamage(attacker, damageSource)) {
            return 0.0F;
        }

        ItemStack weaponStack = attacker.getMainHandItem();
        if (weaponStack.getItem() instanceof DisgustSwordItem disgustSwordItem) {
            return damageAmount * disgustSwordItem.getLifeStealRatio();
        }
        return 0.0F;
    }

    @Unique
    private static boolean ecnu$isWeaponLifeStealDamage(LivingEntity attacker, DamageSource damageSource) {
        if (damageSource.is(DamageTypes.PLAYER_ATTACK) || damageSource.is(DamageTypes.MOB_ATTACK)) {
            return true;
        }

        Entity directEntity = damageSource.getDirectEntity();
        return directEntity == null || directEntity == attacker;
    }

    @Unique
    private static float ecnu$getEffectLifeSteal(LivingEntity attacker, float damageAmount) {
        if (attacker instanceof ShalltearBloodfallenEntity) {
            return 0.0F;
        }

        if (!attacker.hasEffect(ECNUEffects.EVIL.get()) && !attacker.hasEffect(ECNUEffects.DISGUSTED.get())) {
            return 0.0F;
        }

        MobEffectInstance effect = attacker.getEffect(ECNUEffects.EVIL.get());
        if (effect == null) {
            effect = attacker.getEffect(ECNUEffects.DISGUSTED.get());
        }
        if (effect == null) {
            return 0.0F;
        }

        float healPercentage = 0.5F;
        try {
            if (ECNUEffects.EVIL.get() instanceof EvilEffect evilEffect) {
                healPercentage = evilEffect.getHealPercentage();
            }
        } catch (Exception ignored) {
            // Fall back to the default 50% lifesteal ratio.
        }

        return damageAmount * healPercentage * (effect.getAmplifier() + 1);
    }
}
