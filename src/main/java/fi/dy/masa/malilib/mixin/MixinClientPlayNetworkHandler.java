package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.network.ClientPacketChannelHandler;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPlayNetworkHandler
{

    @Shadow private ClientLevel level;
    @Shadow @Final private Minecraft minecraft;
    @Nullable private ClientLevel worldBefore;

    @Inject(method = "handleLogin", at = @At("HEAD"))
    private void onPreJoinGameHead(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        // Need to grab the old world reference at the start of the method,
        // because the next injection point is right after the world has been assigned,
        // since we need the new world reference for the callback.
        this.worldBefore = this.level;
    }

    @Inject(method = "handleLogin", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/Minecraft;setLevel(Lnet/minecraft/client/multiplayer/ClientLevel;)V"))
    private void onPreGameJoin(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, this.level, this.minecraft);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onPostGameJoin(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, this.level, this.minecraft);
        this.worldBefore = null;
    }

    @Inject(method = "handleCustomPayload", cancellable = true,
                at = @At(value = "INVOKE",
                         target = "Lnet/minecraft/network/protocol/game/ClientboundCustomPayloadPacket;getIdentifier()Lnet/minecraft/resources/ResourceLocation;"))
    private void onCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci)
    {
        if (((ClientPacketChannelHandler) ClientPacketChannelHandler.getInstance()).processPacketFromServer(packet, (ClientPacketListener)(Object) this))
        {
            ci.cancel();
        }
    }
}
