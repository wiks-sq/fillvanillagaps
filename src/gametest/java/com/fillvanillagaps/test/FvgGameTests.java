package com.fillvanillagaps.test;

import com.fillvanillagaps.FillVanillaGaps;
import com.fillvanillagaps.feature.CropHarvest;
import com.fillvanillagaps.feature.InventorySorter;
import com.fillvanillagaps.feature.VeinMining;
import com.fillvanillagaps.registry.TerracottaBlocks;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Server-side checks for every feature. Run with ./gradlew runGameTest (also part of ./gradlew build). */
public class FvgGameTests {
	private static Holder<Enchantment> enchantment(GameTestHelper helper, ResourceKey<Enchantment> key) {
		return helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
	}

	private static void check(GameTestHelper helper, boolean condition, String message) {
		if (!condition) {
			throw helper.assertionException("test.error.value_not_equal", message, "true", "false");
		}
	}

	private static ServerPlayer survivalPlayer(GameTestHelper helper, Item tool) {
		ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tool));
		player.setShiftKeyDown(true);
		return player;
	}

	@GameTest
	public void terracottaSlabsAndStairsExist(GameTestHelper helper) {
		check(helper, TerracottaBlocks.ITEMS.size() == 34, "34 terracotta slab/stair items");
		for (String base : TerracottaBlocks.baseNames()) {
			for (String suffix : List.of("_slab", "_stairs")) {
				Block block = BuiltInRegistries.BLOCK.getValue(FillVanillaGaps.id(base + suffix));
				check(helper, block != Blocks.AIR, base + suffix + " block registered");
				check(helper, block.asItem() != Items.AIR, base + suffix + " has a block item");
				ResourceKey<Recipe<?>> recipe = ResourceKey.create(Registries.RECIPE, FillVanillaGaps.id(base + suffix));
				check(helper, helper.getLevel().getServer().getRecipeManager().byKey(recipe).isPresent(), base + suffix + " recipe loaded");
			}
		}
		Block slab = BuiltInRegistries.BLOCK.getValue(FillVanillaGaps.id("red_terracotta_slab"));
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, slab.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE));
		List<ItemStack> drops = Block.getDrops(helper.getBlockState(pos), helper.getLevel(), helper.absolutePos(pos), null);
		check(helper, drops.size() == 1 && drops.getFirst().getCount() == 2, "double slab drops two slabs");
		helper.succeed();
	}

	@GameTest
	public void rightClickHarvestReplants(GameTestHelper helper) {
		BlockPos crop = new BlockPos(1, 2, 1);
		helper.setBlock(crop.below(), Blocks.FARMLAND);
		helper.setBlock(crop, ((CropBlock) Blocks.WHEAT).getStateForAge(7));
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		BlockPos abs = helper.absolutePos(crop);
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);

		InteractionResult result = CropHarvest.onUseBlock(player, helper.getLevel(), InteractionHand.MAIN_HAND, hit);
		check(helper, result.consumesAction(), "harvest consumed the click");
		helper.assertBlockState(crop, ((CropBlock) Blocks.WHEAT).getStateForAge(0));
		helper.assertItemEntityPresent(Items.WHEAT, crop, 2.0);

		// Unripe crops are left alone.
		helper.setBlock(crop, ((CropBlock) Blocks.WHEAT).getStateForAge(3));
		check(helper, !CropHarvest.onUseBlock(player, helper.getLevel(), InteractionHand.MAIN_HAND, hit).consumesAction(), "unripe crop ignored");
		helper.succeed();
	}

	@GameTest(maxTicks = 200)
	public void treeFellingAndFastLeafDecay(GameTestHelper helper) {
		BlockPos base = new BlockPos(3, 1, 3);
		for (int y = 0; y < 5; y++) {
			helper.setBlock(base.above(y), Blocks.OAK_LOG);
		}
		BlockPos top = base.above(4);
		BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			helper.setBlock(top.relative(dir), leaves);
			helper.setBlock(top.relative(dir).below(), leaves);
		}
		helper.setBlock(top.above(), leaves);

		ServerPlayer player = survivalPlayer(helper, Items.DIAMOND_AXE);
		BlockState logState = helper.getBlockState(base);
		helper.setBlock(base, Blocks.AIR); // the block the player broke by hand
		VeinMining.afterBlockBreak(helper.getLevel(), player, helper.absolutePos(base), logState, null);

		for (int y = 1; y < 5; y++) {
			helper.assertBlockPresent(Blocks.AIR, base.above(y));
		}
		check(helper, player.getMainHandItem().getDamageValue() == 4, "axe lost 1 durability per extra log");
		helper.assertItemEntityPresent(Items.OAK_LOG);
		// With vanilla random ticks these leaves would take minutes; fast decay clears them in well under 10 seconds.
		helper.succeedWhen(() -> {
			for (Direction dir : Direction.Plane.HORIZONTAL) {
				helper.assertBlockPresent(Blocks.AIR, top.relative(dir));
			}
			helper.assertBlockPresent(Blocks.AIR, top.above());
		});
	}

	@GameTest
	public void treeFellingSparesBuildings(GameTestHelper helper) {
		BlockPos base = new BlockPos(3, 1, 3);
		for (int y = 0; y < 4; y++) {
			helper.setBlock(base.above(y), Blocks.OAK_LOG);
		}
		// Player-placed (persistent) leaves: this is a build, not a tree.
		helper.setBlock(base.above(3).north(), Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true));
		ServerPlayer player = survivalPlayer(helper, Items.DIAMOND_AXE);
		BlockState logState = helper.getBlockState(base);
		helper.setBlock(base, Blocks.AIR);
		VeinMining.afterBlockBreak(helper.getLevel(), player, helper.absolutePos(base), logState, null);
		for (int y = 1; y < 4; y++) {
			helper.assertBlockPresent(Blocks.OAK_LOG, base.above(y));
		}
		helper.succeed();
	}

	@GameTest
	public void veinMiningNeedsCrouchAndRightTool(GameTestHelper helper) {
		BlockPos origin = new BlockPos(2, 2, 2);
		List<BlockPos> vein = List.of(origin, origin.east(), origin.east().above(), origin.north().east()); // includes a diagonal
		vein.forEach(p -> helper.setBlock(p, Blocks.IRON_ORE));

		// Not crouching: nothing extra breaks.
		ServerPlayer standing = survivalPlayer(helper, Items.IRON_PICKAXE);
		standing.setShiftKeyDown(false);
		helper.setBlock(origin, Blocks.AIR);
		VeinMining.afterBlockBreak(helper.getLevel(), standing, helper.absolutePos(origin), Blocks.IRON_ORE.defaultBlockState(), null);
		helper.assertBlockPresent(Blocks.IRON_ORE, origin.east());

		// Wooden pickaxe can't harvest iron: no chain.
		ServerPlayer wooden = survivalPlayer(helper, Items.WOODEN_PICKAXE);
		VeinMining.afterBlockBreak(helper.getLevel(), wooden, helper.absolutePos(origin), Blocks.IRON_ORE.defaultBlockState(), null);
		helper.assertBlockPresent(Blocks.IRON_ORE, origin.east());

		ServerPlayer crouching = survivalPlayer(helper, Items.IRON_PICKAXE);
		VeinMining.afterBlockBreak(helper.getLevel(), crouching, helper.absolutePos(origin), Blocks.IRON_ORE.defaultBlockState(), null);
		for (BlockPos p : vein) {
			helper.assertBlockPresent(Blocks.AIR, p);
		}
		helper.assertItemEntityPresent(Items.RAW_IRON);
		helper.succeed();
	}

	@GameTest
	public void featherFallingStopsTrampling(GameTestHelper helper) {
		BlockPos withBoots = new BlockPos(1, 1, 1);
		BlockPos withoutBoots = new BlockPos(4, 1, 4);
		helper.setBlock(withBoots, Blocks.FARMLAND);
		helper.setBlock(withoutBoots, Blocks.FARMLAND);

		Zombie protectedZombie = helper.spawn(EntityTypes.ZOMBIE, withBoots.above());
		ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
		boots.enchant(enchantment(helper, Enchantments.FEATHER_FALLING), 1);
		protectedZombie.setItemSlot(EquipmentSlot.FEET, boots);
		Zombie plainZombie = helper.spawn(EntityTypes.ZOMBIE, withoutBoots.above());

		Blocks.FARMLAND.fallOn(helper.getLevel(), helper.getBlockState(withBoots), helper.absolutePos(withBoots), protectedZombie, 10.0);
		Blocks.FARMLAND.fallOn(helper.getLevel(), helper.getBlockState(withoutBoots), helper.absolutePos(withoutBoots), plainZombie, 10.0);
		helper.assertBlockPresent(Blocks.FARMLAND, withBoots);
		helper.assertBlockPresent(Blocks.DIRT, withoutBoots);
		helper.succeed();
	}

	@GameTest
	public void anvilIsNeverTooExpensive(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		AnvilMenu menu = new AnvilMenu(1, player.getInventory());
		ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
		sword.set(net.minecraft.core.component.DataComponents.REPAIR_COST, 63); // six previous anvil uses
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		book.enchant(enchantment(helper, Enchantments.SHARPNESS), 5);
		menu.getSlot(0).set(sword);
		menu.getSlot(1).set(book);
		menu.createResult();
		check(helper, menu.getCost() == 39, "cost capped at 39, was " + menu.getCost());
		check(helper, !menu.getSlot(2).getItem().isEmpty(), "result is offered instead of 'Too Expensive!'");
		helper.succeed();
	}

	@GameTest
	public void doubleDoorsOpenTogether(GameTestHelper helper) {
		BlockPos left = new BlockPos(2, 1, 2);
		BlockPos right = left.relative(Direction.NORTH.getClockWise());
		placeDoor(helper, left, DoorHingeSide.LEFT);
		placeDoor(helper, right, DoorHingeSide.RIGHT);

		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		helper.useBlock(left, player);
		check(helper, helper.getBlockState(left).getValue(DoorBlock.OPEN), "clicked door opened");
		check(helper, helper.getBlockState(right).getValue(DoorBlock.OPEN), "partner door opened");
		check(helper, helper.getBlockState(right.above()).getValue(DoorBlock.OPEN), "partner upper half opened");

		player.setShiftKeyDown(true);
		helper.useBlock(left, player);
		check(helper, !helper.getBlockState(left).getValue(DoorBlock.OPEN), "crouch-clicked door closed");
		check(helper, helper.getBlockState(right).getValue(DoorBlock.OPEN), "partner stays open when crouching");
		helper.succeed();
	}

	private static void placeDoor(GameTestHelper helper, BlockPos pos, DoorHingeSide hinge) {
		BlockState lower = Blocks.OAK_DOOR.defaultBlockState()
			.setValue(DoorBlock.FACING, Direction.NORTH)
			.setValue(DoorBlock.HINGE, hinge)
			.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
		helper.setBlock(pos.below(), Blocks.STONE);
		helper.setBlock(pos, lower);
		helper.setBlock(pos.above(), lower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
	}

	@GameTest
	public void deathDropsAreProtected(GameTestHelper helper) {
		ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
		Vec3 spot = Vec3.atCenterOf(helper.absolutePos(new BlockPos(3, 2, 3)));
		player.setPos(spot);
		player.getInventory().add(new ItemStack(Items.DIAMOND, 5));
		player.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
		player.getInventory().dropAll();

		List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(spot, spot).inflate(4));
		check(helper, drops.size() == 2, "two death drops, found " + drops.size());
		for (ItemEntity drop : drops) {
			check(helper, drop.isPermanentlyInvulnerable(), "drop is invulnerable");
			check(helper, drop.hasGlowingTag(), "drop glows");
			check(helper, drop.getAge() < -30000, "drop never despawns");
		}
		helper.succeed();
	}

	@GameTest
	public void sortingMergesAndOrders(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		SimpleContainer chest = new SimpleContainer(27);
		chest.setItem(4, new ItemStack(Items.DIAMOND, 1));
		chest.setItem(9, new ItemStack(Items.DIRT, 40));
		chest.setItem(15, new ItemStack(Items.STONE, 16));
		chest.setItem(20, new ItemStack(Items.DIRT, 32));
		ChestMenu menu = ChestMenu.threeRows(1, player.getInventory(), chest);

		List<Slot> section = InventorySorter.sectionToSort(menu, player.getInventory(), menu.getSlot(0));
		check(helper, section.size() == 27, "container section is the 27 chest slots");
		check(helper, InventorySorter.sortSlots(section, player), "sort succeeded");
		check(helper, chest.getItem(0).is(Items.STONE) && chest.getItem(0).getCount() == 16, "stone first");
		check(helper, chest.getItem(1).is(Items.DIRT) && chest.getItem(1).getCount() == 64, "dirt merged to a full stack");
		check(helper, chest.getItem(2).is(Items.DIRT) && chest.getItem(2).getCount() == 8, "dirt remainder");
		check(helper, chest.getItem(3).is(Items.DIAMOND), "diamond after blocks");
		check(helper, chest.getItem(4).isEmpty(), "rest is empty");

		// Sorting the player section never moves the hotbar.
		player.getInventory().setItem(0, new ItemStack(Items.IRON_SWORD));
		player.getInventory().setItem(30, new ItemStack(Items.COBBLESTONE, 5));
		Slot playerSlot = menu.slots.stream().filter(s -> s.container == player.getInventory() && s.getContainerSlot() == 30).findFirst().orElseThrow();
		List<Slot> playerSection = InventorySorter.sectionToSort(menu, player.getInventory(), playerSlot);
		check(helper, playerSection.size() == 27, "player section is the 27 main inventory slots");
		InventorySorter.sortSlots(playerSection, player);
		check(helper, player.getInventory().getItem(0).is(Items.IRON_SWORD), "hotbar untouched");
		check(helper, player.getInventory().getItem(9).is(Items.COBBLESTONE), "cobblestone moved to the first main slot");
		helper.succeed();
	}

	@GameTest
	public void builtInDataPacksAreActive(GameTestHelper helper) {
		check(helper, Enchantment.areCompatible(enchantment(helper, Enchantments.INFINITY), enchantment(helper, Enchantments.MENDING)),
			"Infinity and Mending are compatible");
		ResourceKey<Recipe<?>> recipe = ResourceKey.create(Registries.RECIPE, FillVanillaGaps.id("leather_from_smoking_rotten_flesh"));
		check(helper, helper.getLevel().getServer().getRecipeManager().byKey(recipe).isPresent(), "rotten flesh smoking recipe loaded");
		helper.succeed();
	}
}
