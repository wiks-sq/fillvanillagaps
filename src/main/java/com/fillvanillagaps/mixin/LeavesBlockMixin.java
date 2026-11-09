package com.fillvanillagaps.mixin;

import com.fillvanillagaps.FvgConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fast leaf decay: leaves cut off from their log decay within half a second instead of waiting for random ticks. */
@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin {
	@Shadow
	protected abstract boolean decaying(BlockState state);

	@Shadow
	private static BlockState updateDistance(BlockState state, LevelAccessor level, BlockPos pos) {
		throw new AssertionError();
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void fillvanillagaps$decayNow(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (!FvgConfig.get().fastLeafDecay || !this.decaying(state)) {
			return;
		}
		if (updateDistance(state, level, pos).getValue(LeavesBlock.DISTANCE) < LeavesBlock.DECAY_DISTANCE) {
			return; // a log came back since the decay was scheduled
		}
		Block.dropResources(state, level, pos);
		level.removeBlock(pos, false);
		ci.cancel();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void fillvanillagaps$scheduleDecay(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (!FvgConfig.get().fastLeafDecay) {
			return;
		}
		BlockState updated = level.getBlockState(pos);
		if (updated.getBlock() == (Object) this && this.decaying(updated)) {
			level.scheduleTick(pos, (Block) (Object) this, 2 + random.nextInt(9));
		}
	}
}
