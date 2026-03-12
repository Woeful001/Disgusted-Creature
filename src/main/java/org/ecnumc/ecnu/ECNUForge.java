package org.ecnumc.ecnu;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.ecnumc.ecnu.common.ECNUContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mod Main Class
 * @author liudongyu
 */
@SuppressWarnings("java:S1118")
@Mod(ECNUForge.MODID)
public class ECNUForge {
    public static final String MODID = "disgusted_creature";
    private static final Logger LOGGER = LoggerFactory.getLogger(ECNUForge.class);

    public ECNUForge() {
        LOGGER.info("ECNU模组开始初始化...");
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ECNUContent.modConstruction(modBus);
        LOGGER.info("ECNU模组内容注册完成");

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(this); // 注册当前类以监听Forge事件
        LOGGER.info("ECNU模组初始化完成!");
    }

    @SubscribeEvent
    public void onServerStarting(net.minecraftforge.event.server.ServerStartingEvent event) {
        LOGGER.info("服务器启动，恶心生物模组已加载");
    }
}
