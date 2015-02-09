package com.resonant.core.prefab.block

import java.util
import java.util.{List => JList}

import com.resonant.core.graph.api.INodeProvider
import com.resonant.core.graph.api.node.INode
import com.resonant.wrapper.core.api.tile.IDebugInfo
import nova.core.block.Block
import nova.core.block.components.Stateful
import nova.core.network.NetworkManager
import nova.core.util.Direction
import nova.core.util.components.Storable
import nova.core.util.transform.Vector3i

import scala.collection.convert.wrapAll._

/**
 * @author Calclavia
 */
trait NodeProvider extends Block with Stateful with Storable with INodeProvider with IDebugInfo {
	
	protected val nodes = new util.HashSet[Node]

	override def awake() {
		super.awake()

		if (NetworkManager.instance.get().isServer) {
			nodes.foreach(_.reconstruct())
		}
	}

	override def load() {
		if (NetworkManager.instance.get().isServer) {
			nodes.foreach(_.reconstruct())
		}
	}

	override def onNeighborChange(neighborPosition: Vector3i) {
		super.onNeighborChange(neighborPosition)
		if (NetworkManager.instance.get().isServer) {
			nodes.foreach(_.reconstruct())
		}
	}

	override def unload() {
		if (NetworkManager.instance.get().isServer) {
			nodes.foreach(_.deconstruct())
		}
	}

	override def save(data: util.Map[String, AnyRef]) {
		super.save(data)
		nodes.filter(_.isInstanceOf[Storable]).foreach(_.asInstanceOf[Storable].save(data))
	}

	override def load(data: util.Map[String, AnyRef]) {
		super.load(data)
		nodes.filter(_.isInstanceOf[Storable]).foreach(_.asInstanceOf[Storable].load(data))
	}

	override def getNode[N <: INode](nodeType: Class[_ <: N], from: Direction): N = {
		return nodes.filter(node => nodeType.isAssignableFrom(node.getClass)).headOption.getOrElse(null).asInstanceOf[N]
	}

	override def getDebugInfo: JList[String] = {
		val debugs = nodes.toList.filter(_.isInstanceOf[IDebugInfo])

		if (debugs.size > 0) {
			return debugs.map(_.asInstanceOf[IDebugInfo].getDebugInfo.toList).reduceLeft(_ ::: _)
		}

		return List[String]()
	}
}
