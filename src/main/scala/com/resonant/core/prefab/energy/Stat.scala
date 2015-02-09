package com.resonant.core.prefab.energy

import net.minecraft.nbt.NBTTagCompound

/**
 * A stat is a type of positive numerical value that has a maximum value
 * @author Calclavia
 */
class Stat[T](implicit n: Numeric[T]) extends Ordered[T] with ISave {
	private var _value: T = n.zero
	private var _prevValue: T = n.zero
	private var _max: T = n.zero

	def this(newMax: T)(implicit n: Numeric[T]) {
		this()(n)
		max = newMax
	}

	def setValue(newVal: T) = value = newVal

	def isMax: Boolean = n.gteq(value, max)

	def setMax(newMax: T) = max = newMax

	def isLastEmpty: Boolean = (prev == 0 && !isMin) || (n.gt(prev, n.zero) && isMin)

	def prev = _prevValue

	def isMin: Boolean = n.lteq(value, n.zero)

	/**
	 * Returns the difference between the maximum and current value.
	 */
	def remaining: T = n.min(max, value)

	def +(amount: T): T = n.plus(value, amount)

	def -(amount: T): T = n.minus(value, amount)

	def +=(amount: T): T = {
		value = n.plus(value, amount)
		return value
	}

	def value = _value

	def value_=(newValue: T) {
		_prevValue = _value
		_value = n.min(n.max(newValue, n.zero), max)
	}

	def -=(amount: T): T = {
		value = n.minus(value, amount)
		return value
	}

	override def compare(that: T): Int = n.compare(value, that)

	override def load(nbt: NBTTagCompound) {
		max = nbt.getDouble("statMax").asInstanceOf[T]
		value = nbt.getDouble("statValue").asInstanceOf[T]
	}

	def max = _max

	def max_=(newMax: T) {
		_max = newMax
		value = value
	}

	override def save(nbt: NBTTagCompound) {
		nbt.setDouble("statMax", n.toDouble(max))
		nbt.setDouble("statValue", n.toDouble(value))
	}

	override def toString: String = getClass.getSimpleName + "[" + value + "/" + max + "]"
}