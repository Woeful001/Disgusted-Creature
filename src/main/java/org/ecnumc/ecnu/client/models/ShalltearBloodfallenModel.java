package org.ecnumc.ecnu.client.models;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.ecnumc.ecnu.common.entities.ShalltearBloodfallenEntity;

/**
 * Entity Model for Shalltear Bloodfallen
 */
@OnlyIn(Dist.CLIENT)
public class ShalltearBloodfallenModel<T extends ShalltearBloodfallenEntity> extends PlayerModel<T> {
	private static final boolean SLIM = false;

	public ShalltearBloodfallenModel(ModelPart root) {
		super(root, SLIM);
	}

	public static LayerDefinition createBodyLayer() {
		return LayerDefinition.create(createMesh(CubeDeformation.NONE, SLIM), 64, 64);
	}
}

