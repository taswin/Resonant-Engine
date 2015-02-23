package com.resonant.core.prefab.block

import java.util
import java.util.{List => JList}

import com.resonant.core.graph.api.NodeProvider
import com.resonant.core.graph.internal.{Graph, GraphProvider, Node}
import com.resonant.wrapper.core.api.tile.DebugInfo
import nova.core.block.Block
import nova.core.block.components.Stateful
import nova.core.game.Game
import nova.core.retention.Storable
import nova.core.util.Direction
import nova.core.util.transform.Vector3i

import scala.collection.convert.wrapAll._

/**
 * @author Calclavia
 */
trait NodeBlockProvider extends Block with Stateful with Storable with NodeProvider with DebugInfo {

	protected var nodes = Set.empty[Node[_ <: Node[_]]]

	override def awake() {
		super.awake()

		if (Game.instance.networkManager.isServer) {
			nodes
				.filter(_.isInstanceOf[GraphProvider[_ <: Graph[_]]])
				.map(_.asInstanceOf[GraphProvider[_ <: Graph[_]]])
				.foreach(_.getGraph.markBuild())
		}
	}

	override def load() {
		if (Game.instance.networkManager.isServer) {
			nodes
				.filter(_.isInstanceOf[GraphProvider[_ <: Graph[_]]])
				.map(_.asInstanceOf[GraphProvider[_ <: Graph[_]]])
				.foreach(_.getGraph.markBuild())
		}
	}

	override def onNeighborChange(neighborPosition: Vector3i) {
		super.onNeighborChange(neighborPosition)
		if (Game.instance.networkManager.isServer) {
			nodes
				.filter(_.isInstanceOf[GraphProvider[_ <: Graph[_]]])
				.map(_.asInstanceOf[GraphProvider[_ <: Graph[_]]])
				.foreach(_.getGraph.markBuild())
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

	override def getNodes(from: Direction): util.Set[Node[_ <: Node[_]]] = nodes

	override def getDebugInfo: JList[String] = {
		val debugs = nodes.toList.filter(_.isInstanceOf[DebugInfo])

		if (debugs.size > 0) {
			return debugs.map(_.asInstanceOf[DebugInfo].getDebugInfo.toList).reduceLeft(_ ::: _)
		}

		return List[String]()
	}
}
