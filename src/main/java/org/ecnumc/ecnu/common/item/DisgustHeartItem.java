package org.ecnumc.ecnu.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.ecnumc.ecnu.common.player.DisgustedHeartBuffHandler;
import org.jetbrains.annotations.NotNull;

public class DisgustHeartItem extends Item {
    public DisgustHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            DisgustedHeartBuffHandler.grantPermanentBuff(player);
        }
        return result;
    }
}
