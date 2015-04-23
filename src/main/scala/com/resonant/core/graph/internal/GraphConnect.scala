package com.resonant.core.graph.internal

import java.util.{List => JLIst}

import com.resonant.core.graph.internal.matrix.AdjacencyMatrix

import scala.collection.JavaConversions._
/**
 * A graph that contains getNodes, each with its ability to connect to other getNodes.
 * @author Calclavia
 */
abstract class GraphConnect[N <: Node[_]] extends Graph[N] {

	protected var nodes = List.empty[N]
	protected[graph] var adjMat: AdjacencyMatrix = null

	def add(node: N) {
		if (!nodes.contains(node)) {
			nodes :+= node
		}
	}

	def remove(node: N) {
		if (nodes.contains(node)) {
			nodes.diff(List(node))
		}
	}

	def id(node: AnyRef): Int = {
		assert(nodes.contains(node))
		return nodes.indexOf(node)
	}

	override def getNodes: JLIst[N] = nodes

	def isConnected(from: N, to: N): Boolean = adjMat(id(from))(id(to))

	def build() {
		adjMat = new AdjacencyMatrix(nodes.size, nodes.size)

		for (node <- nodes) {
			for (con <- node.connections) {
				if (nodes.contains(con)) {
					adjMat(id(node), id(con.asInstanceOf[N])) = true
				}
			}
		}
	}
}