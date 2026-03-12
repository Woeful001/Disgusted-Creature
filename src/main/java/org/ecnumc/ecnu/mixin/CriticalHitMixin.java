package org.ecnumc.ecnu.mixin;

import net.minecraft.world.entity.player.Player;
import org.ecnumc.ecnu.common.registries.ECNUEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 玩家受到伤害翻倍mixin
 * 当玩家有惹怒效果时，受到的所有伤害都会翻倍
 */
@Mixin(Player.class)
public class CriticalHitMixin {

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageForDouble(float originalDamage) {
        Player player = (Player) (Object) this;
        if (player.hasEffect(ECNUEffects.RILING.get()) || player.hasEffect(ECNUEffects.DISGUSTED.get())) {
            return originalDamage * 2.0F;
        }
        return originalDamage;
    }
}
