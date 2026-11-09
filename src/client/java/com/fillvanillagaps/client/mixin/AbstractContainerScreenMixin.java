package com.fillvanillagaps.client.mixin;

import com.fillvanillagaps.client.FillVanillaGapsClient;
import com.fillvanillagaps.client.InventorySortClient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The sort key works in any container screen. Text fields (anvil, recipe search) get the key first. */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
	@Shadow
	protected @Nullable Slot hoveredSlot;

	@Inject(method = "keyPressed", cancellable = true, at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;checkHotbarKeyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"))
	private void fillvanillagaps$sortKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		if (FillVanillaGapsClient.SORT_KEY.matches(event)
			&& InventorySortClient.requestSort((AbstractContainerScreen<?>) (Object) this, this.hoveredSlot)) {
			cir.setReturnValue(true);
		}
	}
}
