package com.resonant.core.graph.internal

import java.util.{List => JLIst}

import com.resonant.lib.math.matrix.AdjacencyMatrix

import scala.collection.JavaConversions._
/**
 * A graph that contains getNodes, each with its ability to connect to other getNodes.
 * @author Calclavia
 */
abstract class GraphConnect[N <: Node[_]] extends Graph[N] {
	protected var nodes = Set.empty[N]
	protected[graph] var adjMat: AdjacencyMatrix[N] = null

	def add(node: N) {
		nodes += node
	}

	def remove(node: N) {
		nodes -= node
	}

	//TODO: Collection?
	override def getNodes: JLIst[N] = nodes.toList

	def build() {
		adjMat = new AdjacencyMatrix[N](nodes, nodes)

		for (node <- nodes) {
			for (con <- node.connections) {
				if (nodes.contains(con)) {
					adjMat(node, con.asInstanceOf[N]) = true
				}
			}
		}
	}
}