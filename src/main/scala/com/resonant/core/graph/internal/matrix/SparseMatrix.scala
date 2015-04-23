package com.resonant.core.graph.internal.matrix

import scala.reflect.ClassTag

/**
 * A general sparse matrix with generic keys and values.
 * @author Calclavia
 */
class SparseMatrix[K1, K2, V](val rows: Seq[K1], var columns: Seq[K2])(implicit classTag: ClassTag[V]) {

	private val default: V = classTag.newArray(1)(0)
	private var mat = Map.empty[(Int, Int), V].withDefaultValue(default)

	def of(i: Int, j: Int): V = mat(i, j)

	def apply(i: K1, j: K2): V = of(rows.indexOf(i), columns.indexOf(j))

	def update(i: K1, j: K2, value: V) {
		mat += (rows.indexOf(i), columns.indexOf(j)) -> value
	}

	override def equals(obj: Any): Boolean = {
		obj match {
			case b: SparseMatrix[K1, K2, V] =>
				val a = this
				if (b.rows == a.rows && b.columns == b.columns) {
					//TODO: Check equality
					return a.mat.equals(b.mat)
				}
		}
		return false
	}

	override def toString: String = {
		val sb = new StringBuilder
		sb.append("SparseMatrix [" + rows.size + "x" + columns.size + "]\n")
		val averageRowLabelLength = rows.map(_.toString.length).sum / rows.length
		(0 until averageRowLabelLength).foreach(sb.append(" "))
		sb.append(" | ")
		//Print row labels
		columns.foreach(j => {
			val averageRowLength = rows.map(this(_, j)).collect { case Some(element) => element.toString.length }.sum / rows.length
			sb.append(j)
			(0 until averageRowLength).foreach(sb.append(" "))
		})
		sb.append("\n")
		rows.foreach(i => {
			//Print column labels
			sb.append(i + " | ")
			columns.foreach(j => sb.append(this(i, j) + " "))
			sb.append("\n")
		})
		return sb.toString
	}
}
