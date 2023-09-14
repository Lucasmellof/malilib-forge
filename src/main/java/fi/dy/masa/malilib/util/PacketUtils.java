package fi.dy.masa.malilib.util;

import java.util.Objects;

import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.ByteBuf;

public class PacketUtils {
	/**
	 * Wraps the newly created buf from {@code buf.slice()} in a PacketByteBuf.
	 *
	 * @param buf the original ByteBuf
	 * @return a slice of the buffer
	 * @see ByteBuf#slice()
	 */
	public static FriendlyByteBuf slice(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");
		return new FriendlyByteBuf(buf.slice());
	}

	/**
	 * Wraps the newly created buf from {@code buf.retainedSlice()} in a PacketByteBuf.
	 *
	 * @param buf the original ByteBuf
	 * @return a slice of the buffer
	 * @see ByteBuf#retainedSlice()
	 */
	public static FriendlyByteBuf retainedSlice(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");
		return new FriendlyByteBuf(buf.retainedSlice());
	}
}
