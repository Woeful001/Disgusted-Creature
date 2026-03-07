package org.ecnumc.ecnu.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.ecnumc.ecnu.client.models.MosquitoModel;
import org.ecnumc.ecnu.client.models.NullsetModel;
import org.ecnumc.ecnu.client.renderers.MosquitoRenderer;
import org.ecnumc.ecnu.client.renderers.NullsetRenderer;
import org.ecnumc.ecnu.common.registries.ECNUEntityTypes;

import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * Mod Client
 * @author liudongyu
 */
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ECNUClient {
	/**
	 * Register layer definitions for mod entities.
	 * @param event	register layer definitions event
	 */
	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ECNULayerLocations.NULLSET, NullsetModel::createBodyLayer);
		event.registerLayerDefinition(ECNULayerLocations.MOSQUITO, MosquitoModel::createBodyLayer);
	}

	/**
	 * Register renderers for mod entities.
	 * @param event	register renderers event
	 */
	@SubscribeEvent
	public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
		// 延迟到事件触发时才获取RegistryObject的值
		event.registerEntityRenderer(ECNUEntityTypes.NULLSET.get(), NullsetRenderer::new);
		event.registerEntityRenderer(ECNUEntityTypes.MOSQUITO.get(), MosquitoRenderer::new);
	}

	private ECNUClient() {
	}
}
