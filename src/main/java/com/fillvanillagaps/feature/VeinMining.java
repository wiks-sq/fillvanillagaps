package com.fillvanillagaps.feature;

import com.fillvanillagaps.FvgConfig;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Crouch while breaking a log with an axe to fell the whole tree, or an ore with a pickaxe to mine the whole vein.
 * Every extra block costs durability and hunger like normal mining, and it stops before the tool breaks.
 */
public final class VeinMining {
	private static final int LEVEL_EVENT_BLOCK_BREAK = 2001;
	private static boolean breaking = false;

	private VeinMining() {
	}

	public static void afterBlockBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
		if (breaking || !(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) {
			return;
		}
		if (!player.isShiftKeyDown() || player.getAbilities().instabuild) {
			return;
		}
		FvgConfig config = FvgConfig.get();
		ItemStack tool = player.getMainHandItem();
		List<BlockPos> targets;
		if (config.treeFelling && state.is(BlockTags.LOGS) && tool.is(ItemTags.AXES)) {
			targets = findTree(serverLevel, pos, state.getBlock(), config.treeFellingMaxLogs);
		} else if (config.veinMining && state.is(ConventionalBlockTags.ORES) && tool.is(ItemTags.PICKAXES)
			&& player.hasCorrectToolForDrops(state)) {
			targets = findVein(serverLevel, pos, state.getBlock(), config.veinMiningMaxBlocks);
		} else {
			return;
		}
		breakAll(serverPlayer, serverLevel, targets, tool.getItem());
	}

	private static void breakAll(ServerPlayer player, ServerLevel level, List<BlockPos> targets, Item toolItem) {
		breaking = true;
		try {
			for (BlockPos target : targets) {
				ItemStack tool = player.getMainHandItem();
				if (!tool.is(toolItem) || (tool.isDamageableItem() && tool.getMaxDamage() - tool.getDamageValue() <= 1)) {
					break; // tool swapped, broken, or about to break
				}
				BlockState targetState = level.getBlockState(target);
				if (player.gameMode.destroyBlock(target) && player.connection != null) {
					// The breaking player doesn't get the vanilla particles for blocks they didn't hit directly.
					player.connection.send(new ClientboundLevelEventPacket(LEVEL_EVENT_BLOCK_BREAK, target, Block.getId(targetState), false));
				}
			}
		} finally {
			breaking = false;
		}
	}

	/** Logs of the same type connected to {@code origin} (diagonals included, never below it). Empty unless natural leaves touch them. */
	public static List<BlockPos> findTree(BlockGetter level, BlockPos origin, Block log, int max) {
		List<BlockPos> found = new ArrayList<>();
		Set<BlockPos> visited = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		queue.add(origin);
		visited.add(origin);
		boolean natural = false;
		while (!queue.isEmpty() && found.size() < max) {
			BlockPos current = queue.poll();
			for (BlockPos next : BlockPos.betweenClosed(current.offset(-1, -1, -1), current.offset(1, 1, 1))) {
				if (next.getY() < origin.getY() || visited.contains(next)) {
					continue;
				}
				BlockPos immutable = next.immutable();
				visited.add(immutable);
				BlockState nextState = level.getBlockState(immutable);
				if (nextState.is(log)) {
					found.add(immutable);
					queue.add(immutable);
					if (found.size() >= max) {
						break;
					}
				} else if (!natural && isNaturalLeaves(nextState)) {
					natural = true;
				}
			}
		}
		return natural ? found : List.of();
	}

	/** Ores of the same block connected to {@code origin}, diagonals included. */
	public static List<BlockPos> findVein(BlockGetter level, BlockPos origin, Block ore, int max) {
		List<BlockPos> found = new ArrayList<>();
		Set<BlockPos> visited = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		queue.add(origin);
		visited.add(origin);
		while (!queue.isEmpty() && found.size() < max) {
			BlockPos current = queue.poll();
			for (BlockPos next : BlockPos.betweenClosed(current.offset(-1, -1, -1), current.offset(1, 1, 1))) {
				if (visited.contains(next)) {
					continue;
				}
				BlockPos immutable = next.immutable();
				visited.add(immutable);
				if (level.getBlockState(immutable).is(ore)) {
					found.add(immutable);
					queue.add(immutable);
					if (found.size() >= max) {
						break;
					}
				}
			}
		}
		return found;
	}

	private static boolean isNaturalLeaves(BlockState state) {
		return (state.getBlock() instanceof LeavesBlock && !state.getValue(LeavesBlock.PERSISTENT)) || state.is(BlockTags.WART_BLOCKS);
	}
}
