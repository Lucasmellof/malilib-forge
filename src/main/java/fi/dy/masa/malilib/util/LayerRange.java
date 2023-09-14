package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRangeChangeListener;

public class LayerRange {
	protected final IRangeChangeListener refresher;
	protected LayerMode layerMode = LayerMode.ALL;
	protected Axis axis = Axis.Y;
	protected int layerSingle = 0;
	protected int layerAbove = 0;
	protected int layerBelow = 0;
	protected int layerRangeMin = 0;
	protected int layerRangeMax = 0;
	protected boolean hotkeyRangeMin;
	protected boolean hotkeyRangeMax;

	public LayerRange(IRangeChangeListener refresher) {
		this.refresher = refresher;
	}

	public LayerMode getLayerMode() {
		return this.layerMode;
	}

	public Axis getAxis() {
		return this.axis;
	}

	public boolean getMoveLayerRangeMin() {
		return this.hotkeyRangeMin;
	}

	public boolean getMoveLayerRangeMax() {
		return this.hotkeyRangeMax;
	}

	public void toggleHotkeyMoveRangeMin() {
		this.hotkeyRangeMin = !this.hotkeyRangeMin;
	}

	public void toggleHotkeyMoveRangeMax() {
		this.hotkeyRangeMax = !this.hotkeyRangeMax;
	}

	public int getLayerSingle() {
		return this.layerSingle;
	}

	public int getLayerAbove() {
		return this.layerAbove;
	}

	public int getLayerBelow() {
		return this.layerBelow;
	}

	public int getLayerRangeMin() {
		return this.layerRangeMin;
	}

	public int getLayerRangeMax() {
		return this.layerRangeMax;
	}

	public int getLayerMin() {
		return switch (this.layerMode) {
			case ALL, ALL_BELOW -> -30000000;
			case SINGLE_LAYER -> this.layerSingle;
			case ALL_ABOVE -> this.layerAbove;
			case LAYER_RANGE -> this.layerRangeMin;
		};

	}

	public int getLayerMax() {
		return switch (this.layerMode) {
			case ALL, ALL_ABOVE -> 30000000;
			case SINGLE_LAYER -> this.layerSingle;
			case ALL_BELOW -> this.layerBelow;
			case LAYER_RANGE -> this.layerRangeMax;
		};

	}

	public int getCurrentLayerValue(boolean isSecondValue) {
		return switch (this.layerMode) {
			case SINGLE_LAYER -> this.layerSingle;
			case ALL_ABOVE -> this.layerAbove;
			case ALL_BELOW -> this.layerBelow;
			case LAYER_RANGE -> isSecondValue ? this.layerRangeMax : this.layerRangeMin;
			default -> 0;
		};
	}

	public void setLayerMode(LayerMode mode) {
		this.setLayerMode(mode, true);
	}

	public void setLayerMode(LayerMode mode, boolean printMessage) {
		this.layerMode = mode;

		this.refresher.updateAll();

		if (printMessage) {
			String val = GuiBase.TXT_GREEN + mode.getDisplayName();
			InfoUtils.printActionbarMessage("malilib.message.set_layer_mode_to", val);
		}
	}

	public void setAxis(Axis axis) {
		this.axis = axis;

		this.refresher.updateAll();
		String val = GuiBase.TXT_GREEN + axis.getName();
		InfoUtils.printActionbarMessage("malilib.message.set_layer_axis_to", val);
	}

	public void setLayerSingle(int layer) {
		int old = this.layerSingle;
		//layer = this.getWorldLimitsClampedValue(layer);

		if (layer != old) {
			this.layerSingle = layer;
			this.updateLayersBetween(old, old);
			this.updateLayersBetween(layer, layer);
		}
	}

	public void setLayerAbove(int layer) {
		int old = this.layerAbove;
		//layer = this.getWorldLimitsClampedValue(layer);

		if (layer != old) {
			this.layerAbove = layer;
			this.updateLayersBetween(old, layer);
		}
	}

	public void setLayerBelow(int layer) {
		int old = this.layerBelow;
		//layer = this.getWorldLimitsClampedValue(layer);

		if (layer != old) {
			this.layerBelow = layer;
			this.updateLayersBetween(old, layer);
		}
	}

	public boolean setLayerRangeMin(int layer) {
		return this.setLayerRangeMin(layer, false);
	}

