package com.fillvanillagaps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Every feature can be switched off in config/fillvanillagaps.json.
 * Missing keys keep their defaults, and the file is rewritten on startup so new options appear.
 */
public final class FvgConfig {
	private static FvgConfig instance = new FvgConfig();

	// Server-side gameplay features
	public boolean cropRightClickHarvest = true;
	public boolean treeFelling = true;
	public int treeFellingMaxLogs = 256;
	public boolean veinMining = true;
	public int veinMiningMaxBlocks = 64;
	public boolean fastLeafDecay = true;
	public boolean featherFallingStopsTrampling = true;
	public boolean anvilNoTooExpensive = true;
	public boolean doubleDoors = true;
	public boolean safeDeathDrops = true;
	public boolean inventorySorting = true;

	// Client-side HUD features
	public boolean foodSaturationHud = true;
	public boolean foodTooltip = true;

	public static FvgConfig get() {
		return instance;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(FillVanillaGaps.MOD_ID + ".json");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			if (Files.exists(path)) {
				FvgConfig loaded = gson.fromJson(Files.readString(path), FvgConfig.class);
				if (loaded != null) {
					instance = loaded;
				}
			}
			Files.createDirectories(path.getParent());
			Files.writeString(path, gson.toJson(instance));
		} catch (IOException | JsonParseException e) {
			FillVanillaGaps.LOGGER.warn("Could not read {}, using defaults", path, e);
		}
	}
}
