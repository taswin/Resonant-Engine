package com.resonant.core.graph.internal

import nova.core.util.transform.Matrix

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

	/**
	 * @return The adjacency matrix as a binary matrix.
	 */
	def toMatrix: Matrix = {
		val mat = new Matrix(rows, cols)
		for (i <- 0 until rows; j <- 0 until cols)
			mat(i, j) = if (matrix(i)(j)) 1 else 0
		return mat
	}
}
