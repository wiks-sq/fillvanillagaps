package com.fillvanillagaps.client.mixin;

import com.fillvanillagaps.client.FillVanillaGapsClient;
import com.fillvanillagaps.client.InventorySortClient;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The sort key works in any container screen. Text fields (anvil, recipe search) get the key first. */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
	// 26.4 turned slots into SlotWidgets; the hovered one is only reachable through this getter.
	@Shadow
	protected abstract @Nullable SlotWidget getHoveredSlot();

	@Inject(method = "keyPressed", cancellable = true, at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;checkHotbarKeyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"))
	private void fillvanillagaps$sortKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		SlotWidget hovered = this.getHoveredSlot();
		if (FillVanillaGapsClient.SORT_KEY.matches(event)
			&& InventorySortClient.requestSort((AbstractContainerScreen<?>) (Object) this, hovered == null ? null : hovered.slot())) {
			cir.setReturnValue(true);
		}
	}
}
