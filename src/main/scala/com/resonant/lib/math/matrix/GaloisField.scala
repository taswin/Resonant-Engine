package com.resonant.lib.math.matrix

/**
 * Galois field of two elements.
 * @author Calclavia
 */
object GaloisField {

	implicit class GF2(val self: Boolean) extends Numeric[GF2] {

		/** Compares two Boolean expressions and returns `true` if one or both of them evaluate to true.
		  *
		  * `a || b` returns `true` if and only if
		  * - `a` is `true` or
		  * - `b` is `true` or
		  * - `a` and `b` are `true`.
		  *
		  * @note This method uses 'short-circuit' evaluation and
		  *       behaves as if it was declared as `def ||(x: => Boolean): Boolean`.
		  *       If `a` evaluates to `true`, `true` is returned without evaluating `b`.
		  */
		def ||(x: GF2): Boolean = self || x.self

		/** Compares two Boolean expressions and returns `true` if both of them evaluate to true.
		  *
		  * `a && b` returns `true` if and only if
		  * - `a` and `b` are `true`.
		  *
		  * @note This method uses 'short-circuit' evaluation and
		  *       behaves as if it was declared as `def &&(x: => Boolean): Boolean`.
		  *       If `a` evaluates to `false`, `false` is returned without evaluating `b`.
		  */
		def &&(x: GF2): Boolean = self && x.self

		override def plus(x: GF2, y: GF2): GF2 = fromInt((toInt(x) + toInt(y)) % 2)

		override def minus(x: GF2, y: GF2): GF2 = fromInt((toInt(x) - toInt(y)) % 2)

		override def times(x: GF2, y: GF2): GF2 = fromInt(toInt(x) * toInt(y))

		override def compare(x: GF2, y: GF2): Int = toInt(x).compare(toInt(y))

		override def negate(x: GF2): GF2 = !x.self

		override def toInt(x: GF2): Int = if (x.self) 1 else 0

		override def fromInt(x: Int): GF2 = if (x > 0) true else false

		override def toDouble(x: GF2): Double = toInt(x)

		override def toFloat(x: GF2): Float = toInt(x)

		override def toLong(x: GF2): Long = toInt(x)

		override def toString: String = "GF(" + (if (self) "1" else "0") + ")"
	}

}