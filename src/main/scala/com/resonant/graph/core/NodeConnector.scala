package com.resonant.graph.core

import java.util.{Map => JMap, Set => JSet}

import nova.core.util.Direction

import scala.collection.convert.wrapAll._
import scala.collection.mutable

/**
 * A node that can connect to other nodes.
 *
 * @param parent - The INodeProvider that contains this node.
 * @tparam A - The type of objects this node can connect to.
 * @author Darkguardsman, Calclavia
 */
abstract class NodeConnector[A <: AnyRef](parent: INodeProvider) extends Node(parent) with INodeConnector[A] {
	/**
	 * Connections to other nodes specifically.
	 */
	private val connectionMap = mutable.WeakHashMap.empty[A, Direction]
	/** The bitmask containing sides that this node may connect to */
	var connectionMask = 0x3F
	/** Functional event handler when the connection changes */
	var onConnectionChanged: () => Unit = () => ()
	var isInvalid = false
	/** The bitmask containing the connected sides */
	protected var _connectedMask = 0x00

	/**
	 * Can this node connect with another node?
	 * @param other - Most likely a node, but it can also be another object
	 * @param from - Direction of connection
	 * @return True connection is allowed
	 */
	override def canConnect[B <: A](other: B, from: Direction): Boolean = isValidConnection(other) && canConnect(from)

	def canConnect(from: Direction): Boolean = connectionMask.mask(from) || from == Direction.UNKNOWN

	//TODO: This getClass.isAssignableFrom has issues.
	def isValidConnection(other: AnyRef): Boolean = other != null //&& other.getClass.isAssignableFrom(getClass)

	def connect[B <: A](obj: B, dir: Direction) {
		connectionMap.put(obj, dir)
		_connectedMask = _connectedMask.openMask(dir)
	}

	def disconnect[B <: A](obj: B) {
		connectionMap.remove(obj) match {
			case Some(x) => _connectedMask = _connectedMask.closeMask(x)
			case _ =>
		}
	}

	def disconnect(dir: Direction) {
		if (connectionMap.removeAll(connectionMap.filter(_._2 == dir))) {
			_connectedMask = _connectedMask.closeMask(dir)
		}
	}

	def directionMap: JMap[A, Direction] = connectionMap

	/**
	 * Called to rebuild the connection map.
	 * This is a general way used to search all adjacent TileEntity and attempt a connection
	 */
	override def reconstruct() {
		super.reconstruct()
		val prevCon = connectedMask
		clearConnections()
		rebuild()

		if (prevCon != connectedMask) {
			onConnectionChanged()
		}

		isInvalid = false
	}

	def connectedMask = _connectedMask

	def rebuild() {

	}

	def clearConnections() {
		connectionMap.clear()
		_connectedMask = 0x00
	}

	override def deconstruct() {
		super.deconstruct()
		clearConnections()
		isInvalid = true
	}

	override def toString: String = getClass.getSimpleName + "[Connections: " + connections.size + "]"

	override def connections: JSet[A] = connectionMap.keys.toSet[A]
}