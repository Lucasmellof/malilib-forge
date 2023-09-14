package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public interface IWorldLoadListener {
	/**
	 * Called when the client world is going to be changed,
	 * before the reference has been changed
	 *
	 * @param worldBefore the old world reference, before the new one gets assigned
	 * @param worldAfter  the new world reference that is going to get assigned
	 * @param mc
	 */
	default void onWorldLoadPre(@Nullable ClientLevel worldBefore, @Nullable ClientLevel worldAfter, Minecraft mc) {
	}

	/**
	 * Called after the client world reference has been changed
	 *
	 * @param worldBefore the old world reference, before the new one gets assigned
	 * @param worldAfter  the new world reference that is going to get assigned
	 * @param mc
	 */
	default void onWorldLoadPost(@Nullable ClientLevel worldBefore, @Nullable ClientLevel worldAfter, Minecraft mc) {
	}
}
