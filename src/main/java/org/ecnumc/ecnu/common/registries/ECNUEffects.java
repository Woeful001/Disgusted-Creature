package org.ecnumc.ecnu.common.registries;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ecnumc.ecnu.common.effects.BanEffect;
import org.ecnumc.ecnu.common.effects.ItchingEffect;
import org.ecnumc.ecnu.common.effects.RilingEffect;
import org.ecnumc.ecnu.common.effects.EvilEffect;
import org.ecnumc.ecnu.common.effects.DisgustedEffect;

import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * 自定义效果注册类
 */
public final class ECNUEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    /**
     * 禁用效果 - 禁用盔甲槽位
     */
    public static final RegistryObject<MobEffect> BAN = EFFECTS.register("ban", BanEffect::new);

    /**
     * 瘙痒效果 - 基于禁用效果，额外提供力量和急迫效果
     */
    public static final RegistryObject<MobEffect> ITCHING = EFFECTS.register("itching", ItchingEffect::new);

    /**
     * 惹怒效果 - 受到的伤害必定为暴击
     */
    public static final RegistryObject<MobEffect> RILING = EFFECTS.register("riling", RilingEffect::new);

    /**
     * 阴暗效果 - 造成伤害的部分转化为生命值恢复
     */
    public static final RegistryObject<MobEffect> Evil = EFFECTS.register("evil", EvilEffect::new);

    /**
     * 作呕效果 - 复合效果
     */
    public static final RegistryObject<MobEffect> DISGUSTED = EFFECTS.register("disgusted", DisgustedEffect::new);

    private ECNUEffects() {
    }
    /**
     * 注册效果
     * @param modBus 模组事件总线
     */
    public static void init(IEventBus modBus) {
        EFFECTS.register(modBus);
    }
}