	public boolean setLayerRangeMax(int layer) {
		return this.setLayerRangeMax(layer, false);
	}

	protected boolean setLayerRangeMin(int layer, boolean force) {
		int old = this.layerRangeMin;
		//layer = this.getWorldLimitsClampedValue(layer);

		if (!force) {
			layer = Math.min(layer, this.layerRangeMax);
		}

		if (layer != old) {
			this.layerRangeMin = layer;
			this.updateLayersBetween(old, layer);
		}

		return layer != old;
	}

	protected int getPositionFromEntity(Entity entity) {
		return switch (this.axis) {
			case X -> Mth.floor(entity.getX());
			case Y -> Mth.floor(entity.getY());
			case Z -> Mth.floor(entity.getZ());
		};

	}

	protected boolean setLayerRangeMax(int layer, boolean force) {
		int old = this.layerRangeMax;
		//layer = this.getWorldLimitsClampedValue(layer);

		if (!force) {
			layer = Math.max(layer, this.layerRangeMin);
		}

		if (layer != old) {
			this.layerRangeMax = layer;
			this.updateLayersBetween(old, layer);
		}

		return layer != old;
	}

	public void setSingleBoundaryToPosition(Entity entity) {
		int pos = this.getPositionFromEntity(entity);
		this.setSingleBoundaryToPosition(pos);
	}

	protected void setSingleBoundaryToPosition(int pos) {
		switch (this.layerMode) {
			case SINGLE_LAYER:
				this.setLayerSingle(pos);
				break;
			case ALL_ABOVE:
				this.setLayerAbove(pos);
				break;
			case ALL_BELOW:
				this.setLayerBelow(pos);
				break;
			default:
		}
	}

	public void setToPosition(Entity entity) {
		if (this.layerMode == LayerMode.LAYER_RANGE) {
			int pos = this.getPositionFromEntity(entity);
			this.setLayerRangeMin(pos, true);
			this.setLayerRangeMax(pos, true);
		} else {
			this.setSingleBoundaryToPosition(entity);
		}
	}

	protected void markAffectedLayersForRenderUpdate(IntBoundingBox limits) {
		int val1;
		int val2;

		switch (this.layerMode) {
			case ALL:
				this.refresher.updateAll();
				return;
			case SINGLE_LAYER: {
				val1 = this.layerSingle;
				val2 = this.layerSingle;
				break;
			}
			case ALL_ABOVE: {
				val1 = this.layerAbove;
				val2 = limits.getMaxValueForAxis(this.axis);
				;
				break;
			}
			case ALL_BELOW: {
				val1 = limits.getMinValueForAxis(this.axis);
				val2 = this.layerBelow;
				break;
			}
			case LAYER_RANGE: {
				val1 = this.layerRangeMin;
				val2 = this.layerRangeMax;
				break;
			}
			default:
				return;
		}

		this.updateLayersBetween(val1, val2);
	}

	protected void updateLayersBetween(int layer1, int layer2) {
		int layerMin = Math.min(layer1, layer2);
		int layerMax = Math.max(layer1, layer2);

		switch (this.axis) {
			case X:
				this.refresher.updateBetweenX(layerMin, layerMax);
				break;
			case Y:
				this.refresher.updateBetweenY(layerMin, layerMax);
				break;
			case Z:
				this.refresher.updateBetweenZ(layerMin, layerMax);
				break;
		}
	}

	public boolean moveLayer(int amount) {
		String axisName = this.axis.getName().toLowerCase();
		String strTo = GuiBase.TXT_GREEN + axisName + " = ";

		switch (this.layerMode) {
			case ALL:
				return false;
			case SINGLE_LAYER: {
				this.setLayerSingle(this.layerSingle + amount);
				String val = strTo + this.layerSingle;
				InfoUtils.printActionbarMessage("malilib.message.set_layer_to", val);
				break;
			}
			case ALL_ABOVE: {
				this.setLayerAbove(this.layerAbove + amount);
				String val = strTo + this.layerAbove;
				InfoUtils.printActionbarMessage("malilib.message.moved_min_layer_to", val);
				break;
			}
			case ALL_BELOW: {
				this.setLayerBelow(this.layerBelow + amount);
				String val = strTo + this.layerBelow;
				InfoUtils.printActionbarMessage("malilib.message.moved_max_layer_to", val);
				break;
			}
			case LAYER_RANGE: {
				Entity entity = EntityUtils.getCameraEntity();

				if (entity != null) {
					boolean minBoundaryClosest = this.layerRangeIsMinClosest(entity);
					this.moveLayerRange(amount, minBoundaryClosest);
				}

				break;
			}
			default:
		}

		return true;
	}

