package com.fillvanillagaps.mixin;

import com.fillvanillagaps.feature.SafeDeathDrops;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Armor and offhand items dropped on a player's death (called from Inventory.dropAll). */
@Mixin(EntityEquipment.class)
public abstract class EntityEquipmentMixin {
	@WrapOperation(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean fillvanillagaps$protectDrop(Level level, Entity entity, Operation<Boolean> original, LivingEntity dropper) {
		if (dropper instanceof Player) {
			SafeDeathDrops.protect(entity);
		}
		return original.call(level, entity);
	}
}
