package org.ecnumc.ecnu.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.ecnumc.ecnu.common.entities.ShalltearBloodfallenEntity;
import org.ecnumc.ecnu.common.registries.ECNUEntityTypes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 恶心的蛋方块，会在满足条件时召唤 Shalltear Bloodfallen。
 * 同时保留原版龙蛋的瞬移和重力掉落行为。
 */
public class DisgustedEggBlock extends DragonEggBlock {
	private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 13.0D, 13.0D);
	private static final int CHECK_INTERVAL_TICKS = 20;
	private static final int SUMMON_TIME_TICKS = 20 * 60;
	private static final int HORIZONTAL_RADIUS = 1;
	private static final int HEIGHT = 5;
	private static final Map<SummonKey, Integer> SUMMON_PROGRESS = new ConcurrentHashMap<>();

	public DisgustedEggBlock() {
		super(Properties.copy(Blocks.DRAGON_EGG).noOcclusion());
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!level.isClientSide) {
			startChecking(level, pos);
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		if (!level.isClientSide) {
			startChecking(level, pos);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			clearProgress(level, pos);
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);

		SummonKey key = new SummonKey(level.dimension(), pos.immutable());
		if (!level.getBlockState(pos).is(this)) {
			SUMMON_PROGRESS.remove(key);
			return;
		}

		if (!isSummonSiteValid(level, pos)) {
			SUMMON_PROGRESS.remove(key);
			level.scheduleTick(pos, this, CHECK_INTERVAL_TICKS);
			return;
		}

		int progress = SUMMON_PROGRESS.getOrDefault(key, 0) + CHECK_INTERVAL_TICKS;
		if (progress >= SUMMON_TIME_TICKS) {
			if (spawnBoss(level, pos)) {
				SUMMON_PROGRESS.remove(key);
				level.removeBlock(pos, false);
				return;
			}
			progress = SUMMON_TIME_TICKS - CHECK_INTERVAL_TICKS;
		}

		SUMMON_PROGRESS.put(key, progress);
		level.scheduleTick(pos, this, CHECK_INTERVAL_TICKS);
	}

	private void startChecking(Level level, BlockPos pos) {
		clearProgress(level, pos);
		level.scheduleTick(pos, this, CHECK_INTERVAL_TICKS);
	}

	private void clearProgress(Level level, BlockPos pos) {
		SUMMON_PROGRESS.remove(new SummonKey(level.dimension(), pos.immutable()));
	}

	private boolean isSummonSiteValid(ServerLevel level, BlockPos eggPos) {
		return level.getBlockState(eggPos.below()).is(Blocks.MAGMA_BLOCK) && isSummonAreaClear(level, eggPos);
	}

	/**
	 * 以蛋所在层为底面，向上检查 3x3x5 空间是否为空。
	 * 这样既允许蛋放在岩浆块上，也能保证 Boss 有完整站立空间。
	 */
	private boolean isSummonAreaClear(ServerLevel level, BlockPos eggPos) {
		for (int offsetX = -HORIZONTAL_RADIUS; offsetX <= HORIZONTAL_RADIUS; offsetX++) {
			for (int offsetZ = -HORIZONTAL_RADIUS; offsetZ <= HORIZONTAL_RADIUS; offsetZ++) {
				for (int offsetY = 0; offsetY < HEIGHT; offsetY++) {
					BlockPos checkPos = eggPos.offset(offsetX, offsetY, offsetZ);
					if (checkPos.equals(eggPos)) {
						continue;
					}
					if (!level.getBlockState(checkPos).isAir()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean spawnBoss(ServerLevel level, BlockPos pos) {
		ShalltearBloodfallenEntity boss = ECNUEntityTypes.SHALLTEAR_BLOODFALLEN.get().create(level);
		if (boss == null) {
			return false;
		}

		boss.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
		boss.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
		boss.setPersistenceRequired();
		level.addFreshEntity(boss);
		level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 40, 0.45D, 0.8D, 0.45D, 0.02D);
		level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 24, 0.35D, 0.6D, 0.35D, 0.01D);
		level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0F, 0.85F);
		return true;
	}

	private record SummonKey(ResourceKey<Level> dimension, BlockPos pos) {
	}
}
