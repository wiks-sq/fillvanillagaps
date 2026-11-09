package com.fillvanillagaps.mixin;

import com.fillvanillagaps.FvgConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Boots with Feather Falling no longer trample farmland. */
@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {
	@WrapOperation(method = "fallOn", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/FarmlandBlock;turnToBaseBlock(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
	private void fillvanillagaps$featherFalling(FarmlandBlock self, Entity entity, BlockState state, Level level, BlockPos pos, Operation<Void> original) {
		if (FvgConfig.get().featherFallingStopsTrampling && entity instanceof LivingEntity living && hasFeatherFalling(living)) {
			return;
		}
		original.call(self, entity, state, level, pos);
	}

	private static boolean hasFeatherFalling(LivingEntity entity) {
		ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
		if (boots.isEmpty()) {
			return false;
		}
		return entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.FEATHER_FALLING)
			.map(enchantment -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, boots) > 0)
			.orElse(false);
	}
}
