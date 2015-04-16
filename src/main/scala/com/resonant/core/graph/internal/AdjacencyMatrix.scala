package com.resonant.core.graph.internal

/**
 * @author Calclavia
 */
class AdjacencyMatrix(val rows: Int, val cols: Int) {

	private val matrix = Array.ofDim[Boolean](rows, cols)

	def apply(i: Int)(j: Int) = matrix(i)(j)

	def update(i: Int, j: Int, value: Boolean) {
		matrix(i)(j) = value
	}

	def isUndirectlyConnected(from: Int, to: Int): Boolean = {
		return matrix(from)(to) || matrix(to)(from)
	}

	/**
	 * @param node - The node to check
	 * @return Gets a set of getNodes that the given node is connected to.
	 */
	def getDirectedTo(node: Int): Array[Int] = (0 until matrix(0).size).filter(matrix(node)(_)).toArray

	/**
	 * @param node - The node to check
	 * @return Gets a set of getNodes that the given node is connected from.
	 */
	def getDirectedFrom(node: Int): Array[Int] = (0 until matrix.size).filter(matrix(_)(node)).toArray
}
