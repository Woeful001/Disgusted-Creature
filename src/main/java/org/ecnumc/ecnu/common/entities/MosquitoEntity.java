package org.ecnumc.ecnu.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.AABB;
import org.ecnumc.ecnu.common.registries.ECNUEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MosquitoEntity extends Bee {
    private static final Logger LOGGER = LoggerFactory.getLogger(MosquitoEntity.class);
    private int attackCooldown = 0;
    private static final int ATTACK_COOLDOWN_TICKS = 1200; // 攻击冷却
    private static final double FLYING_HEIGHT = 1.0D; // 离地高度

    // 简化生成检查方法，避免复杂逻辑导致的注册错误
    public static boolean checkMobSpawnRules(EntityType<MosquitoEntity> entityType, ServerLevelAccessor level, 
                                           MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        // 基本检查
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        
        if (!level.getLevel().dimension().equals(Level.OVERWORLD)) {
            return false;
        }
        
        // 简单的高度检查
        if (pos.getY() < 60 || pos.getY() > 200) {
            return false;
        }

        // 简单的光照检查
        if (level.getBrightness(LightLayer.SKY, pos) > 12) {
            return false;
        }

        // 基本的碰撞检查
        AABB spawnBox = entityType.getDimensions().makeBoundingBox(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (!level.noCollision(spawnBox)) {
            return false;
        }

        return true;
    }
    
    public MosquitoEntity(EntityType<? extends MosquitoEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        // 使用蜜蜂的基础AI，然后添加我们自定义的行为
        super.registerGoals();

        // 移除蜜蜂的授粉相关目标
        // 修复：使用正确的API方法
        this.goalSelector.getAvailableGoals().removeIf(goal ->
            goal.getGoal() instanceof BreedGoal || 
            goal.getGoal() instanceof TemptGoal || 
            goal.getGoal() instanceof FollowParentGoal
        );

        // 添加基础AI目标来改善行为
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        // 添加蚊子特有的攻击行为（提高优先级）
        this.goalSelector.addGoal(1, new MosquitoAttackGoal(this, 1.5D, true)); // 增加移动速度

        // 攻击所有生物（包括玩家）- 增加搜索范围
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 20, true, false,
                target -> !(target instanceof MosquitoEntity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.1D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                // 添加攻击范围属性
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D); // 无击退，保持接近目标
    }

    @Override
    public void tick() {
        super.tick();
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        
        // 保持离地1格高度飞行
        this.maintainFlyingHeight();
    }

    /**
     * 保持离地1格高度飞行
     */
    private void maintainFlyingHeight() {
        if (!this.level().isClientSide && this.isAlive()) {
            BlockPos groundPos = BlockPos.containing(this.getX(), this.getY() - 1.0D, this.getZ());
            
            // 检查下方是否有固体方块
            if (!this.level().getBlockState(groundPos).isAir()) {
                // 如果离地高度不是1格，则调整Y坐标
                double targetY = groundPos.getY() + 1.0D + FLYING_HEIGHT;
                if (Math.abs(this.getY() - targetY) > 0.1D) {
                    this.setPos(this.getX(), targetY, this.getZ());
                }
            } else {
                // 如果下方是空气，寻找最近的地面
                this.findAndMaintainFlyingHeight();
            }
        }
    }

    /**
     * 寻找并维持飞行高度
     */
    private void findAndMaintainFlyingHeight() {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        // 向下寻找地面
        for (int y = (int)this.getY(); y >= this.level().getMinBuildHeight(); y--) {
            mutablePos.set((int)this.getX(), y, (int)this.getZ());
            if (!this.level().getBlockState(mutablePos).isAir()) {
                double targetY = y + 1.0D + FLYING_HEIGHT;
                this.setPos(this.getX(), targetY, this.getZ());
                break;
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        // 在AI步骤中也维持飞行高度
        this.maintainFlyingHeight();
    }

    public boolean doHurtTarget(LivingEntity target) {
        // 添加安全检查，防止攻击无效目标
        if (target == null || !target.isAlive() || target == this) {
            return false;
        }
        
        // 移除攻击冷却检查，让AI控制攻击时机
        // 实际造成伤害
        boolean hurtResult = target.hurt(this.damageSources().mobAttack(this), 
            (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        
        if (hurtResult) {
            // 施加瘙痒效果（等级1）
            target.addEffect(new MobEffectInstance(ECNUEffects.ITCHING.get(), 200, 0, false, true));

            // 重置攻击冷却
            attackCooldown = ATTACK_COOLDOWN_TICKS;

            // 播放叮咬音效
            this.playSound(SoundEvents.BEE_POLLINATE, 0.5F, 1.5F);

            return true;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BEE_LOOP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEE_DEATH;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false; // 蚊子免疫摔落伤害
    }

    /**
     * 自定义攻击目标 - 继承蜜蜂的飞行AI
     */
    static class MosquitoAttackGoal extends MeleeAttackGoal {
        private final MosquitoEntity mosquito;

        public MosquitoAttackGoal(MosquitoEntity mosquito, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mosquito, speedModifier, followingTargetEvenIfNotSeen);
            this.mosquito = mosquito;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            // 添加额外的安全检查
            if (enemy == null || !enemy.isAlive() || enemy == this.mob) {
                return;
            }
            
            // 确保目标不是其他蚊子
            if (enemy instanceof MosquitoEntity) {
                return;
            }

            // 增加攻击范围 - 原来的范围太小
            double attackRange = this.getAttackReachSqr(enemy);
            // 手动增加攻击范围（平方后的距离）
            double extendedRange = attackRange * 2.0; // 增加到原来的2倍
            
            if (distToEnemySqr <= extendedRange && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.mosquito.doHurtTarget(enemy);
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            // 重写攻击范围计算，增加蚊子的攻击距离
            return super.getAttackReachSqr(attackTarget) * 1.5; // 增加50%的攻击范围
        }
    }
}
