package fi.dy.masa.malilib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PositionUtils {
	public static final Direction[] ALL_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	public static final Direction[] VERTICAL_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.UP};

	public static Vec3 modifyValue(CoordinateType type, Vec3 valueIn, double amount) {
		return switch (type) {
			case X -> new Vec3(valueIn.x + amount, valueIn.y, valueIn.z);
			case Y -> new Vec3(valueIn.x, valueIn.y + amount, valueIn.z);
			case Z -> new Vec3(valueIn.x, valueIn.y, valueIn.z + amount);
		};

	}

	public static BlockPos modifyValue(CoordinateType type, BlockPos valueIn, int amount) {
		return switch (type) {
			case X -> new BlockPos(valueIn.getX() + amount, valueIn.getY(), valueIn.getZ());
			case Y -> new BlockPos(valueIn.getX(), valueIn.getY() + amount, valueIn.getZ());
			case Z -> new BlockPos(valueIn.getX(), valueIn.getY(), valueIn.getZ() + amount);
		};

	}

	public static Vec3 setValue(CoordinateType type, Vec3 valueIn, double newValue) {
		return switch (type) {
			case X -> new Vec3(newValue, valueIn.y, valueIn.z);
			case Y -> new Vec3(valueIn.x, newValue, valueIn.z);
			case Z -> new Vec3(valueIn.x, valueIn.y, newValue);
		};

	}

	public static BlockPos setValue(CoordinateType type, BlockPos valueIn, int newValue) {
		return switch (type) {
			case X -> new BlockPos(newValue, valueIn.getY(), valueIn.getZ());
			case Y -> new BlockPos(valueIn.getX(), newValue, valueIn.getZ());
			case Z -> new BlockPos(valueIn.getX(), valueIn.getY(), newValue);
		};

	}

	public static BlockPos getEntityBlockPos(Entity entity) {
		return new BlockPos(Math.floor(entity.getX()), Math.floor(entity.getY()), Math.floor(entity.getZ()));
	}

	/**
	 * Returns the closest direction the given entity is looking towards,
	 * with a vertical/pitch threshold of 60 degrees.
	 *
	 * @param entity
	 * @return
	 */
	public static Direction getClosestLookingDirection(Entity entity) {
		return getClosestLookingDirection(entity, 60);
	}

	/**
	 * Returns the closest direction the given entity is looking towards.
	 *
	 * @param entity
	 * @param verticalThreshold the pitch threshold to return the up or down facing instead of horizontals
	 * @return
	 */
	public static Direction getClosestLookingDirection(Entity entity, float verticalThreshold) {
		if (entity.getXRot() >= verticalThreshold) {
			return Direction.DOWN;
		} else if (entity.getYRot() <= -verticalThreshold) {
			return Direction.UP;
		}

		return entity.getDirection();
	}

	/**
	 * Returns the closest block position directly infront of the
	 * given entity that is not colliding with it.
	 *
	 * @param entity
	 * @return
	 */
	public static BlockPos getPositionInfrontOfEntity(Entity entity) {
		return getPositionInfrontOfEntity(entity, 60);
	}

	/**
	 * Returns the closest block position directly infront of the
	 * given entity that is not colliding with it.
	 *
	 * @param entity
	 * @param verticalThreshold
	 * @return
	 */
	public static BlockPos getPositionInfrontOfEntity(Entity entity, float verticalThreshold) {
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		double w = entity.getBbWidth();
		BlockPos pos = new BlockPos(x, y, z);

		if (entity.getXRot() >= verticalThreshold) {
			return pos.below(1);
		} else if (entity.getXRot() <= -verticalThreshold) {
			return new BlockPos(x, Math.ceil(entity.getBoundingBox().maxY), z);
		}

		y = Math.floor(y + entity.getEyeHeight());

		switch (entity.getDirection()) {
			case EAST:
				return new BlockPos((int) Math.ceil(x + w / 2), (int) y, (int) Math.floor(z));
			case WEST:
				return new BlockPos((int) Math.floor(x - w / 2) - 1, (int) y, (int) Math.floor(z));
			case SOUTH:
				return new BlockPos((int) Math.floor(x), (int) y, (int) Math.ceil(z + w / 2));
			case NORTH:
				return new BlockPos((int) Math.floor(x), (int) y, (int) Math.floor(z - w / 2) - 1);
			default:
		}

		return pos;
	}

	/**
	 * Returns the hit vector at the center point of the given side/face of the given block position.
	 *
	 * @param basePos
	 * @param facing
	 * @return
	 */
	public static Vec3 getHitVecCenter(BlockPos basePos, Direction facing) {
		int x = basePos.getX();
		int y = basePos.getY();
		int z = basePos.getZ();

		return switch (facing) {
			case UP -> new Vec3(x + 0.5, y + 1, z + 0.5);
			case DOWN -> new Vec3(x + 0.5, y, z + 0.5);
			case NORTH -> new Vec3(x + 0.5, y + 0.5, z);
			case SOUTH -> new Vec3(x + 0.5, y + 0.5, z + 1);
			case WEST -> new Vec3(x, y + 0.5, z);
			case EAST -> new Vec3(x + 1, y + 0.5, z + 1);
			default -> new Vec3(x, y, z);
		};
	}

	/**
	 * Returns the part of the block face the player is currently targeting.
	 * The block face is divided into four side segments and a center segment.
	 *
	 * @param originalSide
	 * @param playerFacingH
	 * @param pos
	 * @param hitVec
	 * @return
	 */
	public static HitPart getHitPart(Direction originalSide, Direction playerFacingH, BlockPos pos, Vec3 hitVec) {
		Vec3 positions = getHitPartPositions(originalSide, playerFacingH, pos, hitVec);
		double posH = positions.x;
		double posV = positions.y;
		double offH = Math.abs(posH - 0.5d);
		double offV = Math.abs(posV - 0.5d);

		if (offH > 0.25d || offV > 0.25d) {
			if (offH > offV) {
				return posH < 0.5d ? HitPart.LEFT : HitPart.RIGHT;
			} else {
				return posV < 0.5d ? HitPart.BOTTOM : HitPart.TOP;
			}
		} else {
			return HitPart.CENTER;
		}
	}

	private static Vec3 getHitPartPositions(Direction originalSide, Direction playerFacingH, BlockPos pos, Vec3 hitVec) {
		double x = hitVec.x - pos.getX();
		double y = hitVec.y - pos.getY();
		double z = hitVec.z - pos.getZ();
		double posH = 0;
		double posV = 0;

		switch (originalSide) {
			case DOWN:
			case UP:
				switch (playerFacingH) {
					case NORTH:
						posH = x;
						posV = 1.0d - z;
						break;
					case SOUTH:
						posH = 1.0d - x;
						posV = z;
						break;
					case WEST:
						posH = 1.0d - z;
						posV = 1.0d - x;
						break;
					case EAST:
						posH = z;
						posV = x;
						break;
					default:
				}

				if (originalSide == Direction.DOWN) {
					posV = 1.0d - posV;
				}

				break;
			case NORTH:
			case SOUTH:
				posH = originalSide.getAxisDirection() == AxisDirection.POSITIVE ? x : 1.0d - x;
				posV = y;
				break;
			case WEST:
			case EAST:
				posH = originalSide.getAxisDirection() == AxisDirection.NEGATIVE ? z : 1.0d - z;
				posV = y;
				break;
		}

		return new Vec3(posH, posV, 0);
	}

	/**
	 * Returns the direction the targeted part of the targeting overlay is pointing towards.
	 *
	 * @param side
	 * @param playerFacingH
	 * @param pos
	 * @param hitVec
	 * @return
	 */
	public static Direction getTargetedDirection(Direction side, Direction playerFacingH, BlockPos pos, Vec3 hitVec) {
		Vec3 positions = getHitPartPositions(side, playerFacingH, pos, hitVec);
		double posH = positions.x;
		double posV = positions.y;
		double offH = Math.abs(posH - 0.5d);
		double offV = Math.abs(posV - 0.5d);

		if (offH > 0.25d || offV > 0.25d) {
			if (side.getAxis() == Direction.Axis.Y) {
				if (offH > offV) {
					return posH < 0.5d ? playerFacingH.getCounterClockWise() : playerFacingH.getClockWise();
				} else {
					if (side == Direction.DOWN) {
						return posV > 0.5d ? playerFacingH.getOpposite() : playerFacingH;
					} else {
						return posV < 0.5d ? playerFacingH.getOpposite() : playerFacingH;
					}
				}
			} else {
				if (offH > offV) {
					return posH < 0.5d ? side.getClockWise() : side.getCounterClockWise();
				} else {
					return posV < 0.5d ? Direction.DOWN : Direction.UP;
				}
			}
		}

		return side;
	}

	public enum HitPart {
		CENTER,
		LEFT,
		RIGHT,
		BOTTOM,
		TOP
	}

	public enum CoordinateType {
		X,
		Y,
		Z
	}
}
