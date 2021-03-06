package resonant.lib.transform.region

import java.math.{BigDecimal, MathContext, RoundingMode}

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import resonant.lib.transform.AbstractOperation
import resonant.lib.transform.vector.Vector2

class Rectangle(var min: Vector2, var max: Vector2) extends AbstractOperation[Rectangle]
{
  def this() = this(new Vector2, new Vector2)

  def this(vec: Vector2, expansion: Double) = this(vec, vec + expansion)

  def this(minX: Double, minY: Double, maxX: Double, maxY: Double) = this(new Vector2(minX, minY), new Vector2(maxX, maxY))

  def this(rect: Rectangle) = this(rect.min.clone, rect.max.clone)

  override def set(other: Rectangle): Rectangle =
  {
    min = other.min.clone
    max = other.max.clone
    return this
  }

  /**
   * Conversion
   */
  override def round: Rectangle = new Rectangle(min.round, max.round)

  override def ceil: Rectangle = new Rectangle(min.ceil, max.ceil)

  override def floor: Rectangle = new Rectangle(min.floor, max.floor)

  override def max(other: Rectangle): Rectangle = new Rectangle(min.max(other.min), max.max(other.max))

  override def min(other: Rectangle): Rectangle = new Rectangle(min.min(other.min), max.min(other.max))

  override def reciprocal(): Rectangle = new Rectangle(min.reciprocal, max.reciprocal)

  /**
   * Operations
   */
  override def +(amount: Double): Rectangle = new Rectangle(min + amount, max + amount)

  override def +(amount: Rectangle): Rectangle = new Rectangle(min + amount.min, max + amount.max)

  def +(vec: Vector2): Rectangle = new Rectangle(min + vec, max + vec)

  def +=(vec: Vector2): Rectangle =
  {
    min += vec
    max += vec
    return this
  }

  def -(vec: Vector2): Rectangle = this + (vec * -1)

  def -=(vec: Vector2): Rectangle = this += (vec * -1)

  def add(vec: Vector2): Rectangle = this + vec

  def addSet(vec: Vector2): Rectangle = this += vec

  def subtract(vec: Vector2): Rectangle = this - vec

  def subtractSet(vec: Vector2): Rectangle = this -= vec

  def *(amount: Double): Rectangle = new Rectangle(min * amount, max * amount)

  def *(amount: Rectangle): Rectangle = new Rectangle(min * amount.min, max * amount.max)

  /**
   * Checks if a point is located inside a region
   */
  def intersects(point: Vector2): Boolean =
  {
    return (point.x > this.min.x && point.x < this.max.x) && (point.y > this.min.y && point.y < this.max.y)
  }

  /**
   * Returns whether the given region intersects with this one.
   */
  def intersects(region: Rectangle): Boolean =
  {
    return if (region.max.x > this.min.x && region.min.x < this.max.x) (if (region.max.y > this.min.y && region.min.y < this.max.y) true else false) else false
  }

  override def writeNBT(nbt: NBTTagCompound): NBTTagCompound =
  {
    nbt.setTag("min", min.toNBT)
    nbt.setTag("max", max.toNBT)
    return nbt
  }

  override def writeByteBuf(data: ByteBuf): ByteBuf =
  {
    min.writeByteBuf(data)
    max.writeByteBuf(data)
    return data
  }

  override def toString: String =
  {
    val cont: MathContext = new MathContext(4, RoundingMode.HALF_UP)
    return "Rectangle[" + new BigDecimal(min.x, cont) + ", " + new BigDecimal(min.y, cont) + "] -> [" + new BigDecimal(max.x, cont) + ", " + new BigDecimal(max.y, cont) + "]"
  }

  override def equals(o: Any): Boolean =
  {
    if (o.isInstanceOf[Rectangle]) return (min == (o.asInstanceOf[Rectangle]).min) && (max == (o.asInstanceOf[Rectangle]).max)
    return false
  }

  override def clone: Rectangle = new Rectangle(this)
}