package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class IntBoundingBox {
	public final int minX;
	public final int minY;
	public final int minZ;
	public final int maxX;
	public final int maxY;
	public final int maxZ;

	public IntBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public boolean containsPos(Vec3i pos) {
		return pos.getX() >= this.minX &&
				       pos.getX() <= this.maxX &&
				       pos.getZ() >= this.minZ &&
				       pos.getZ() <= this.maxZ &&
				       pos.getY() >= this.minY &&
				       pos.getY() <= this.maxY;
	}

	public boolean containsPos(long pos) {
		int x = BlockPos.getX(pos);
		int y = BlockPos.getY(pos);
		int z = BlockPos.getZ(pos);

		return x >= this.minX && y >= this.minY && z >= this.minZ &&
				       x <= this.maxX && y <= this.maxY && z <= this.maxZ;
	}

	public boolean intersects(IntBoundingBox box) {
		return this.maxX >= box.minX &&
				       this.minX <= box.maxX &&
				       this.maxZ >= box.minZ &&
				       this.minZ <= box.maxZ &&
				       this.maxY >= box.minY &&
				       this.minY <= box.maxY;
	}

	public int getMinValueForAxis(Direction.Axis axis) {
		return switch (axis) {
			case X -> this.minX;
			case Y -> this.minY;
			case Z -> this.minZ;
		};

	}

	public int getMaxValueForAxis(Direction.Axis axis) {
		return switch (axis) {
			case X -> this.maxX;
			case Y -> this.maxY;
			case Z -> this.maxZ;
		};

	}

	public BoundingBox toVanillaBox() {
		return new BoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public IntArrayTag toNBTIntArray() {
		return new IntArrayTag(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
	}

	public static IntBoundingBox fromVanillaBox(BoundingBox box) {
		return createProper(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
	}

	public static IntBoundingBox createProper(int x1, int y1, int z1, int x2, int y2, int z2) {
		return new IntBoundingBox(
				Math.min(x1, x2),
				Math.min(y1, y2),
				Math.min(z1, z2),
				Math.max(x1, x2),
				Math.max(y1, y2),
				Math.max(z1, z2));
	}

	public static IntBoundingBox createForWorldBounds(@Nullable Level world) {
		int worldMinH = -30000000;
		int worldMaxH = 30000000;
		int worldMinY = world != null ? world.getMinBuildHeight() : -64;
		int worldMaxY = world != null ? world.getMaxBuildHeight() - 1 : 319;

		return new IntBoundingBox(worldMinH, worldMinY, worldMinH, worldMaxH, worldMaxY, worldMaxH);
	}

	public static IntBoundingBox fromArray(int[] coords) {
		if (coords.length == 6) {
			return new IntBoundingBox(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
		} else {
			return new IntBoundingBox(0, 0, 0, 0, 0, 0);
		}
	}

	public IntBoundingBox expand(int amount) {
		return this.expand(amount, amount, amount);
	}

	public IntBoundingBox expand(int x, int y, int z) {
		return new IntBoundingBox(this.minX - x, this.minY - y, this.minZ - z,
				this.maxX + x, this.maxY + y, this.maxZ + z);
	}

	public IntBoundingBox shrink(int x, int y, int z) {
		return this.expand(-x, -y, -z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.maxX;
		result = prime * result + this.maxY;
		result = prime * result + this.maxZ;
		result = prime * result + this.minX;
		result = prime * result + this.minY;
		result = prime * result + this.minZ;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}

		IntBoundingBox other = (IntBoundingBox) obj;

		return this.maxX == other.maxX &&
				       this.maxY == other.maxY &&
				       this.maxZ == other.maxZ &&
				       this.minX == other.minX &&
				       this.minY == other.minY &&
				       this.minZ == other.minZ;
	}
}
