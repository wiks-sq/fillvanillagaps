package com.fillvanillagaps.client;

import com.fillvanillagaps.FvgConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Locale;

/**
 * Shows the hidden saturation value as a gold outline around the hunger icons, and previews how much
 * hunger the food in your hand will restore. Food tooltips show hunger and saturation numbers.
 */
public final class FoodHud {
	private static final int SATURATION_COLOR = 0xFFF5C518;

	private FoodHud() {
	}

	public static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		if (!FvgConfig.get().foodSaturationHud || player == null || minecraft.gui.hud.isHidden()
			|| minecraft.gameMode == null || !minecraft.gameMode.canHurtPlayer()
			|| player.getVehicle() instanceof LivingEntity) {
			return;
		}

		FoodData food = player.getFoodData();
		int xRight = graphics.guiWidth() / 2 + 91;
		int y = graphics.guiHeight() - 39;

		FoodProperties held = heldFood(player);
		if (held != null && food.getFoodLevel() < 20) {
			int restored = Math.min(20, food.getFoodLevel() + held.nutrition());
			float pulse = 0.5F + 0.5F * Mth.sin((player.tickCount + deltaTracker.getGameTimeDeltaPartialTick(false)) * 0.25F);
			int color = ((int) (0x30 + 0x50 * pulse) << 24) | 0xFFFFFF;
			for (int i = 0; i < 10; i++) {
				int x = xRight - i * 8 - 9;
				int before = Mth.clamp(food.getFoodLevel() - i * 2, 0, 2);
				int after = Mth.clamp(restored - i * 2, 0, 2);
				if (after > before) {
					graphics.fill(x + 1, y + 1, x + 8, y + 8, color);
				}
			}
		}

		float saturation = food.getSaturationLevel();
		for (int i = 0; i < 10; i++) {
			float amount = Mth.clamp(saturation - i * 2, 0.0F, 2.0F) / 2.0F;
			if (amount <= 0.0F) {
				break;
			}
			drawFrame(graphics, xRight - i * 8 - 9, y, amount);
		}
	}

	/** A 9x9 outline; partial saturation draws only the right-hand share of it. */
	private static void drawFrame(GuiGraphicsExtractor graphics, int x, int y, float amount) {
		int width = Math.max(1, Math.round(9 * amount));
		int left = x + 9 - width;
		graphics.fill(left, y, x + 9, y + 1, SATURATION_COLOR);
		graphics.fill(left, y + 8, x + 9, y + 9, SATURATION_COLOR);
		graphics.fill(x + 8, y, x + 9, y + 9, SATURATION_COLOR);
		graphics.fill(left, y, left + 1, y + 9, SATURATION_COLOR);
	}

	private static FoodProperties heldFood(Player player) {
		FoodProperties main = player.getMainHandItem().get(DataComponents.FOOD);
		return main != null ? main : player.getOffhandItem().get(DataComponents.FOOD);
	}

	public static void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> lines) {
		if (!FvgConfig.get().foodTooltip) {
			return;
		}
		FoodProperties food = stack.get(DataComponents.FOOD);
		if (food != null) {
			lines.add(Component.translatable("tooltip.fillvanillagaps.food", food.nutrition(),
				String.format(Locale.ROOT, "%.1f", food.saturation())).withStyle(ChatFormatting.GOLD));
		}
	}
}
