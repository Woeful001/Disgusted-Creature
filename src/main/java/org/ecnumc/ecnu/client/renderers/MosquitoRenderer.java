package org.ecnumc.ecnu.client.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.ecnumc.ecnu.client.ECNULayerLocations;
import org.ecnumc.ecnu.client.models.MosquitoModel;
import org.ecnumc.ecnu.common.entities.MosquitoEntity;

import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * Mosquito Renderer
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class MosquitoRenderer extends MobRenderer<MosquitoEntity, MosquitoModel<MosquitoEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/entity/mosquito/mosquito.png");

    public MosquitoRenderer(EntityRendererProvider.Context manager) {
        super(manager, new MosquitoModel<>(manager.bakeLayer(ECNULayerLocations.MOSQUITO)), 0.3F);
    }

    /**
     * Get texture for a mosquito entity
     * @param entity	the mosquito entity
     * @return a texture
     */
    @Override
    public ResourceLocation getTextureLocation(MosquitoEntity entity) {
        return TEXTURE;
    }
}
