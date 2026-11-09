package com.fillvanillagaps.feature;

import com.fillvanillagaps.FvgConfig;
import com.fillvanillagaps.network.SortPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts either the open container (chests, barrels, ender chests, shulker boxes, chest boats/minecarts)
 * or the player's main inventory. The hotbar is never touched.
 */
public final class InventorySorter {
	private static final Comparator<ItemStack> ORDER = Comparator
		.comparingInt((ItemStack stack) -> BuiltInRegistries.ITEM.getId(stack.getItem()))
		.thenComparing(stack -> stack.getHoverName().getString())
		.thenComparing(Comparator.comparingInt(ItemStack::getCount).reversed());

	private InventorySorter() {
	}

	public static void handle(ServerPlayer player, SortPayload payload) {
		if (!FvgConfig.get().inventorySorting || player.isSpectator()) {
			return;
		}
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != payload.containerId()) {
			return;
		}
		Slot hovered = payload.slotIndex() >= 0 && payload.slotIndex() < menu.slots.size() ? menu.slots.get(payload.slotIndex()) : null;
		List<Slot> section = sectionToSort(menu, player.getInventory(), hovered);
		if (!section.isEmpty() && sortSlots(section, player)) {
			menu.broadcastChanges();
		}
	}

	public static boolean isSortableContainer(AbstractContainerMenu menu) {
		return menu instanceof ChestMenu || menu instanceof ShulkerBoxMenu;
	}

	public static List<Slot> sectionToSort(AbstractContainerMenu menu, Inventory inventory, @Nullable Slot hovered) {
		boolean playerSection = hovered != null ? hovered.container == inventory : !isSortableContainer(menu);
		List<Slot> slots = new ArrayList<>();
		for (Slot slot : menu.slots) {
			if (playerSection) {
				if (slot.container == inventory && slot.getContainerSlot() >= Inventory.getSelectionSize() && slot.getContainerSlot() < Inventory.INVENTORY_SIZE) {
					slots.add(slot);
				}
			} else if (isSortableContainer(menu) && slot.container != inventory) {
				slots.add(slot);
			}
		}
		return slots;
	}

	/** Merges partial stacks and orders the slots. Leaves everything untouched if any slot refuses its new stack. */
	public static boolean sortSlots(List<Slot> slots, Player player) {
		List<ItemStack> merged = new ArrayList<>();
		for (Slot slot : slots) {
			if (!slot.mayPickup(player)) {
				return false;
			}
			ItemStack stack = slot.getItem().copy();
			for (ItemStack existing : merged) {
				if (stack.isEmpty()) {
					break;
				}
				int room = Math.min(existing.getMaxStackSize(), slot.getMaxStackSize(existing)) - existing.getCount();
				if (room > 0 && ItemStack.isSameItemSameComponents(existing, stack)) {
					int moved = Math.min(room, stack.getCount());
					existing.grow(moved);
					stack.shrink(moved);
				}
			}
			if (!stack.isEmpty()) {
				merged.add(stack);
			}
		}
		merged.sort(ORDER);
		if (merged.size() > slots.size()) {
			return false;
		}
		for (int i = 0; i < merged.size(); i++) {
			if (!slots.get(i).mayPlace(merged.get(i))) {
				return false;
			}
		}
		for (int i = 0; i < slots.size(); i++) {
			slots.get(i).set(i < merged.size() ? merged.get(i) : ItemStack.EMPTY);
		}
		return true;
	}
}
