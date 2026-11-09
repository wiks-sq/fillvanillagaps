package com.fillvanillagaps.mixin;

import com.fillvanillagaps.FvgConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Opening one door of a double door opens its partner too. Crouch to open just one. */
@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin {
	@Inject(method = "useWithoutItem", at = @At("RETURN"))
	private void fillvanillagaps$openPartner(BlockState original, Level level, BlockPos pos, Player player, BlockHitResult hit,
		CallbackInfoReturnable<InteractionResult> cir) {
		if (!FvgConfig.get().doubleDoors || player.isShiftKeyDown() || !cir.getReturnValue().consumesAction()) {
			return;
		}
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof DoorBlock door)) {
			return;
		}
		Direction facing = state.getValue(DoorBlock.FACING);
		DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
		// Vanilla gives a door placed next to another the opposite hinge, so the partner sits on the side away from the hinge.
		Direction towardPartner = hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
		BlockPos partnerPos = pos.relative(towardPartner);
		BlockState partner = level.getBlockState(partnerPos);
		if (partner.is(state.getBlock())
			&& partner.getValue(DoorBlock.FACING) == facing
			&& partner.getValue(DoorBlock.HINGE) != hinge
			&& partner.getValue(DoorBlock.HALF) == state.getValue(DoorBlock.HALF)) {
			door.setOpen(player, level, partner, partnerPos, state.getValue(DoorBlock.OPEN));
		}
	}
}
