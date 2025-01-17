package fi.dy.masa.malilib.interfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;

public interface IRenderer {
	/**
	 * Called after the vanilla overlays have been rendered
	 */
	default void onRenderGameOverlayPost(PoseStack matrixStack) {
	}

	/**
	 * Called after vanilla world rendering
	 */
	default void onRenderWorldLast(PoseStack matrixStack, Matrix4f projMatrix) {
	}

	/**
	 * Called after the tooltip text of an item has been rendered
	 */
	default void onRenderTooltipLast(ItemStack stack, int x, int y) {
	}

	/**
	 * Returns a supplier for the profiler section name that should be used for this renderer
	 */
	default Supplier<String> getProfilerSectionSupplier() {
		return () -> this.getClass().getName();
	}
}
