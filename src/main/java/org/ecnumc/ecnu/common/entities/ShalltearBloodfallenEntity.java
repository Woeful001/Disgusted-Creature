package org.ecnumc.ecnu.common.entities;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.ecnumc.ecnu.common.registries.ECNUEntityTypes;
import org.ecnumc.ecnu.common.registries.ECNUEffects;
import org.ecnumc.ecnu.common.registries.ECNUItems;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * 人形 Boss: Shalltear Bloodfallen
 */
public class ShalltearBloodfallenEntity extends PathfinderMob {
	private static final String SECOND_PHASE_TAG = "SecondPhase";
	private static final double HOVER_HEIGHT = 1.0D;
	private static final double HOVER_SMOOTHING = 0.18D;
	private static final double MAX_VERTICAL_SPEED = 0.2D;
	private static final double COMBAT_HOVER_SMOOTHING = 0.48D;
	private static final double COMBAT_MAX_VERTICAL_SPEED = 0.55D;
	private static final double COMBAT_VERTICAL_OFFSET = 0.12D;
	private static final double PHASE_ONE_CHASE_SPEED = 1.05D;
	private static final double PHASE_TWO_CHASE_SPEED = 1.30D; // 调整：降低二阶段移动速度 (原 1.50)
	private static final double WANDER_SPEED = 1.25D;
	private static final double PHASE_ONE_ATTACK_SPEED = 0.95D;
	private static final double PHASE_TWO_ATTACK_SPEED = 2.2D;
	private static final double PHASE_ONE_FLYING_SPEED = 0.38D;
	private static final double PHASE_TWO_FLYING_SPEED = 0.45D; // 调整：降低二阶段飞行速度 (原 0.55)
	private static final double PHASE_ONE_FOLLOW_RANGE = 48.0D;
	private static final double PHASE_TWO_FOLLOW_RANGE = 96.0D;
	private static final int PERMANENT_EFFECT_DURATION = Integer.MAX_VALUE;
	private static final int PLAYER_DEBUFF_DURATION = 200;
	private static final int PLAYER_DEBUFF_AMPLIFIER = 0;
	private static final float PHASE_ONE_LIFE_STEAL_RATIO = 0.50F;
	private static final float PHASE_TWO_LIFE_STEAL_RATIO = 0.80F;
	private boolean secondPhase;
	private boolean pendingEquipmentSync = true;
	private final ServerBossEvent bossEvent = new ServerBossEvent(
			Component.translatable("entity.disgusted_creature.shalltear_bloodfallen"),
			BossEvent.BossBarColor.RED,
			BossEvent.BossBarOverlay.PROGRESS
	);

	public ShalltearBloodfallenEntity(Level level) {
		super(ECNUEntityTypes.SHALLTEAR_BLOODFALLEN.get(), level);
		initFlightBehavior();
		applyPhaseAttributes();
	}

	public ShalltearBloodfallenEntity(EntityType<? extends ShalltearBloodfallenEntity> entityType, Level level) {
		super(entityType, level);
		initFlightBehavior();
		applyPhaseAttributes();
	}