	protected void moveLayerRange(int amount, boolean minBoundaryClosest) {
		boolean moveMin = this.getMoveMin(minBoundaryClosest);
		boolean moveMax = this.getMoveMax(minBoundaryClosest);

		boolean moved = false;
		boolean force = moveMin && moveMax;

		if (moveMin) {
			moved |= this.setLayerRangeMin(this.layerRangeMin + amount, force);
		}

		if (moveMax) {
			moved |= this.setLayerRangeMax(this.layerRangeMax + amount, force);
		}

		if (moved) {
			String axisName = this.axis.getName().toLowerCase();

			if (moveMin && moveMax) {
				InfoUtils.printActionbarMessage("malilib.message.moved_layer_range", String.valueOf(amount), axisName);
			} else {
				String val1 = moveMin ? StringUtils.translate("malilib.message.layer_range.range_min") : StringUtils.translate("malilib.message.layer_range.range_max");
				InfoUtils.printActionbarMessage("malilib.message.moved_layer_range_boundary", val1, String.valueOf(amount), axisName);
			}
		}
	}

	protected boolean getMoveMax(boolean minBoundaryClosest) {
		return this.hotkeyRangeMax || (minBoundaryClosest == false && this.hotkeyRangeMin == false);
	}

	protected boolean getMoveMin(boolean minBoundaryClosest) {
		return this.hotkeyRangeMin || (minBoundaryClosest && this.hotkeyRangeMax == false);
	}

	protected boolean layerRangeIsMinClosest(Entity entity) {
		double playerPos = this.axis == Axis.Y ? entity.getY() : (this.axis == Axis.X ? entity.getX() : entity.getZ());
		double min = this.layerRangeMin + 0.5D;
		double max = this.layerRangeMax + 0.5D;

		return playerPos < min || (Math.abs(playerPos - min) < Math.abs(playerPos - max));
	}

	public String getCurrentLayerString() {
		return switch (this.layerMode) {
			case SINGLE_LAYER -> String.valueOf(this.layerSingle);
			case ALL_ABOVE -> String.valueOf(this.layerAbove);
			case ALL_BELOW -> String.valueOf(this.layerBelow);
			case LAYER_RANGE -> String.format("%d ... %s", this.layerRangeMin, this.layerRangeMax);
			default -> "";
		};
	}

	protected int getWorldLimitsClampedValue(int value, IntBoundingBox limits) {
		return Mth.clamp(value,
				limits.getMinValueForAxis(this.axis),
				limits.getMaxValueForAxis(this.axis));
	}

