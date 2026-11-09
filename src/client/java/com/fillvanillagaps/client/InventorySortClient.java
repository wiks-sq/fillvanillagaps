package com.fillvanillagaps.client;

import com.fillvanillagaps.FvgConfig;
import com.fillvanillagaps.network.SortPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

public final class InventorySortClient {
	private InventorySortClient() {
	}

	public static boolean canSort(AbstractContainerScreen<?> screen) {
		return FvgConfig.get().inventorySorting
			&& !(screen instanceof CreativeModeInventoryScreen)
			&& ClientPlayNetworking.canSend(SortPayload.TYPE);
	}

	/** Returns true if a sort request was sent. */
	public static boolean requestSort(AbstractContainerScreen<?> screen, @Nullable Slot slot) {
		if (!canSort(screen)) {
			return false;
		}
		ClientPlayNetworking.send(new SortPayload(screen.getMenu().containerId, slot == null ? -1 : slot.index));
		return true;
	}
}
