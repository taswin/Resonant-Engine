package com.resonant.lib.wrapper

import nova.core.util.transform.Vector3d

/**
 * Wraps Direction an adds some extension methods
 * @author Calclavia
 */
object DirectionWrapper {

	implicit def DirectionEnumFacingWrapper(enumFacing: EnumFacing): Direction = Direction.getOrientation(enumFacing.ordinal)

	implicit def EnumFacingDirectionWrapper(forgeDir: Direction): EnumFacing = EnumFacing.getFront(forgeDir.ordinal)

	implicit class DirectionWrap(val underlying: Direction) {
		def offset: Vector3d = new Vector3d(underlying.offsetX, underlying.offsetY, underlying.offsetZ)
	}

}