	public boolean isPositionWithinRange(BlockPos pos) {
		return this.isPositionWithinRange(pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean isPositionWithinRange(long posLong) {
		int x = BlockPos.getX(posLong);
		int y = BlockPos.getY(posLong);
		int z = BlockPos.getZ(posLong);

		return this.isPositionWithinRange(x, y, z);
	}

	public boolean isPositionWithinRange(int x, int y, int z) {
		return switch (this.layerMode) {
			case ALL -> true;
			case SINGLE_LAYER -> this.isPositionWithinSingleLayerRange(x, y, z);
			case ALL_ABOVE -> this.isPositionWithinAboveRange(x, y, z);
			case ALL_BELOW -> this.isPositionWithinBelowRange(x, y, z);
			case LAYER_RANGE -> this.isPositionWithinLayerRangeRange(x, y, z);
		};

	}

	protected boolean isPositionWithinSingleLayerRange(int x, int y, int z) {
		return switch (this.axis) {
			case X -> x == this.layerSingle;
			case Y -> y == this.layerSingle;
			case Z -> z == this.layerSingle;
		};

	}

	protected boolean isPositionWithinAboveRange(int x, int y, int z) {
		return switch (this.axis) {
			case X -> x >= this.layerAbove;
			case Y -> y >= this.layerAbove;
			case Z -> z >= this.layerAbove;
		};

	}

	protected boolean isPositionWithinBelowRange(int x, int y, int z) {
		return switch (this.axis) {
			case X -> x <= this.layerBelow;
			case Y -> y <= this.layerBelow;
			case Z -> z <= this.layerBelow;
		};

	}

	protected boolean isPositionWithinLayerRangeRange(int x, int y, int z) {
		return switch (this.axis) {
			case X -> x >= this.layerRangeMin && x <= this.layerRangeMax;
			case Y -> y >= this.layerRangeMin && y <= this.layerRangeMax;
			case Z -> z >= this.layerRangeMin && z <= this.layerRangeMax;
		};

	}

	public boolean isPositionAtRenderEdgeOnSide(BlockPos pos, Direction side) {
		return switch (this.axis) {
			case X ->
					(side == Direction.WEST && pos.getX() == this.getLayerMin()) || (side == Direction.EAST && pos.getX() == this.getLayerMax());
			case Y ->
					(side == Direction.DOWN && pos.getY() == this.getLayerMin()) || (side == Direction.UP && pos.getY() == this.getLayerMax());
			case Z ->
					(side == Direction.NORTH && pos.getZ() == this.getLayerMin()) || (side == Direction.SOUTH && pos.getZ() == this.getLayerMax());
		};

	}

	public boolean intersects(SubChunkPos pos) {
		switch (this.axis) {
			case X: {
				final int xMin = (pos.getX() << 4);
				final int xMax = (pos.getX() << 4) + 15;
				return !(xMax < this.getLayerMin() || xMin > this.getLayerMax());
			}
			case Y: {
				final int yMin = (pos.getY() << 4);
				final int yMax = (pos.getY() << 4) + 15;
				return !(yMax < this.getLayerMin() || yMin > this.getLayerMax());
			}
			case Z: {
				final int zMin = (pos.getZ() << 4);
				final int zMax = (pos.getZ() << 4) + 15;
				return !(zMax < this.getLayerMin() || zMin > this.getLayerMax());
			}
			default:
				return false;
		}
	}

	public boolean intersects(IntBoundingBox box) {
		return this.intersectsBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public boolean intersectsBox(BlockPos posMin, BlockPos posMax) {
		return this.intersectsBox(posMin.getX(), posMin.getY(), posMin.getZ(), posMax.getX(), posMax.getY(), posMax.getZ());
	}

	public boolean intersectsBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return switch (this.axis) {
			case X -> !(maxX < this.getLayerMin() || minX > this.getLayerMax());
			case Y -> !(maxY < this.getLayerMin() || minY > this.getLayerMax());
			case Z -> !(maxZ < this.getLayerMin() || minZ > this.getLayerMax());
		};

	}

	public int getClampedValue(int value, Axis axis) {
		if (this.axis == axis) {
			return Mth.clamp(value, this.getLayerMin(), this.getLayerMax());
		}

		//return MathHelper.clamp(value, limits.getMinValueForAxis(axis), limits.getMaxValueForAxis(axis));
		return value;
	}

	@Nullable
	public IntBoundingBox getClampedRenderBoundingBox(IntBoundingBox box) {
		if (!this.intersects(box)) {
			return null;
		}

		switch (this.axis) {
			case X: {
				final int xMin = Math.max(box.minX, this.getLayerMin());
				final int xMax = Math.min(box.maxX, this.getLayerMax());
				return IntBoundingBox.createProper(xMin, box.minY, box.minZ, xMax, box.maxY, box.maxZ);
			}
			case Y: {
				final int yMin = Math.max(box.minY, this.getLayerMin());
				final int yMax = Math.min(box.maxY, this.getLayerMax());
				return IntBoundingBox.createProper(box.minX, yMin, box.minZ, box.maxX, yMax, box.maxZ);
			}
			case Z: {
				final int zMin = Math.max(box.minZ, this.getLayerMin());
				final int zMax = Math.min(box.maxZ, this.getLayerMax());
				return IntBoundingBox.createProper(box.minX, box.minY, zMin, box.maxX, box.maxY, zMax);
			}
			default:
				return null;
		}
	}

	/**
	 * Returns a box clamped by the world bounds and this LayerRange,
	 * which is expanded by the expandAmount (if possible) in both
	 * directions on the axis that this LayerRange is set to.
	 */
	public IntBoundingBox getExpandedBox(Level world, int expandAmount) {
		int worldMinH = -30000000;
		int worldMaxH = 30000000;
		int worldMinY = world != null ? world.getMinBuildHeight() : -64;
		int worldMaxY = world != null ? world.getMaxBuildHeight() - 1 : 319;
		int minX = worldMinH;
		int minY = worldMinY;
		int minZ = worldMinH;
		int maxX = worldMaxH;
		int maxY = worldMaxY;
		int maxZ = worldMaxH;

		switch (this.axis) {
			case X:
				minX = Math.max(minX, this.getLayerMin() - expandAmount);
				maxX = Math.min(maxX, this.getLayerMax() + expandAmount);
				break;

			case Y:
				minY = Math.max(minY, this.getLayerMin() - expandAmount);
				maxY = Math.min(maxY, this.getLayerMax() + expandAmount);
				break;

			case Z:
				minZ = Math.max(minZ, this.getLayerMin() - expandAmount);
				maxZ = Math.min(maxZ, this.getLayerMax() + expandAmount);
				break;
		}

		return IntBoundingBox.createProper(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Nullable
	public IntBoundingBox getClampedArea(BlockPos posMin, BlockPos posMax) {
		return this.getClampedArea(posMin.getX(), posMin.getY(), posMin.getZ(), posMax.getX(), posMax.getY(), posMax.getZ());
	}

	@Nullable
	public IntBoundingBox getClampedArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		if (!this.intersectsBox(minX, minY, minZ, maxX, maxY, maxZ)) {
			return null;
		}

		switch (this.axis) {
			case X: {
				final int xMin = Math.max(minX, this.getLayerMin());
				final int xMax = Math.min(maxX, this.getLayerMax());
				return IntBoundingBox.createProper(xMin, minY, minZ, xMax, maxY, maxZ);
			}
			case Y: {
				final int yMin = Math.max(minY, this.getLayerMin());
				final int yMax = Math.min(maxY, this.getLayerMax());
				return IntBoundingBox.createProper(minX, yMin, minZ, maxX, yMax, maxZ);
			}
			case Z: {
				final int zMin = Math.max(minZ, this.getLayerMin());
				final int zMax = Math.min(maxZ, this.getLayerMax());
				return IntBoundingBox.createProper(minX, minY, zMin, maxX, maxY, zMax);
			}
			default:
				return null;
		}
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();

		obj.add("mode", new JsonPrimitive(this.layerMode.name()));
		obj.add("axis", new JsonPrimitive(this.axis.name()));
		obj.add("layer_single", new JsonPrimitive(this.layerSingle));
		obj.add("layer_above", new JsonPrimitive(this.layerAbove));
		obj.add("layer_below", new JsonPrimitive(this.layerBelow));
		obj.add("layer_range_min", new JsonPrimitive(this.layerRangeMin));
		obj.add("layer_range_max", new JsonPrimitive(this.layerRangeMax));
		obj.add("hotkey_range_min", new JsonPrimitive(this.hotkeyRangeMin));
		obj.add("hotkey_range_max", new JsonPrimitive(this.hotkeyRangeMax));

		return obj;
	}

	public static LayerRange createFromJson(JsonObject obj, IRangeChangeListener refresher) {
		LayerRange range = new LayerRange(refresher);
		range.fromJson(obj);
		return range;
	}

	public void fromJson(JsonObject obj) {
		this.layerMode = LayerMode.fromStringStatic(JsonUtils.getString(obj, "mode"));
		this.axis = Axis.byName(JsonUtils.getString(obj, "axis"));
		if (this.axis == null) {
			this.axis = Axis.Y;
		}

		this.layerSingle = JsonUtils.getInteger(obj, "layer_single");
		this.layerAbove = JsonUtils.getInteger(obj, "layer_above");
		this.layerBelow = JsonUtils.getInteger(obj, "layer_below");
		this.layerRangeMin = JsonUtils.getInteger(obj, "layer_range_min");
		this.layerRangeMax = JsonUtils.getInteger(obj, "layer_range_max");
		this.hotkeyRangeMin = JsonUtils.getBoolean(obj, "hotkey_range_min");
		this.hotkeyRangeMax = JsonUtils.getBoolean(obj, "hotkey_range_max");
	}
}
