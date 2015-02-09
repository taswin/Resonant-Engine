package com.resonant.api.graph.core

import java.util.{Set => JSet}

import scala.beans.BeanProperty
import scala.collection.convert.wrapAll._
import scala.collection.mutable

/**
 * Collection of nodes patterened in a grid
 */
class Grid[N <: AnyRef] extends IGraph[N] {
	private val _nodes = mutable.WeakHashMap.empty[N, Boolean]
	//    Collections.newSetFromMap(mutable.WeakHashMap.empty[N,Boolean])

	@BeanProperty
	var nodeClass: Class[N] = null

	/**
	 * Destroys the grid and all of its data
	 */
	def deconstruct() {
		clear()
	}

	def clear() {
		_nodes synchronized {
			_nodes.clear()
		}
	}

	/**
	 * Called to rebuild the grid node by node
	 */
	def reconstruct() {

	}

	/**
	 * Checks to see if the node is valid
	 */
	def isValidNode(node: AnyRef): Boolean = nodeClass == null || node != null && nodeClass.isAssignableFrom(node.getClass)

	/**
	 * Adds an object to the node list
	 */
	def add(node: N) {
		_nodes synchronized {
			_nodes += node -> false
		}
	}

	/**
	 * Removes a node from the node list.
	 */
	def remove(node: N) {
		_nodes synchronized {
			_nodes -= node
		}
	}

	/**
	 * Gets the list of all nodes
	 */
	def getNodes: JSet[N] = nodes

	override def toString = getClass.getSimpleName + "[" + hashCode + ", Nodes: " + nodes.size + "]"

	def nodes = _nodes.keySet
}