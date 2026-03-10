package org.ecnumc.ecnu.client.renderers;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.ecnumc.ecnu.client.models.ShalltearBloodfallenModel;
import org.ecnumc.ecnu.common.entities.ShalltearBloodfallenEntity;

import static org.ecnumc.ecnu.ECNUForge.MODID;

/**
 * Shalltear Bloodfallen Renderer
 */
@OnlyIn(Dist.CLIENT)
public class ShalltearBloodfallenRenderer extends MobRenderer<ShalltearBloodfallenEntity, ShalltearBloodfallenModel<ShalltearBloodfallenEntity>> {
	private static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(MODID, "shalltear_bloodfallen"), "main");
	public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/entity/nullset/nullset.png");

	public ShalltearBloodfallenRenderer(EntityRendererProvider.Context manager) {
		super(manager, new ShalltearBloodfallenModel<>(manager.bakeLayer(LAYER)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(ShalltearBloodfallenEntity entity) {
		return TEXTURE;
	}
}
