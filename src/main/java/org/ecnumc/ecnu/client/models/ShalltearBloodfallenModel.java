package org.ecnumc.ecnu.client.models;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.ecnumc.ecnu.common.entities.ShalltearBloodfallenEntity;

/**
 * Entity Model for Shalltear Bloodfallen
 */
@OnlyIn(Dist.CLIENT)
public class ShalltearBloodfallenModel<T extends ShalltearBloodfallenEntity> extends PlayerModel<T> {
	private static final boolean SLIM = true;
	private static final float ATTACK_BLEND_SPEED = 1.8F;

	public ShalltearBloodfallenModel(ModelPart root) {
		super(root, SLIM);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		float playerBodyX = this.body.xRot;
		float playerBodyY = this.body.yRot;
		float playerBodyZ = this.body.zRot;
		float playerHeadY = this.head.yRot;
		float playerHeadZ = this.head.zRot;
		float playerRightArmX = this.rightArm.xRot;
		float playerRightArmY = this.rightArm.yRot;
		float playerRightArmZ = this.rightArm.zRot;
		float playerLeftArmX = this.leftArm.xRot;
		float playerLeftArmY = this.leftArm.yRot;
		float playerLeftArmZ = this.leftArm.zRot;

		float swing = Mth.sin(limbSwing * 0.45F) * limbSwingAmount * 0.18F;
		float sway = Mth.sin(ageInTicks * 0.08F) * 0.04F;
		float hover = Mth.cos(ageInTicks * 0.12F) * 0.03F;
		float attackProgress = Math.max(this.attackTime, entity.getAttackAnim(0.0F));
		if (attackProgress <= 0.0F && entity.swinging) {
			attackProgress = 0.45F;
		}
		float attackBlend = Mth.clamp(attackProgress * ATTACK_BLEND_SPEED, 0.0F, 1.0F);

		float idleRightArmX = 0.70F + swing + hover;
		float idleRightArmY = -0.18F;
		float idleRightArmZ = 0.10F + sway;
		float idleLeftArmX = -0.38F - swing * 0.6F;
		float idleLeftArmY = 0.16F;
		float idleLeftArmZ = -0.12F - sway;
		float idleBodyX = 0.10F + hover;
		float idleBodyY = 0.0F;
		float idleBodyZ = 0.0F;

		this.crouching = false;
		this.body.xRot = Mth.lerp(attackBlend, idleBodyX, playerBodyX);
		this.body.yRot = Mth.lerp(attackBlend, idleBodyY, playerBodyY);
		this.body.zRot = Mth.lerp(attackBlend, idleBodyZ, playerBodyZ);
		this.body.y = 0.0F;

		this.head.yRot = playerHeadY;
		this.head.zRot = Mth.lerp(attackBlend, 0.0F, playerHeadZ);

		this.rightArm.xRot = Mth.lerp(attackBlend, idleRightArmX, playerRightArmX);
		this.rightArm.yRot = Mth.lerp(attackBlend, idleRightArmY, playerRightArmY);
		this.rightArm.zRot = Mth.lerp(attackBlend, idleRightArmZ, playerRightArmZ);
		this.leftArm.xRot = Mth.lerp(attackBlend, idleLeftArmX, playerLeftArmX);
		this.leftArm.yRot = Mth.lerp(attackBlend, idleLeftArmY, playerLeftArmY);
		this.leftArm.zRot = Mth.lerp(attackBlend, idleLeftArmZ, playerLeftArmZ);

		// 双脚略微靠后：这里使用正 xRot，修正之前方向反了的问题。
		this.rightLeg.xRot = 0.48F + swing * 0.45F;
		this.rightLeg.yRot = 0.10F;
		this.rightLeg.zRot = 0.05F;
		this.leftLeg.xRot = 0.34F - swing * 0.35F;
		this.leftLeg.yRot = -0.08F;
		this.leftLeg.zRot = -0.04F;

		this.rightLeg.y = 12.0F + hover * 8.0F;
		this.leftLeg.y = 12.0F + hover * 8.0F;
		this.rightArm.y = 2.0F + hover * 4.0F;
		this.leftArm.y = 2.0F + hover * 4.0F;

		// 同步玩家模型第二层，避免出现一个透明一个正常的重影。
		this.hat.copyFrom(this.head);
		this.jacket.copyFrom(this.body);
		this.leftSleeve.copyFrom(this.leftArm);
		this.rightSleeve.copyFrom(this.rightArm);
		this.leftPants.copyFrom(this.leftLeg);
		this.rightPants.copyFrom(this.rightLeg);
	}

	public static LayerDefinition createBodyLayer() {
		return LayerDefinition.create(createMesh(CubeDeformation.NONE, SLIM), 64, 64);
	}
}
