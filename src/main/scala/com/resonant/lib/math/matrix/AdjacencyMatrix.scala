package com.resonant.lib.math.matrix

/**
 * @author Calclavia
 */
class AdjacencyMatrix[K1, K2](rows: Seq[K1], columns: Seq[K2]) extends SparseMatrix[K1, K2, GF2](rows, columns) {

	def isConnected(from: K1, to: K2): Boolean = {
		return this(from, to) || this(to, from)
	}

	def isBiConnected(from: Int, to: Int): Boolean = {
		return mat(from)(to) || mat(to)(from)
	}

	/**
	 * @param node - The node to check
	 * @return Gets a set of nodes that the given node is connected to.
	 */
	def getDirectedTo(node: Int): Set[Int] = (0 until mat(0).size).filter(mat(node)(_)).toSet

	/**
	 * @param node - The node to check
	 * @return Gets a set of nodes that the given node is connected from.
	 */
	def getDirectedFrom(node: Int): Set[Int] = (0 until mat.size).filter(mat(_)(node)).toSet

	def getDirected(node: Int) = getDirectedTo(node) | getDirectedFrom(node)
}
