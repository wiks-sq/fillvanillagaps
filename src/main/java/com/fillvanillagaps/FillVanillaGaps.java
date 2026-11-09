package com.fillvanillagaps;

import com.fillvanillagaps.feature.CropHarvest;
import com.fillvanillagaps.feature.InventorySorter;
import com.fillvanillagaps.feature.VeinMining;
import com.fillvanillagaps.network.SortPayload;
import com.fillvanillagaps.registry.TerracottaBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FillVanillaGaps implements ModInitializer {
	public static final String MOD_ID = "fillvanillagaps";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		FvgConfig.load();
		TerracottaBlocks.register();

		UseBlockCallback.EVENT.register(CropHarvest::onUseBlock);
		PlayerBlockBreakEvents.AFTER.register(VeinMining::afterBlockBreak);

		PayloadTypeRegistry.serverboundPlay().register(SortPayload.TYPE, SortPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SortPayload.TYPE,
			(payload, context) -> context.server().execute(() -> InventorySorter.handle(context.player(), payload)));

		// Data-driven tweaks ship as built-in data packs, so each world can switch them off with /datapack.
		ModContainer self = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
		ResourceLoader.registerBuiltinPack(id("infinity_mending"), self,
			Component.translatable("dataPack.fillvanillagaps.infinity_mending.name"), PackActivationType.DEFAULT_ENABLED);
		ResourceLoader.registerBuiltinPack(id("rotten_flesh_leather"), self,
			Component.translatable("dataPack.fillvanillagaps.rotten_flesh_leather.name"), PackActivationType.DEFAULT_ENABLED);

		LOGGER.info("Fill Vanilla Gaps loaded");
	}
}
