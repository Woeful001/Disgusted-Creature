package org.ecnumc.ecnu.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DisgustSwordItem extends SwordItem {

    private final float lifeStealRatio;

    public DisgustSwordItem(Tier tier, int attackDamage, float attackSpeed, Properties properties, float lifeStealRatio) {
        super(tier, attackDamage, attackSpeed, properties);
        this.lifeStealRatio = lifeStealRatio;
    }

    public float getLifeStealRatio() {
        return lifeStealRatio;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.disgusted_creature.disgusted_sword.lifesteal", Math.round(lifeStealRatio * 100.0F))
                .withStyle(ChatFormatting.DARK_RED));
    }
}
