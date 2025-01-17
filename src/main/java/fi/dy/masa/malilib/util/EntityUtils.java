package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class EntityUtils {
	/**
	 * Returns the camera entity, if it's not null, otherwise returns the client player entity.
	 *
	 * @return the camera entity, or the client player entity if the camera entity is null
	 */
	@Nullable
	public static Entity getCameraEntity() {
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.getCameraEntity();

		if (entity == null) {
			entity = mc.player;
		}

		return entity;
	}
}
