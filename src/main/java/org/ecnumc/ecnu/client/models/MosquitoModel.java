package org.ecnumc.ecnu.client.models;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.ecnumc.ecnu.ECNUForge;
import org.ecnumc.ecnu.common.entities.MosquitoEntity;

public class MosquitoModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(ECNUForge.MODID, "mosquito"), "main");
	private final ModelPart mosquito;
	private final ModelPart rwing;
	private final ModelPart lwing;
	private final ModelPart legs;
	private final ModelPart eye;
	
	public MosquitoModel(ModelPart root) {
		this.mosquito = root.getChild("mosquito");
		this.rwing = this.mosquito.getChild("rwing");
		this.lwing = this.mosquito.getChild("lwing");
		this.legs = this.mosquito.getChild("legs");
		this.eye = this.mosquito.getChild("eye");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition mosquito = partdefinition.addOrReplaceChild("mosquito", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, -12.0F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
				.texOffs(14, 8).addBox(-1.5F, -12.5F, -6.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 30.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition stomach_r1 = mosquito.addOrReplaceChild("stomach_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, -10.0F, -6.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition mouth_r1 = mosquito.addOrReplaceChild("mouth_r1", CubeListBuilder.create().texOffs(14, 14).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, -9.5F, -3.0F, -0.48F, 0.0F, 0.0F));

		PartDefinition rwing = mosquito.addOrReplaceChild("rwing", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, -5.0F));

		PartDefinition rw_r1 = rwing.addOrReplaceChild("rw_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -0.2119F, -6.0F, 3.0F, 1.0F, 7.0F, new CubeDeformation(-0.3F)).mirror(false), PartPose.offsetAndRotation(1.25F, -0.2881F, -0.9629F, 0.0873F, -0.2618F, 0.0F));

		PartDefinition lwing = mosquito.addOrReplaceChild("lwing", CubeListBuilder.create(), PartPose.offset(-1.25F, -12.2881F, -5.9629F));

		PartDefinition lw_r1 = lwing.addOrReplaceChild("lw_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -0.4619F, -6.0F, 3.0F, 1.0F, 7.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0873F, 0.2618F, 0.0F));

		PartDefinition legs = mosquito.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lb2_r1 = legs.addOrReplaceChild("lb2_r1", CubeListBuilder.create().texOffs(16, 21).addBox(-2.5116F, -0.8272F, -1.7569F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-0.55F, -8.9F, -4.3F, -0.1243F, -0.51F, -0.0985F));

		PartDefinition lb1_r1 = legs.addOrReplaceChild("lb1_r1", CubeListBuilder.create().texOffs(8, 19).addBox(-2.3747F, 0.1927F, -1.366F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-0.75F, -10.5F, -4.5F, -0.2393F, -0.4703F, -0.3351F));

		PartDefinition rb2_r1 = legs.addOrReplaceChild("rb2_r1", CubeListBuilder.create().texOffs(16, 21).mirror().addBox(1.5116F, -0.8272F, -1.7569F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)).mirror(false), PartPose.offsetAndRotation(0.55F, -8.9F, -4.3F, -0.1243F, 0.51F, 0.0985F));

		PartDefinition rb1_r1 = legs.addOrReplaceChild("rb1_r1", CubeListBuilder.create().texOffs(8, 19).mirror().addBox(0.3747F, 0.1927F, -1.366F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.3F)).mirror(false), PartPose.offsetAndRotation(0.75F, -10.5F, -4.5F, -0.2393F, 0.4703F, 0.3351F));

		PartDefinition lm2_r1 = legs.addOrReplaceChild("lm2_r1", CubeListBuilder.create().texOffs(12, 21).mirror().addBox(0.862F, -0.8202F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)).mirror(false), PartPose.offsetAndRotation(0.55F, -8.9F, -4.5F, 0.0F, 0.0F, 0.1309F));

		PartDefinition lm1_r1 = legs.addOrReplaceChild("lm1_r1", CubeListBuilder.create().texOffs(20, 2).mirror().addBox(-0.0784F, -0.0186F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.3F)).mirror(false), PartPose.offsetAndRotation(0.75F, -10.5F, -4.5F, 0.0F, 0.0F, 0.3927F));

		PartDefinition rm2_r1 = legs.addOrReplaceChild("rm2_r1", CubeListBuilder.create().texOffs(12, 21).addBox(-1.862F, -0.8202F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-0.55F, -8.9F, -4.5F, 0.0F, 0.0F, -0.1309F));

		PartDefinition rm1_r1 = legs.addOrReplaceChild("rm1_r1", CubeListBuilder.create().texOffs(20, 2).addBox(-1.9216F, -0.0186F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-0.75F, -10.5F, -4.5F, 0.0F, 0.0F, -0.3927F));

		PartDefinition lf2_r1 = legs.addOrReplaceChild("lf2_r1", CubeListBuilder.create().texOffs(8, 21).addBox(-2.378F, -0.9929F, 0.6688F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-0.55F, -8.9F, -4.5F, 0.0681F, 0.2618F, -0.1207F));

		PartDefinition lf1_r1 = legs.addOrReplaceChild("lf1_r1", CubeListBuilder.create().texOffs(20, 6).addBox(-2.2443F, 0.1405F, 0.4438F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-0.75F, -10.5F, -4.5F, 0.1325F, 0.2618F, -0.3743F));

		PartDefinition rf2_r1 = legs.addOrReplaceChild("rf2_r1", CubeListBuilder.create().texOffs(8, 21).mirror().addBox(1.378F, -0.9929F, 0.6688F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)).mirror(false), PartPose.offsetAndRotation(0.55F, -8.9F, -4.5F, 0.0681F, -0.2618F, 0.1207F));

		PartDefinition rf1_r1 = legs.addOrReplaceChild("rf1_r1", CubeListBuilder.create().texOffs(20, 6).mirror().addBox(0.2443F, 0.1405F, 0.4438F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.3F)).mirror(false), PartPose.offsetAndRotation(0.75F, -10.5F, -4.5F, 0.1325F, -0.2618F, 0.3743F));

		PartDefinition eye = mosquito.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, -13.0F, 0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
				.texOffs(0, 15).addBox(0.0F, -13.0F, 0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 0.0F, -4.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// 保持原有的飞行动画逻辑
		if (entity instanceof MosquitoEntity mosquito) { // 移除 isFlying() 检查，让翅膀始终动画
			// 使用更平滑的动画计算
			float animationTime = (ageInTicks * 3.0F) % Mth.TWO_PI; // 翅膀拍打频率适中
			float wingAngle = Mth.sin(animationTime) * 15.0F; // 减小摆动幅度使动画更自然
			
			// 应用翅膀旋转
			this.rwing.xRot = (float) Math.toRadians(wingAngle);
			this.rwing.yRot = (float) Math.toRadians(-wingAngle * 0.3F); // 减少Y轴旋转
			this.lwing.xRot = (float) Math.toRadians(wingAngle);
			this.lwing.yRot = (float) Math.toRadians(wingAngle * 0.3F);
		} else {
			// 非飞行状态时重置翅膀位置
			this.rwing.xRot = 0.0F;
			this.rwing.yRot = 0.0F;
			this.lwing.xRot = 0.0F;
			this.lwing.yRot = 0.0F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		mosquito.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}