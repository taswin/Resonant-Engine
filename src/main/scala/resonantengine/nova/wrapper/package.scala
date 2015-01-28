package resonantengine.nova

import nova.core.entity.Entity
import nova.core.util.transform.{Cuboid, Operator, Vector3d}
import nova.wrapper.mc1710.backward.entity.EntityBackwardWrapper
import nova.wrapper.mc1710.forward.util.CuboidForwardWrapper

/**
 * Wraps NOVA objects and provides them with Scala synthetic sugar coating.
 * @author Calclavia
 */
package object wrapper {

	implicit def wrapEntity(underlying: net.minecraft.entity.Entity): Entity = new EntityBackwardWrapper(underlying)

	/**
	 * Temporary Minecraft Bridge
	 */
	implicit class OperatorWrapper[I <: Operator[I, O], O <: I](underlying: Operator[I, O]) {
		def +(other: I): O = underlying.add(other)

		def +(other: Double): O = underlying.add(other)

		def -(other: I): O = underlying.subtract(other)

		def -(other: Double): O = underlying.subtract(other)

		def *(other: I): O = underlying.multiply(other)

		def *(other: Double): O = underlying.multiply(other)

		def /(other: I): O = underlying.divide(other)

		def /(other: Double): O = underlying.divide(other)

		def unary_+ : O = underlying.asInstanceOf[O]

		def unary_- : O = underlying.inverse()
	}

	implicit class WrapTile(underlying: net.minecraft.tileentity.TileEntity) {
		def getPosition = new Vector3d(underlying.xCoord, underlying.yCoord, underlying.zCoord)
	}

	implicit class WrapEntity(underlying: net.minecraft.entity.Entity) {
		def getPosition = new Vector3d(underlying.posX, underlying.posY, underlying.posZ)
	}

	implicit class WrapCuboid(underlying: Cuboid) {
		def toAABB = new CuboidForwardWrapper(underlying)
	}
}
