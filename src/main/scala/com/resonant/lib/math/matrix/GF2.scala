package com.resonant.lib.math.matrix

/**
 * Galois field of two elements.
 * @author Calclavia
 */
final class GF2(val self: Boolean) extends AnyVal with Numeric[Boolean] {
	override def plus(x: Boolean, y: Boolean): Boolean = fromInt(toInt(x) + toInt(y))

	override def toDouble(x: Boolean): Double = if (x) 1 else 0

	override def toFloat(x: Boolean): Float = if (x) 1 else 0

	override def toInt(x: Boolean): Int = if (x) 1 else 0

	override def negate(x: Boolean): Boolean = !x

	override def fromInt(x: Int): Boolean = if (x > 0) true else false

	override def toLong(x: Boolean): Long = if (x) 1 else 0

	override def times(x: Boolean, y: Boolean): Boolean = fromInt(toInt(x) * toInt(y))

	override def minus(x: Boolean, y: Boolean): Boolean = fromInt(toInt(x) - toInt(y))

	override def compare(x: Boolean, y: Boolean): Int = toInt(x).compare(toInt(y))
}
