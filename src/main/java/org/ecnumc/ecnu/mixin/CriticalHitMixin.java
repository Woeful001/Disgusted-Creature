package org.ecnumc.ecnu.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.ecnumc.ecnu.common.registries.ECNUEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 玩家受到伤害翻倍mixin
 * 当玩家有惹怒效果时，受到的所有伤害都会翻倍
 */
@Mixin(Player.class)
public class CriticalHitMixin {
    
    // 使用Unique注解确保字段名唯一性
    @Unique
    private boolean disgusted_Creature$doubleDamage = false;
    
    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    private void onActuallyHurt(DamageSource pDamageSource, float pDamageAmount, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        
        if (player.hasEffect(ECNUEffects.RILING.get()) ||
                player.hasEffect(ECNUEffects.DISGUSTED.get())) {
            // 标记这次伤害应该被翻倍
            disgusted_Creature$doubleDamage = true;
        }
    }
    
    @Inject(method = "actuallyHurt", at = @At("RETURN"))
    private void onActuallyHurtReturn(DamageSource pDamageSource, float pDamageAmount, CallbackInfo ci) {
        // 重置标记
        disgusted_Creature$doubleDamage = false;
    }
    
    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageForDouble(float originalDamage, DamageSource source, float damageAmount) {
        // 只检查标记变量，避免重复检测效果
        if (disgusted_Creature$doubleDamage) {
            // 将伤害翻倍
            return originalDamage * 2.0f;
        }
        return originalDamage;
    }
}
