package com.resonant.lib.math.matrix

import com.resonant.lib.math.matrix.GaloisField.GF2

/**
 * @author Calclavia
 */
class AdjacencyMatrix[K](rows: Set[K], columns: Set[K]) extends SparseMatrix[K, GF2](rows, columns)(new GF2(false)) {

	def isConnected(from: K, to: K): Boolean = this(from, to) || this(to, from)

	def isBiConnected(from: K, to: K): Boolean = this(from, to) && this(to, from)

	/**
	 * @param node - The node to check
	 * @return Gets a set of nodes that the given node is connected to.
	 */
	def getDirectedTo(node: K): Set[K] = mat.collect { case ((k1, k2), v) if k1 == node && v => k2 }.toSet

	/**
	 * @param node - The node to check
	 * @return Gets a set of nodes that the given node is connected from.
	 */
	def getDirectedFrom(node: K): Set[K] = mat.collect { case ((k1, k2), v) if k2 == node && v => k1 }.toSet

	/**
	 * @return Gets nodes directed both from and to this node.
	 */
	def getDirected(node: K) = getDirectedTo(node) | getDirectedFrom(node)
}
