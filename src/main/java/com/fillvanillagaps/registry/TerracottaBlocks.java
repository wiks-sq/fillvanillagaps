package com.fillvanillagaps.registry;

import com.fillvanillagaps.FillVanillaGaps;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Slabs and stairs for plain terracotta and all 16 dyed terracottas.
 * 26.3 gave wool and concrete their slabs/stairs; terracotta is the one colored set still missing them.
 */
public final class TerracottaBlocks {
	private static final String[] COLORS = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
		"light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};

	/** Same camera-clipping rule vanilla uses for its slabs and stairs. */
	private static final BlockBehaviour.StateArgumentPredicate<AABB> NEAR_PLANE_INTERSECTS_OUTLINE = (state, level, pos, nearPlane) -> {
		for (AABB box : state.getOcclusionShape().toAabbs()) {
			if (box.move(pos).intersects(nearPlane)) {
				return true;
			}
		}
		return false;
	};

	public static final List<Item> ITEMS = new ArrayList<>();

	private TerracottaBlocks() {
	}

	public static List<String> baseNames() {
		List<String> names = new ArrayList<>();
		names.add("terracotta");
		for (String color : COLORS) {
			names.add(color + "_terracotta");
		}
		return names;
	}

	public static void register() {
		for (String base : baseNames()) {
			Block baseBlock = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(base));
			register(base + "_slab", SlabBlock::new,
				BlockBehaviour.Properties.ofLegacyCopy(baseBlock).isViewBlocking(NEAR_PLANE_INTERSECTS_OUTLINE));
			register(base + "_stairs", props -> new StairBlock(baseBlock.defaultBlockState(), props),
				BlockBehaviour.Properties.ofFullCopy(baseBlock).isViewBlocking(NEAR_PLANE_INTERSECTS_OUTLINE));
		}

		// Show them in the Colored Blocks tab right after the vanilla terracottas.
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COLORED_BLOCKS).register(output -> {
			ItemStack anchor = null;
			for (ItemStack stack : output.getDisplayStacks()) {
				Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
				if (key.getNamespace().equals("minecraft") && key.getPath().endsWith("terracotta") && !key.getPath().contains("glazed")) {
					anchor = stack;
				}
			}
			List<ItemStack> stacks = ITEMS.stream().map(ItemStack::new).toList();
			if (anchor != null) {
				output.insertAfter(anchor, stacks);
			} else {
				stacks.forEach(output::accept);
			}
		});
	}

	private static void register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties props) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, FillVanillaGaps.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(props.setId(blockKey)));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, FillVanillaGaps.id(name));
		BlockItem item = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
		item.registerBlocks(Item.BY_BLOCK, item);
		ITEMS.add(Registry.register(BuiltInRegistries.ITEM, itemKey, item));
	}
}
