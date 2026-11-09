package com.fillvanillagaps.client;

import com.fillvanillagaps.FillVanillaGaps;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.KeyMapping;

public final class FillVanillaGapsClient implements ClientModInitializer {
	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(FillVanillaGaps.id("main"));
	public static final KeyMapping SORT_KEY = new KeyMapping("key.fillvanillagaps.sort", InputConstants.KEY_R, CATEGORY);

	@Override
	public void onInitializeClient() {
		KeyMappingHelper.registerKeyMapping(SORT_KEY);
		ScreenEvents.AFTER_INIT.register(SortButtons::addButtons);
		HudElementRegistry.attachElementAfter(VanillaHudElements.FOOD_BAR, FillVanillaGaps.id("food_saturation"), FoodHud::extract);
		ItemTooltipCallback.EVENT.register(FoodHud::appendTooltip);
	}
}
