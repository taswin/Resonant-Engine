package com.resonant.lib.math.matrix

import com.resonant.lib.math.matrix.GaloisField.GF2

/**
 * @author Calclavia
 */
class AdjacencyMatrix[K](rows: Set[K], columns: Set[K]) extends SparseMatrix[K, Boolean](rows, columns)(new GF2(false)) {

	def this(rows: Set[K]) = this(rows, rows)

	def isConnected(from: K, to: K): Boolean = this(from, to) || this(to, from)

	def isBiConnected(from: K, to: K): Boolean = this(from, to) && this(to, from)

	/**
	 * Gets a set of nodes that the given node is connected to.
	 * Find the row for the node, and all elements in that row.
	 * @param node - The node to check
	 * @return Gets a set of nodes that the given node is connected to.
	 */
	def getDirectedTo(node: K): Set[K] = mat.collect { case ((k1, k2), v) if k1.equals(node) && v => k2 }.toSet

	/**
	 * Gets a set of nodes that the given node is connected from.
	 * Find the column for the node, and all elements in that column.
	 * @param node - The node to check
	 * @return Gets a set of nodes that the given node is connected from.
	 */
	def getDirectedFrom(node: K): Set[K] = mat.collect { case ((k1, k2), v) if k2.equals(node) && v => k1 }.toSet

	/**
	 * Gets a set of nodes directed both from and to this node.
	 * @return Gets nodes directed both from and to this node.
	 */
	def getDirected(node: K) = getDirectedTo(node) | getDirectedFrom(node)
}
