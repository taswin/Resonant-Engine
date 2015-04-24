package com.resonant.lib.math.matrix

/**
 * Galois field of two elements.
 * @author Calclavia
 */
object GaloisField {

	/**
	 * Special GF2 Arithmetic with modulus operator.
	 */
	implicit class GF2(val self: Boolean) extends Numeric[Boolean] {

		override def plus(x: Boolean, y: Boolean): Boolean = fromInt((toInt(x) + toInt(y)) % 2)

		override def minus(x: Boolean, y: Boolean): Boolean = fromInt((toInt(x) - toInt(y)) % 2)

		override def times(x: Boolean, y: Boolean): Boolean = fromInt(toInt(x) * toInt(y))

		override def compare(x: Boolean, y: Boolean): Int = toInt(x).compare(toInt(y))

		override def negate(x: Boolean): Boolean = !x

		override def toInt(x: Boolean): Int = if (x) 1 else 0

		override def fromInt(x: Int): Boolean = if (x > 0) true else false

		override def toDouble(x: Boolean): Double = toInt(x)

		override def toFloat(x: Boolean): Float = toInt(x)

		override def toLong(x: Boolean): Long = toInt(x)

		override def toString: String = "GF(" + (if (self) "1" else "0") + ")"
	}

}