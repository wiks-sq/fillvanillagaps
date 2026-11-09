package com.fillvanillagaps.client.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
	@Accessor("leftPos")
	int fillvanillagaps$getLeftPos();

	@Accessor("topPos")
	int fillvanillagaps$getTopPos();

	@Accessor("imageWidth")
	int fillvanillagaps$getImageWidth();

	@Accessor("titleLabelY")
	int fillvanillagaps$getTitleLabelY();

	@Accessor("inventoryLabelY")
	int fillvanillagaps$getInventoryLabelY();
}
