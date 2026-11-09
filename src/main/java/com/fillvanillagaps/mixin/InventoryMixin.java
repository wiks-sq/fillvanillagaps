package com.fillvanillagaps.mixin;

import com.fillvanillagaps.feature.SafeDeathDrops;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Inventory.dropAll only runs when a player dies without keepInventory. */
@Mixin(Inventory.class)
public abstract class InventoryMixin {
	@WrapOperation(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean fillvanillagaps$protectDrop(Level level, Entity entity, Operation<Boolean> original) {
		SafeDeathDrops.protect(entity);
		return original.call(level, entity);
	}
}
