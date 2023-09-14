package fi.dy.masa.malilib.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.PacketUtils;

public class ClientPacketChannelHandler implements IClientPacketChannelHandler {
	public static final ResourceLocation REGISTER = new ResourceLocation("minecraft:register");
	public static final ResourceLocation UNREGISTER = new ResourceLocation("minecraft:unregister");

	private static final ClientPacketChannelHandler INSTANCE = new ClientPacketChannelHandler();

	private final ArrayListMultimap<ResourceLocation, IPluginChannelHandler> handlers = ArrayListMultimap.create();

	public static IClientPacketChannelHandler getInstance() {
		return INSTANCE;
	}

	private ClientPacketChannelHandler() {
	}

	@Override
	public void registerClientChannelHandler(IPluginChannelHandler handler) {
		Set<ResourceLocation> toRegister = new HashSet<>();

		for (ResourceLocation channel : handler.getChannels()) {
			if (!this.handlers.containsEntry(channel, handler)) {
				this.handlers.put(channel, handler);

				if (handler.registerToServer()) {
					toRegister.add(channel);
				}
			}
		}

		if (!toRegister.isEmpty()) {
			this.sendRegisterPacket(REGISTER, toRegister);
		}
	}

	@Override
	public void unregisterClientChannelHandler(IPluginChannelHandler handler) {
		Set<ResourceLocation> toUnRegister = new HashSet<>();

		for (ResourceLocation channel : handler.getChannels()) {
			if (this.handlers.remove(channel, handler) && handler.registerToServer()) {
				toUnRegister.add(channel);
			}
		}

		if (!toUnRegister.isEmpty()) {
			this.sendRegisterPacket(UNREGISTER, toUnRegister);
		}
	}

	/**
	 * NOT PUBLIC API - DO NOT CALL
	 */
	public boolean processPacketFromServer(ClientboundCustomPayloadPacket packet, ClientPacketListener netHandler) {
		ResourceLocation channel = packet.getIdentifier();
		List<IPluginChannelHandler> handlers = this.handlers.get(channel);

		if (!handlers.isEmpty()) {
			for (IPluginChannelHandler handler : handlers) {
				FriendlyByteBuf buf = handler.usePacketSplitter() ? PacketSplitter.receive(netHandler, packet) : PacketUtils.retainedSlice(packet.getData());

				// Finished the complete packet
				if (buf != null) {
					handler.onPacketReceived(buf);
				}
			}

			return true;
		}

		return false;
	}

	private void sendRegisterPacket(ResourceLocation type, Collection<ResourceLocation> channels) {
		String joinedChannels = channels.stream().map(ResourceLocation::toString).collect(Collectors.joining("\0"));
		ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
		ServerboundCustomPayloadPacket packet = new ServerboundCustomPayloadPacket(type, new FriendlyByteBuf(payload));

		ClientPacketListener netHandler = Minecraft.getInstance().getConnection();

		if (netHandler != null) {
			netHandler.send(packet);
		} else {
			MaLiLib.logger.warn("Failed to send register channel packet - network handler was null");
		}
	}
}