	private void initFlightBehavior() {
		this.moveControl = new FlyingMoveControl(this, 20, true);
		this.setNoGravity(true);
		this.setPersistenceRequired();
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
		navigation.setCanFloat(true);
		navigation.setCanOpenDoors(false);
		navigation.setCanPassDoors(true);
		return navigation;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		// 调整：提高举盾优先级 (1)，使其优于攻击逻辑，同时确保MOVE flag互斥能打断移动
		this.goalSelector.addGoal(1, new ShieldBlockingGoal(this));
		this.goalSelector.addGoal(2, new AggressiveMeleeAttackGoal(this, PHASE_TWO_CHASE_SPEED, true));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, WANDER_SPEED) {
			@Override
			public boolean canUse() {
				return ShalltearBloodfallenEntity.this.getTarget() == null && super.canUse();
			}

			@Override
			public boolean canContinueToUse() {
				return ShalltearBloodfallenEntity.this.getTarget() == null && super.canContinueToUse();
			}
		});
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 500.0D)
				.add(Attributes.ATTACK_DAMAGE, 12.0D)
				.add(Attributes.ATTACK_SPEED, PHASE_TWO_ATTACK_SPEED)
				.add(Attributes.ARMOR, 12.0D)
				.add(Attributes.ARMOR_TOUGHNESS, 8.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 5.0D)
				.add(Attributes.FLYING_SPEED, PHASE_ONE_FLYING_SPEED)
				.add(Attributes.FOLLOW_RANGE, PHASE_ONE_FOLLOW_RANGE);
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		if (this.pendingEquipmentSync) {
			this.applyPhaseEquipment(false);
			this.pendingEquipmentSync = false;
		}
		this.ensurePhaseEquipmentConsistency();
		this.setNoGravity(true);
		this.ensurePermanentEffects();
		this.tryEnterSecondPhase();
		LivingEntity target = this.getTarget();
		if (target != null && target.isAlive()) {
			this.maintainCombatHover(target);
		} else {
			this.maintainStableHover();
		}
		this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
		this.bossEvent.setName(this.getDisplayName());
	}

	private void ensurePhaseEquipmentConsistency() {
		if (this.secondPhase) {
			if (!this.getOffhandItem().isEmpty()) {
				this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
			}
		} else if (!this.getOffhandItem().is(Items.SHIELD)) {
			this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
		}
	}

	private void tryEnterSecondPhase() {
		if (!this.secondPhase && this.getHealth() <= this.getMaxHealth() * 0.5F) {
			this.secondPhase = true;
			this.pendingEquipmentSync = false;
			this.applyPhaseState(true);
		}
	}

	public boolean isSecondPhase() {
		return this.secondPhase;
	}

	public float getLifeStealRatio() {
		return this.secondPhase ? PHASE_TWO_LIFE_STEAL_RATIO : PHASE_ONE_LIFE_STEAL_RATIO;
	}

	private void applyPhaseState(boolean playTransitionEffects) {
		this.applyPhaseAttributes();
		this.applyPhaseEquipment(playTransitionEffects);
	}

	private void applyPhaseAttributes() {
		this.setBaseAttribute(Attributes.ATTACK_SPEED, this.secondPhase ? PHASE_TWO_ATTACK_SPEED : PHASE_ONE_ATTACK_SPEED);
		this.setBaseAttribute(Attributes.FLYING_SPEED, this.secondPhase ? PHASE_TWO_FLYING_SPEED : PHASE_ONE_FLYING_SPEED);
		this.setBaseAttribute(Attributes.FOLLOW_RANGE, this.secondPhase ? PHASE_TWO_FOLLOW_RANGE : PHASE_ONE_FOLLOW_RANGE);
	}

	private void setBaseAttribute(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
		AttributeInstance instance = this.getAttribute(attribute);
		if (instance != null) {
			instance.setBaseValue(value);
		}
	}

	private void applyPhaseEquipment(boolean playTransitionEffects) {
		this.setItemSlot(EquipmentSlot.MAINHAND, ECNUItems.DISGUSTED_NETHERITE_SWORD.get().getDefaultInstance());
		this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
		this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
		if (this.secondPhase) {
			boolean hadShield = !this.getOffhandItem().isEmpty();
			this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
			if (playTransitionEffects && hadShield) {
				this.playSound(SoundEvents.SHIELD_BREAK, 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
			}
		} else {
			this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
		}
	}


	private void ensurePermanentEffects() {
		if (!this.hasEffect(ECNUEffects.Evil.get())) {
			this.addEffect(new MobEffectInstance(ECNUEffects.Evil.get(), PERMANENT_EFFECT_DURATION, 0, false, false, false));
		}
	}

	private void maintainStableHover() {
		double targetY = findHoverTargetY();
		if (Double.isNaN(targetY)) {
			return;
		}

		double deltaY = targetY - this.getY();
		Vec3 movement = this.getDeltaMovement();
		if (Math.abs(deltaY) < 0.05D) {
			this.setDeltaMovement(movement.x, 0.0D, movement.z);
			return;
		}

		double verticalSpeed = Mth.clamp(deltaY * HOVER_SMOOTHING, -MAX_VERTICAL_SPEED, MAX_VERTICAL_SPEED);
		this.setDeltaMovement(movement.x, verticalSpeed, movement.z);
	}

	private void maintainCombatHover(LivingEntity target) {
		// 调整：减少二阶段战斗时的垂直偏移，防止飞得太高打不到玩家
		double targetY = target.getY() + target.getBbHeight() * 0.68D + COMBAT_VERTICAL_OFFSET;
		if (this.distanceToSqr(target) < 16.0D) {
			targetY -= 0.35D;
		}

		double deltaY = targetY - this.getY();
		Vec3 movement = this.getDeltaMovement();
		if (Math.abs(deltaY) < 0.02D) {
			this.setDeltaMovement(movement.x, 0.0D, movement.z);
			return;
		}

		double verticalSpeed = Mth.clamp(deltaY * COMBAT_HOVER_SMOOTHING, -COMBAT_MAX_VERTICAL_SPEED, COMBAT_MAX_VERTICAL_SPEED);
		this.setDeltaMovement(movement.x, verticalSpeed, movement.z);
	}

	private double findHoverTargetY() {
		for (int y = Mth.floor(this.getY()); y >= this.level().getMinBuildHeight(); y--) {
			if (!this.level().getBlockState(this.blockPosition().atY(y)).isAir()) {
				return y + 1.0D + HOVER_HEIGHT;
			}
		}
		return Double.NaN;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.secondPhase && source.is(DamageTypeTags.IS_PROJECTILE)) {
			return false;
		}
		if (source.is(DamageTypeTags.IS_FIRE)) {
			amount *= 2.0F;
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	public void startSeenByPlayer(ServerPlayer serverPlayer) {
		super.startSeenByPlayer(serverPlayer);
		this.bossEvent.addPlayer(serverPlayer);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer serverPlayer) {
		super.stopSeenByPlayer(serverPlayer);
		this.bossEvent.removePlayer(serverPlayer);
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	public void setCustomName(@Nullable Component name) {
		super.setCustomName(name);
		this.bossEvent.setName(this.getDisplayName());
	}

	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.RIGHT;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		this.swing(InteractionHand.MAIN_HAND, true);
		LivingEntity livingTarget = target instanceof LivingEntity living ? living : null;
		float targetHealthBefore = livingTarget != null ? livingTarget.getHealth() : 0.0F;
		float targetAbsorptionBefore = livingTarget != null ? livingTarget.getAbsorptionAmount() : 0.0F;
		boolean hurt = super.doHurtTarget(target);
		if (hurt) {
			this.healFromSuccessfulAttack(livingTarget, targetHealthBefore, targetAbsorptionBefore);
			if (this.secondPhase && target instanceof Player player) {
				player.addEffect(new MobEffectInstance(ECNUEffects.ITCHING.get(), PLAYER_DEBUFF_DURATION, PLAYER_DEBUFF_AMPLIFIER, false, true));
				player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, PLAYER_DEBUFF_DURATION, PLAYER_DEBUFF_AMPLIFIER, false, true));
			}
		}
		return hurt;
	}

	private void healFromSuccessfulAttack(@Nullable LivingEntity target, float healthBefore, float absorptionBefore) {
		if (target == null) {
			return;
		}

		float totalBefore = healthBefore + absorptionBefore;
		float totalAfter = target.getHealth() + target.getAbsorptionAmount();
		float actualDamageDealt = Math.max(0.0F, totalBefore - totalAfter);
		if (actualDamageDealt > 0.0F) {
			this.heal(actualDamageDealt * this.getLifeStealRatio());
		}
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
		super.dropCustomDeathLoot(source, looting, recentlyHit);
		if (!this.level().isClientSide) {
			this.spawnAtLocation(ECNUItems.DISGUSTED_HEART.get());
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean(SECOND_PHASE_TAG, this.secondPhase);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		boolean hasSavedPhase = tag.contains(SECOND_PHASE_TAG);
		boolean healthIndicatesPhaseTwo = this.getHealth() <= this.getMaxHealth() * 0.5F;
		this.secondPhase = hasSavedPhase ? tag.getBoolean(SECOND_PHASE_TAG) : healthIndicatesPhaseTwo;
		this.applyPhaseAttributes();
		this.pendingEquipmentSync = true;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
										@Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag dataTag) {
		SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, dataTag);
		this.applyPhaseState(false);
		this.pendingEquipmentSync = false;
		this.ensurePermanentEffects();
		return data;
	}

	private static class AggressiveMeleeAttackGoal extends MeleeAttackGoal {
		private static final double FAR_PRESSURE_DISTANCE_SQR = 144.0D;
		private static final double MID_BURST_MIN_DISTANCE_SQR = 25.0D;
		private static final double MID_BURST_MAX_DISTANCE_SQR = 196.0D;
		private static final double MAX_FRENZY_HORIZONTAL_SPEED = 1.05D;
		private static final double PREFERRED_DISTANCE = 2.0D;
		private static final double DISTANCE_TOLERANCE = 0.3D;
		private static final double INNER_BAND_SQR = (PREFERRED_DISTANCE - DISTANCE_TOLERANCE) * (PREFERRED_DISTANCE - DISTANCE_TOLERANCE);
		private static final double OUTER_BAND_SQR = (PREFERRED_DISTANCE + DISTANCE_TOLERANCE) * (PREFERRED_DISTANCE + DISTANCE_TOLERANCE);
		private static final double RETREAT_DISTANCE = 1.4D;
		private static final int BURST_COOLDOWN_TICKS = 12;
		private static final int PHASE_ONE_ATTACK_INTERVAL = 18;
		private static final int PHASE_TWO_ATTACK_INTERVAL = 4;
		private static final float PHASE_ONE_REACH_SCALE = 2.2F;
		private static final float PHASE_TWO_REACH_SCALE = 3.2F;
		private static final float PHASE_ONE_TARGET_WIDTH_BONUS = 0.2F;
		private static final float PHASE_TWO_TARGET_WIDTH_BONUS = 0.5F;
		private static final double EXTRA_ATTACK_RANGE = 2.0D;
		private final ShalltearBloodfallenEntity boss;
		private int burstCooldown;

		public AggressiveMeleeAttackGoal(ShalltearBloodfallenEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
			super(mob, speedModifier, followingTargetEvenIfNotSeen);
			this.boss = mob;
		}

		@Override
		public void tick() {
			if (this.burstCooldown > 0) {
				this.burstCooldown--;
			}

			super.tick();

			LivingEntity target = this.boss.getTarget();
			if (target == null) {
				return;
			}

			double distanceSqr = this.boss.distanceToSqr(target);
			boolean secondPhase = this.boss.isSecondPhase();
			this.boss.getLookControl().setLookAt(target, secondPhase ? 40.0F : 24.0F, secondPhase ? 40.0F : 24.0F);

			if (distanceSqr < INNER_BAND_SQR) {
				this.retreatFromTarget(target, secondPhase);
			} else if (distanceSqr <= OUTER_BAND_SQR) {
				this.boss.getNavigation().stop();
			} else {
				double chaseSpeed = secondPhase ? PHASE_TWO_CHASE_SPEED : PHASE_ONE_CHASE_SPEED;
				double bonusSpeed = secondPhase
						? (distanceSqr > FAR_PRESSURE_DISTANCE_SQR ? 0.60D : distanceSqr > 36.0D ? 0.30D : 0.12D)
						: (distanceSqr > 49.0D ? 0.16D : 0.04D);
				this.boss.getNavigation().moveTo(target, chaseSpeed + bonusSpeed);
			}

			if (secondPhase && distanceSqr > OUTER_BAND_SQR) {
				this.applyBerserkMotion(target, distanceSqr);
			}
		}

		private void retreatFromTarget(LivingEntity target, boolean secondPhase) {
			Vec3 away = new Vec3(this.boss.getX() - target.getX(), 0.0D, this.boss.getZ() - target.getZ());
			if (away.lengthSqr() < 1.0E-5D) {
				return;
			}

			Vec3 retreatDirection = away.normalize();
			Vec3 retreatPoint = this.boss.position().add(retreatDirection.scale(RETREAT_DISTANCE));
			double retreatSpeed = secondPhase ? PHASE_TWO_CHASE_SPEED : PHASE_ONE_CHASE_SPEED;
			this.boss.getNavigation().moveTo(retreatPoint.x, this.boss.getY(), retreatPoint.z, retreatSpeed);
		}

		private void applyBerserkMotion(LivingEntity target, double distanceSqr) {
			Vec3 toTarget = new Vec3(
					target.getX() - this.boss.getX(),
					target.getEyeY() - this.boss.getEyeY(),
					target.getZ() - this.boss.getZ()
			);
			if (toTarget.lengthSqr() < 1.0E-5D) {
				return;
			}

			Vec3 direction = toTarget.normalize();
			Vec3 movement = this.boss.getDeltaMovement();
			double horizontalPush = distanceSqr > 64.0D ? 0.28D : distanceSqr > 16.0D ? 0.18D : 0.10D;
			double verticalPush = distanceSqr > 16.0D ? 0.08D : 0.03D;
			Vec3 extraMotion = new Vec3(direction.x * horizontalPush, direction.y * verticalPush, direction.z * horizontalPush);

			if (this.burstCooldown <= 0 && distanceSqr >= MID_BURST_MIN_DISTANCE_SQR && distanceSqr <= MID_BURST_MAX_DISTANCE_SQR) {
				extraMotion = extraMotion.scale(1.8D);
				this.burstCooldown = BURST_COOLDOWN_TICKS;
			}

			Vec3 nextMovement = movement.add(extraMotion);
			double horizontalSpeed = Math.sqrt(nextMovement.x * nextMovement.x + nextMovement.z * nextMovement.z);
			if (horizontalSpeed > MAX_FRENZY_HORIZONTAL_SPEED) {
				double scale = MAX_FRENZY_HORIZONTAL_SPEED / horizontalSpeed;
				nextMovement = new Vec3(nextMovement.x * scale, nextMovement.y, nextMovement.z * scale);
			}

			this.boss.setDeltaMovement(nextMovement.x, Mth.clamp(nextMovement.y, -0.65D, 0.65D), nextMovement.z);
		}

		@Override
		protected void checkAndPerformAttack(LivingEntity target, double distToEnemySqr) {
			if (distToEnemySqr <= this.getAttackReachSqr(target) && this.isTimeToAttack()) {
				this.resetAttackCooldown();
				this.mob.doHurtTarget(target);
			}
		}

		@Override
		protected double getAttackReachSqr(LivingEntity target) {
			boolean secondPhase = this.boss.isSecondPhase();
			float width = this.mob.getBbWidth() * (secondPhase ? PHASE_TWO_REACH_SCALE : PHASE_ONE_REACH_SCALE)
					+ target.getBbWidth() * (secondPhase ? PHASE_TWO_TARGET_WIDTH_BONUS : PHASE_ONE_TARGET_WIDTH_BONUS);
			double baseReachSqr = width * width + target.getBbWidth();
			double expandedReach = Math.sqrt(baseReachSqr) + EXTRA_ATTACK_RANGE;
			return expandedReach * expandedReach;
		}

		@Override
		protected int getAttackInterval() {
			return this.boss.isSecondPhase() ? PHASE_TWO_ATTACK_INTERVAL : PHASE_ONE_ATTACK_INTERVAL;
		}
	}

	private static class ShieldBlockingGoal extends Goal {
		private final ShalltearBloodfallenEntity mob;
		private int blockDuration;
		private int cooldown;

		public ShieldBlockingGoal(ShalltearBloodfallenEntity mob) {
			this.mob = mob;
			// 优化：举盾时禁止移动，让动作更真实，防止滑步。
			// 这会自动抑制优先级较低的移动目标。
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			if (this.cooldown > 0) {
				this.cooldown--;
			}
			double distSqr = this.mob.getTarget() != null ? this.mob.distanceToSqr(this.mob.getTarget()) : 0;
			return !this.mob.isSecondPhase()
					&& this.mob.isHolding(Items.SHIELD)
					&& this.mob.getTarget() != null
					// 优化：距离太远（>8格）不举盾
					&& distSqr <= 64.0D
					&& this.cooldown <= 0
					&& this.mob.getRandom().nextInt(10) == 0;
		}

		@Override
		public boolean canContinueToUse() {
			return !this.mob.isSecondPhase()
					&& this.mob.isHolding(Items.SHIELD)
					&& this.mob.getTarget() != null
					&& this.blockDuration > 0;
		}

		@Override
		public void start() {
			this.blockDuration = 40 + this.mob.getRandom().nextInt(40);
			this.mob.startUsingItem(InteractionHand.OFF_HAND);
			// 举盾开始时停止移动
			this.mob.getNavigation().stop();
		}

		@Override
		public void stop() {
			this.mob.stopUsingItem();
			this.cooldown = 40 + this.mob.getRandom().nextInt(40);
		}

		@Override
		public void tick() {
			this.blockDuration--;
			if (this.mob.getTarget() != null) {
				this.mob.getLookControl().setLookAt(this.mob.getTarget(), 30.0F, 30.0F);
			}
		}
	}
}

