package resonantengine.lib.wrapper

import nova.core.util.transform.Vector3d

/**
 * Wraps ForgeDirection an adds some extension methods
 * @author Calclavia
 */
object ForgeDirectionWrapper
{

  implicit def ForgeDirectionEnumFacingWrapper(enumFacing : EnumFacing) : ForgeDirection= ForgeDirection.getOrientation(enumFacing.ordinal)

  implicit def EnumFacingForgeDirectionWrapper(forgeDir : ForgeDirection) : EnumFacing = EnumFacing.getFront(forgeDir.ordinal)

	implicit class ForgeDirectionWrap(val underlying: ForgeDirection) {
		def offset: Vector3d = new Vector3d(underlying.offsetX, underlying.offsetY, underlying.offsetZ)
	}
}
