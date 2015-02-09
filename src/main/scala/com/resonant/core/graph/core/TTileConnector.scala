package com.resonant.core.graph.core

import net.minecraft.tileentity.TileEntity
import nova.core.util.Direction

/**
 * A trait applied to NodeConnector
 * @author Calclavia
 */
trait TTileConnector[A <: AnyRef] extends NodeConnector[A] {
	override def rebuild() {
		super.rebuild()

		Direction.VALID_DIRECTIONS.foreach(toDir => {
			if (canConnect(toDir)) {
				val tile = (position + toDir).getTileEntity

				if (tile != null) {
					val node = getNodeFrom(tile, toDir.getOpposite)

					if (node != null && canConnect(node.asInstanceOf[A], toDir) && node.asInstanceOf[NodeConnector[A]].canConnect(this.asInstanceOf[A], toDir.getOpposite)) {
						connect(node.asInstanceOf[A], toDir)
					}
					else if (tile.getClass.isAssignableFrom(getCompareClass)) {
						connect(tile.asInstanceOf[A], toDir)
					}
				}
			}
		})
	}

	protected def getNodeFrom(tile: TileEntity, from: Direction): INode = {
		if (tile.isInstanceOf[INodeProvider]) {
			val node = tile.asInstanceOf[INodeProvider].getNode(getCompareClass, from)

			if (node != null) {
				return node
			}
		}

		return null
	}

	/**
	 * The class used to compare when making connections
	 */
	protected def getCompareClass: Class[_ <: A with INode]
}
