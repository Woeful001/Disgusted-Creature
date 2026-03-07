package org.ecnumc.ecnu.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

/**
 * 禁用效果 - 禁用指定数量的盔甲槽位
 */
public class BanEffect extends MobEffect {
    public BanEffect() {
        this(MobEffectCategory.HARMFUL, 0x8B0000); // 默认深红色
    }
    
    public BanEffect(MobEffectCategory category, int color) {
        super(category, color);
        // 注册tick事件来处理装备禁用
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 持续应用
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;
            
            if (player.hasEffect(this)) {
                int level = Objects.requireNonNull(player.getEffect(this)).getAmplifier() + 1;
                
                // 禁用盔甲槽位
                disableArmorSlots(player, level);
            }
        }
    }

    protected void disableArmorSlots(Player player, int level) {
        Level world = player.level();
        
        // 禁用盔甲槽位（从头盔开始）
        for (int i = 0; i < Math.min(level, 4); i++) {
            ItemStack stack = player.getInventory().armor.get(3 - i); // 3是头盔槽位
            if (!stack.isEmpty()) {
                // 尝试将物品添加到背包
                if (!player.getInventory().add(stack)) {
                    // 背包已满，将物品丢出到世界中
                    BlockPos pos = player.blockPosition();
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, stack.copy());
                    itemEntity.setPickUpDelay(40); // 设置拾取延迟
                    world.addFreshEntity(itemEntity);
                    
                    // 播放物品掉落音效
                    world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }
                // 清空盔甲槽位
                player.getInventory().armor.set(3 - i, ItemStack.EMPTY);
            }
        }
    }
}
