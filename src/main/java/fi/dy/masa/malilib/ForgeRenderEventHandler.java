package fi.dy.masa.malilib;

import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

class ForgeRenderEventHandler {

	@SubscribeEvent
	public void onRenderPost(RenderGuiEvent.Post event) {
		((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(event.getPoseStack(), Minecraft.getInstance(), event.getPartialTick());
	}

	@SubscribeEvent
	public void onRenderTooltip(RenderTooltipEvent.Pre event) {
		((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(event.getPoseStack(), event.getItemStack(), event.getX(), event.getY());
	}

}
