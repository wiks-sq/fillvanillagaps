package com.fillvanillagaps.mixin;

import com.fillvanillagaps.FvgConfig;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * No more "Too Expensive!": every anvil operation is capped at 39 levels instead of being refused at 40+.
 * Vanilla's separate rule that blocks enchanting a whole stack at once is kept.
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
	private static final int MAX_COST = 39;

	@ModifyArg(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V"))
	private int fillvanillagaps$capCost(int cost) {
		if (!FvgConfig.get().anvilNoTooExpensive || cost <= MAX_COST) {
			return cost;
		}
		if (((ItemCombinerMenuAccessor) (Object) this).fillvanillagaps$getInputSlots().getItem(0).getCount() > 1) {
			return cost;
		}
		return MAX_COST;
	}
}
