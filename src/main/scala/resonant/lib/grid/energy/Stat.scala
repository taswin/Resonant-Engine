package resonant.lib.grid.energy

import net.minecraft.nbt.NBTTagCompound
import resonant.lib.utility.nbt.ISaveObj

/**
 * A stat is a type of positive numerical value that has a maximum value
 * @author Calclavia
 */
class Stat[T](implicit n: Numeric[T]) extends Ordered[T] with ISaveObj
{
  private var _value: T = n.zero
  private var _prevValue: T = n.zero
  private var _max: T = n.zero

  def this(newMax: T)(implicit n: Numeric[T])
  {
    this()(n)
    max = newMax
  }

  def value = _value

  def prev = _prevValue

  def value_=(newValue: T)
  {
    _prevValue = _value
    _value = n.min(n.max(newValue, n.zero), max)
  }

  def setValue(newVal: T) = value = newVal

  def max = _max

  def max_=(newMax: T)
  {
    _max = newMax
    value = value
  }

  def setMax(newMax: T) = max = newMax

  def isMax: Boolean = n.gteq(value, max)

  def isMin: Boolean = n.lteq(value, n.zero)

  def isLastEmpty: Boolean = (prev == 0 && !isMin) || (n.gt(prev, n.zero) && isMin)

  /**
   * Returns the difference between the maximum and current value.
   */
  def remaining: T = n.min(max, value)

  def +(amount: T): T = n.plus(value, amount)

  def -(amount: T): T = n.minus(value, amount)

  def +=(amount: T): T =
  {
    value = n.plus(value, amount)
    return value
  }

  def -=(amount: T): T =
  {
    value = n.minus(value, amount)
    return value
  }

  override def compare(that: T): Int = n.compare(value, that)

  override def load(nbt: NBTTagCompound)
  {
    value = nbt.getDouble("value").asInstanceOf[T]
  }

  override def save(nbt: NBTTagCompound)
  {
    nbt.setDouble("value", n.toDouble(value))
  }

  override def toString: String = getClass.getSimpleName + "[" + value + "/" + max + "]"
}
