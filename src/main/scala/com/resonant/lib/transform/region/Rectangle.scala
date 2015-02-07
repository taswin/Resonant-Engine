package com.resonant.lib.transform.region

import java.math.{BigDecimal, MathContext, RoundingMode}

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound

class Rectangle(var min: Vector2, var max: Vector2) extends Shape[Rectangle] {
	def this() = this(new Vector2, new Vector2)

	def this(minX: Double, minY: Double, maxX: Double, maxY: Double) = this(new Vector2(minX, minY), new Vector2(maxX, maxY))

	override def set(other: Rectangle): Rectangle = {
		min = other.min.clone
		max = other.max.clone
		return this
	}

	/**
	 * Operations
	 */
	override def +(amount: Double): Rectangle = new Rectangle(min + amount, max + amount)

	override def +(amount: Rectangle): Rectangle = new Rectangle(min + amount.min, max + amount.max)

	def -(vec: Vector2): Rectangle = this + (vec * -1)

	def +(vec: Vector2): Rectangle = new Rectangle(min + vec, max + vec)

	def -=(vec: Vector2): Rectangle = this += (vec * -1)

	def +=(vec: Vector2): Rectangle = {
		min += vec
		max += vec
		return this
	}

	def *(amount: Double): Rectangle = new Rectangle(min * amount, max * amount)

	def *(amount: Rectangle): Rectangle = new Rectangle(min * amount.min, max * amount.max)

	def this(vec: Vector2, expansion: Double) = this(vec, vec + expansion)

	/** Checks if the point is inside the shape */
	override def isWithin(x: Double, y: Double): Boolean = y >= this.min.y && y <= this.max.y && x >= this.min.x && x <= this.max.x

	def cornerA() = min

	def cornerB() = new Vector2(min.x, max.y)

	def cornerC() = max

	def cornerD() = new Vector2(max.x, min.y)

	/**
	 * Returns whether the given region intersects with this one.
	 */
	def intersects(region: Rectangle): Boolean = {
		return if (region.max.x > this.min.x && region.min.x < this.max.x) (if (region.max.y > this.min.y && region.min.y < this.max.y) true else false) else false
	}

	override def getArea: Double = getSizeX * getSizeY

	override def getSizeX: Double = max.x - min.x

	override def getSizeY: Double = max.y - min.y

	override def writeNBT(nbt: NBTTagCompound): NBTTagCompound = {
		nbt.setTag("min", min.toNBT)
		nbt.setTag("max", max.toNBT)
		return nbt
	}

	override def writeByteBuf(data: ByteBuf): ByteBuf = {
		min.writeByteBuf(data)
		max.writeByteBuf(data)
		return data
	}

	override def toString: String = {
		val cont: MathContext = new MathContext(4, RoundingMode.HALF_UP)
		return "Rectangle[" + new BigDecimal(min.x, cont) + ", " + new BigDecimal(min.y, cont) + "] -> [" + new BigDecimal(max.x, cont) + ", " + new BigDecimal(max.y, cont) + "]"
	}

	override def equals(o: Any): Boolean = {
		if (o.isInstanceOf[Rectangle]) return (min == (o.asInstanceOf[Rectangle]).min) && (max == (o.asInstanceOf[Rectangle]).max)
		return false
	}

	override def clone: Rectangle = new Rectangle(this)

	def this(rect: Rectangle) = this(rect.min.clone, rect.max.clone)
}