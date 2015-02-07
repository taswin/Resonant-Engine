package com.resonant.graph.core

import scala.collection.convert.wrapAll._

/**
 * A grid that contains nodes where all nodes are interconnected
 *
 * @param N The type of node we can connect
 * @author Calclavia
 */
class GridNode[N <: NodeGrid[N]] extends Grid[N] {
	/**
	 * Rebuild the node list starting from the first node and recursively iterating through its connections.
	 */
	def reconstruct(first: N) {
		//TODO: Reconstruct may be called MANY times unnecessarily multiple times. Add check to prevent extra calls
		clear()
		populate(first)
		getNodes.foreach(_.onGridReconstruct())
	}

	def deconstruct(first: N) {
		remove(first)
		first.setGrid(null)

		getNodes.toList.foreach(n => {
			if (n.grid == this) {
				n.setGrid(null)
				n.reconstruct()
			}
		})
	}

	/**
	 * Populates the node list recursively
	 */
	protected def populate(node: N, prev: N = null.asInstanceOf[N]) {
		if (!getNodes.contains(node) && isValidNode(node)) {
			add(node)
			populateNode(node, prev)
			node.connections.foreach(n => populate(n, node))
		}
	}

	protected def populateNode(node: N, prev: N = null.asInstanceOf[N]) {
		if (node.grid != this) {
			node.grid.remove(node)
			node.setGrid(this)
		}
	}
}