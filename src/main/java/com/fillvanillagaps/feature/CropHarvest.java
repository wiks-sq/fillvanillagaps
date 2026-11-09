package com.fillvanillagaps.feature;

import com.fillvanillagaps.FvgConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.TorchflowerCropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;

/** Right-click a fully grown crop to harvest it; one seed from the drops is used to replant. */
public final class CropHarvest {
	private static final int LEVEL_EVENT_BLOCK_BREAK = 2001;

	private CropHarvest() {
	}

	public static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		if (!FvgConfig.get().cropRightClickHarvest || hand != InteractionHand.MAIN_HAND
			|| player.isSpectator() || player.isSecondaryUseActive() || !player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		}
		ItemStack held = player.getMainHandItem();
		if (held.is(Items.BONE_MEAL)) {
			return InteractionResult.PASS;
		}

		BlockPos pos = hit.getBlockPos();
		BlockState state = level.getBlockState(pos);
		BlockState replanted = replantedState(state);
		if (replanted == null || !level.mayInteract(player, pos)) {
			return InteractionResult.PASS;
		}

		if (level instanceof ServerLevel serverLevel) {
			harvest(serverLevel, pos, state, replanted, player, held);
		}
		return InteractionResult.SUCCESS;
	}

	public static void harvest(ServerLevel level, BlockPos pos, BlockState state, BlockState replanted, @Nullable Player player, ItemStack tool) {
		List<ItemStack> drops = Block.getDrops(state, level, pos, null, player, tool);
		Item seed = state.getBlock().asItem();
		for (ItemStack drop : drops) {
			if (drop.is(seed)) {
				drop.shrink(1);
				break;
			}
		}
		level.setBlock(pos, replanted, Block.UPDATE_ALL);
		level.levelEvent(LEVEL_EVENT_BLOCK_BREAK, pos, Block.getId(state));
		for (ItemStack drop : drops) {
			if (!drop.isEmpty()) {
				Block.popResource(level, pos, drop);
			}
		}
		if (player != null) {
			player.causeFoodExhaustion(0.005F);
		}
		level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
	}

	/** The age-0 state to replant with, or null if this block isn't a mature crop. */
	public static @Nullable BlockState replantedState(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof CropBlock crop && !(block instanceof TorchflowerCropBlock)) {
			return crop.isMaxAge(state) ? crop.getStateForAge(0) : null;
		}
		if (block instanceof NetherWartBlock) {
			return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE ? state.setValue(NetherWartBlock.AGE, 0) : null;
		}
		if (block instanceof CocoaBlock) {
			return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE ? state.setValue(CocoaBlock.AGE, 0) : null;
		}
		return null;
	}
}
