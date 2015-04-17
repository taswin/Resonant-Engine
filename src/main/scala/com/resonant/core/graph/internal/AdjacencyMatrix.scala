package com.resonant.core.graph.internal

import java.util

import nova.core.util.transform.Matrix

/**
 * @author Calclavia
 */
class AdjacencyMatrix(val rows: Int, val columns: Int) {

	private val mat = Array.ofDim[Boolean](rows, columns)

	def apply(i: Int)(j: Int) = mat(i)(j)

	def update(i: Int, j: Int, value: Boolean) {
		mat(i)(j) = value
	}

	def isUndirectlyConnected(from: Int, to: Int): Boolean = {
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

	override def equals(obj: Any): Boolean = {
		if (obj.isInstanceOf[AdjacencyMatrix]) {
			val B = obj.asInstanceOf[AdjacencyMatrix]
			val A = this
			if (B.rows == A.rows && B.columns == A.columns) {
				for (i <- 0 until rows; j <- 0 until columns) {
					if (A.mat(i)(j) != B.mat(i)(j)) {
						return false
					}
				}
				return true
			}
		}
		return false
	}

	/**
	 * @return The adjacency matrix as a binary matrix.
	 */
	def toMatrix: Matrix = {
		val newMat = new Matrix(rows, columns)
		for (i <- 0 until rows; j <- 0 until columns)
			newMat(i, j) = if (mat(i)(j)) 1 else 0
		return newMat
	}

	override def toString: String = {
		val sb: StringBuilder = new StringBuilder
		sb.append("Matrix[" + rows + "x" + columns + "]\n")
		(0 until rows).foreach(i => sb.append(util.Arrays.toString(mat(i))).append("\n"))
		return sb.toString
	}
}
