package com.resonant.core.lib.util;

import nova.core.util.Direction;
import nova.core.util.transform.Vector3d;

public class MathUtility {
	/**
	 * Interpolates between point a and point b
	 * @param f A percentage value between 0 to 1
	 * @return The interpolated value
	 */
	public static double lerp(double a, double b, double f) {
		return a + f * (b - a);
	}

	public static float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

	/**
	 * Caps a value between -bounds to +bounds
	 * @return A value capped between two bounds.
	 */
	public static double absCap(double value, double bounds) {
		return Math.min(Math.max(value, -bounds), bounds);
	}

	public static float absCap(float value, float bounds) {
		return Math.min(Math.max(value, -bounds), bounds);
	}

	/**
	 * @param vec - Vector3d that is on the sphere
	 * @return new Vector3d(radius, inclination, azimuth)
	 */
	public static Vector3d vecToSphereAngles(Vector3d vec) {
		double radius = Math.sqrt((vec.x * vec.x) + (vec.y * vec.y) + (vec.z * vec.z));
		double inclination = Math.acos(vec.z / radius);
		double azimuth = Math.atan(vec.y / vec.z);
		return new Vector3d(radius, inclination, azimuth);
	}

	/**
	 * Turns radius and sphere cords into a Vector3d
	 * @param radius - sphere radius
	 * @param inclination -
	 * @param azimuth
	 * @return Vector3d(x, y, z)
	 */
	public static Vector3d sphereAnglesToVec(Double radius, Double inclination, Double azimuth) {
		double x = radius * Math.sin(inclination) * Math.cos(azimuth);
		double y = radius * Math.sin(inclination) * Math.sin(azimuth);
		double z = radius * Math.cos(inclination);

		return new Vector3d(x, y, z);
	}

	/**
	 * Clamps the angles to a min max by adding or subtracting the min max. This way it maintanes
	 * the change in angle in the chance it goes out of bounds
	 */
	public static float clampAngle(float var, float min, float max) {
		while (var < min) {
			var += 360;
		}

		while (var > max) {
			var -= 360;
		}

		return var;
	}

	public static double clampAngle(double var, double min, float max) {
		while (var < min) {
			var += max;
		}
		while (var > max) {
			var -= max;
		}
		return var;
	}

	public static float clamp(float var, float min, float max) {
		if (var < min) {
			return min;
		} else if (var > max) {
			return max;
		} else {
			return var;
		}
	}

	/**
	 * Clamps an angle to 360 degree circle
	 */
	public static float clampAngleTo360(float var) {
		return MathUtility.clampAngle(var, 0, 360);
	}

	public static double clampAngleTo360(double var) {
		return MathUtility.clampAngle(var, 0, 360);
	}

	public static float clampAngleTo180(float var) {
		return MathUtility.clampAngle(var, -180, 180);
	}

	public static double clampAngleTo180(double var) {
		return MathUtility.clampAngle(var, -180, 180);
	}

	/**
	 * Find the shortest delta change to the angle goal from the current angle
	 */
	public static float shortestAngleTo360(float angle, float angleGoal) {
		angle = clampAngleTo360(angle);
		angleGoal = clampAngleTo360(angleGoal);

		if (angle == angleGoal) {
			return 0;
		} else if (angle > angleGoal) {
			return angleGoal - angle;
		} else {
			return angle - angleGoal;
		}
	}

	public static double updateRotation(double from, double to, double speed) {
		from = net.minecraft.util.MathHelper.wrapAngleTo180_double(from);
		to = net.minecraft.util.MathHelper.wrapAngleTo180_double(to);
		double delta = Math.abs(from - to);
		if (delta > 0.001f) {
			if (from > to) {
				from += (delta >= 0) ? speed : -speed;
			} else {
				from += (delta >= 0) ? -speed : speed;
			}

			if (delta < speed + 0.1f) {
				from = to;
			}
		}
		return from;
	}

	public static double updateRotation(float from, float to, float speed) {
		from = net.minecraft.util.MathHelper.wrapAngleTo180_float(from);
		to = net.minecraft.util.MathHelper.wrapAngleTo180_float(to);
		double delta = Math.abs(from - to);
		if (delta > 0.001f) {
			if (from > to) {
				from += (delta >= 0) ? speed : -speed;
			} else {
				from += (delta >= 0) ? -speed : speed;
			}

			if (delta < speed + 0.1f) {
				from = to;
			}
		}
		return from;
	}

	/**
	 * gets the facing direction using the yaw angle
	 */
	public static Direction getFacingDirectionFromAngle(float yaw) {
		float angle = net.minecraft.util.MathHelper.wrapAngleTo180_float(yaw);
		if (angle >= -45 && angle <= 45) {
			return Direction.SOUTH;
		} else if (angle >= 45 && angle <= 135) {

			return Direction.WEST;
		} else if (angle >= 135 && angle <= -135) {

			return Direction.NORTH;
		} else {
			return Direction.EAST;
		}
	}

	/**
	 * gets the facing direction using the yaw angle
	 */
	public static Direction getFacingDirectionFromAngle(double yaw) {
		return getFacingDirectionFromAngle((float) yaw);
	}

	/**
	 * Gets the volume of a sphere
	 * @param radius - distance from center
	 * @return exact volume
	 */
	public static double getSphereVolume(double radius) {
		return (4 * Math.PI * (radius * radius * radius)) / 3;
	}
}
