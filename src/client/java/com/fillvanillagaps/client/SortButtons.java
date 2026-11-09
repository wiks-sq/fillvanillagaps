package com.fillvanillagaps.client;

import com.fillvanillagaps.client.mixin.AbstractContainerScreenAccessor;
import com.fillvanillagaps.feature.InventorySorter;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/** Small sort buttons in the top-right corner of the container and the player-inventory sections. */
public final class SortButtons {
	private static final int SIZE = 9;

	private SortButtons() {
	}

	public static void addButtons(Minecraft minecraft, Screen screen, int width, int height) {
		if (!(screen instanceof AbstractContainerScreen<?> containerScreen) || !InventorySortClient.canSort(containerScreen)) {
			return;
		}
		AbstractContainerScreenAccessor access = (AbstractContainerScreenAccessor) containerScreen;
		Inventory inventory = minecraft.player == null ? null : minecraft.player.getInventory();
		if (inventory == null) {
			return;
		}

		Slot playerSlot = firstSlot(containerScreen, inventory, true);
		Slot containerSlot = InventorySorter.isSortableContainer(containerScreen.getMenu()) ? firstSlot(containerScreen, inventory, false) : null;
		Component tooltip = Component.translatable("gui.fillvanillagaps.sort", FillVanillaGapsClient.SORT_KEY.getTranslatedKeyMessage());

		if (containerSlot != null) {
			Button button = makeButton(containerScreen, containerSlot, tooltip);
			Screens.getWidgets(screen).add(button);
			ScreenEvents.beforeExtract(screen).register((s, graphics, mouseX, mouseY, delta) ->
				place(button, access, access.fillvanillagaps$getTitleLabelY()));
		}
		if (playerSlot != null) {
			Button button = makeButton(containerScreen, playerSlot, tooltip);
			Screens.getWidgets(screen).add(button);
			ScreenEvents.beforeExtract(screen).register((s, graphics, mouseX, mouseY, delta) ->
				place(button, access, access.fillvanillagaps$getInventoryLabelY()));
		}
	}

	private static Button makeButton(AbstractContainerScreen<?> screen, Slot target, Component tooltip) {
		return Button.builder(Component.literal("⇅"), b -> InventorySortClient.requestSort(screen, target))
			.bounds(0, 0, SIZE, SIZE)
			.tooltip(Tooltip.create(tooltip))
			.build();
	}

	/** Re-placed every frame because the recipe book can shift the screen sideways. */
	private static void place(Button button, AbstractContainerScreenAccessor access, int labelY) {
		button.setX(access.fillvanillagaps$getLeftPos() + access.fillvanillagaps$getImageWidth() - 7 - SIZE);
		button.setY(access.fillvanillagaps$getTopPos() + labelY - 1);
	}

	private static Slot firstSlot(AbstractContainerScreen<?> screen, Inventory inventory, boolean playerInventory) {
		for (Slot slot : screen.getMenu().slots) {
			boolean isPlayer = slot.container == inventory;
			if (playerInventory && isPlayer && slot.getContainerSlot() >= Inventory.getSelectionSize() && slot.getContainerSlot() < Inventory.INVENTORY_SIZE) {
				return slot;
			}
			if (!playerInventory && !isPlayer) {
				return slot;
			}
		}
		return null;
	}
}
