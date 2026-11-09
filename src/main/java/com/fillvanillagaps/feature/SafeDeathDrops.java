package com.fillvanillagaps.feature;

import com.fillvanillagaps.FvgConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

/** Items a player drops on death never despawn, can't be destroyed by lava/fire/explosions/cactus, and glow through walls. */
public final class SafeDeathDrops {
	private SafeDeathDrops() {
	}

	public static void protect(Entity entity) {
		if (FvgConfig.get().safeDeathDrops && entity instanceof ItemEntity item) {
			item.setUnlimitedLifetime();
			item.setPermanentlyInvulnerable(true);
			item.setGlowingTag(true);
		}
	}
}
