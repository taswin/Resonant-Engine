package resonant.lib.transform.vector

import io.netty.buffer.ByteBuf
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{MovingObjectPosition, Vec3}
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.util.ForgeDirection

class VectorWorld(var world: World, newX: Double, newY: Double, newZ: Double) extends Vector3(newX, newY, newZ)
{
  def this(nbt: NBTTagCompound) = this(DimensionManager.getWorld(nbt.getInteger("dimension")), nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"))

  def this(data: ByteBuf) = this(DimensionManager.getWorld(data.readInt()), data.readDouble(), data.readDouble(), data.readDouble())

  def this(entity: Entity) = this(entity.worldObj, entity.posX, entity.posY, entity.posZ)

  def this(tile: TileEntity) = this(tile.getWorldObj, tile.xCoord, tile.yCoord, tile.zCoord)

  def this(vec: IVectorWorld) = this(vec.world, vec.x, vec.y, vec.z)

  def this(world: World, vector: Vector3) = this(world, vector.x, vector.y, vector.z)

  def this(world: World, vector: IVector3) = this(world, vector.x, vector.y, vector.z)

  def this(world: World, vec: Vec3) = this(world, vec.xCoord, vec.yCoord, vec.zCoord)

  def this(world: World, target: MovingObjectPosition) = this(world, target.hitVec)

  def world(newWorld: World)
  {
    world = newWorld
  }

  /**
   * Conversions
   */
  override def writeNBT(nbt: NBTTagCompound): NBTTagCompound =
  {
    nbt.setInteger("dimension", world.provider.dimensionId)
    nbt.setDouble("x", x)
    nbt.setDouble("y", y)
    nbt.setDouble("z", z)
    return nbt
  }

  override def writeByteBuf(data: ByteBuf): ByteBuf =
  {
    data.writeInt(world.provider.dimensionId)
    data.writeDouble(x)
    data.writeDouble(y)
    data.writeDouble(z)
    return data
  }

  def toVector3 = new Vector3(x, y, z)

  override def add(x: Double, y: Double, z: Double): VectorWorld = this +(x, y, z)

  override def +(x: Double, y: Double, z: Double): VectorWorld = new VectorWorld(world, this.x + x, this.y + y, this.z + z)

  override def addEquals(x: Double, y: Double, z: Double): VectorWorld = this +=(x, y, z)

  override def +=(x: Double, y: Double, z: Double): VectorWorld = set(new VectorWorld(world, this.x + x, this.y + y, this.z + z))

  override def set(vec: IVector3): VectorWorld =
  {
    if (vec.isInstanceOf[VectorWorld])
      world = vec.asInstanceOf[VectorWorld].world
    x = vec.x
    y = vec.y
    z = vec.z
    return this
  }

  override def add(amount: ForgeDirection): VectorWorld = this + amount

  override def +(amount: ForgeDirection): VectorWorld = this + new Vector3(amount)

  override def addEquals(amount: ForgeDirection): VectorWorld = this += amount

  override def +=(amount: ForgeDirection): VectorWorld = set(this + new Vector3(amount))

  override def /(amount: IVector3): VectorWorld = new VectorWorld(world, x / amount.x, y / amount.y, z / amount.z)

  /**
   * "Generated" Alias Operation Methods override
   */
  override def add(amount: Double): VectorWorld = this + amount

  /**
   * Operations
   */
  override def +(amount: Double): VectorWorld = new VectorWorld(world, x + amount, y + amount, z + amount)

  override def add(amount: IVector3): VectorWorld = this + amount

  override def +(amount: IVector3): VectorWorld = new VectorWorld(world, x + amount.x, y + amount.y, z + amount.z)

  override def subtract(amount: Double): VectorWorld = this - amount

  /**
   * "Generated" method override
   */
  override def -(amount: Double): VectorWorld = new VectorWorld(world, x - amount, y - amount, z - amount)

  override def subtract(amount: IVector3): VectorWorld = this - amount

  override def -(amount: IVector3): VectorWorld = new VectorWorld(world, x - amount.x, y - amount.y, z - amount.z)

  override def multiply(amount: Double): VectorWorld = this * amount

  override def multiply(amount: IVector3): VectorWorld = this * amount

  override def *(amount: IVector3): VectorWorld = new VectorWorld(world, x * amount.x, y * amount.y, z * amount.z)

  override def divide(amount: Double): VectorWorld = this / amount

  override def /(amount: Double): VectorWorld = this * (1 / amount)

  override def *(amount: Double): VectorWorld = new VectorWorld(world, x * amount, y * amount, z * amount)

  override def addEquals(amount: Double): VectorWorld = this += amount

  override def +=(amount: Double): VectorWorld =
  {
    x += amount
    y += amount
    z += amount
    return this
  }

  override def addEquals(amount: IVector3): VectorWorld = this += amount

  override def +=(amount: IVector3): VectorWorld =
  {
    x += amount.x
    y += amount.y
    z += amount.z
    return this
  }

  override def subtractEquals(amount: Double): VectorWorld = this -= amount

  override def -=(amount: Double): VectorWorld = this += -amount

  override def subtractEquals(amount: IVector3): VectorWorld = this -= amount

  override def -=(amount: IVector3): VectorWorld =
  {
    x -= amount.x
    y -= amount.y
    z -= amount.z
    return this
  }

  override def multiplyEquals(amount: Double): VectorWorld = this *= amount

  override def *=(amount: Double): VectorWorld =
  {
    x *= amount
    y *= amount
    z *= amount
    return this
  }

  override def multiplyEquals(amount: IVector3): VectorWorld = this *= amount

  override def *=(amount: IVector3): VectorWorld =
  {
    x *= amount.x
    y *= amount.y
    z *= amount.z
    return this
  }

  override def divideEquals(amount: Double): VectorWorld = this /= amount

  override def /=(amount: Double): VectorWorld = this *= (1 / amount)

  override def divideEquals(amount: IVector3): VectorWorld = this /= amount

  override def /=(amount: IVector3): VectorWorld =
  {
    x *= amount.x
    y *= amount.y
    z *= amount.z
    return this
  }

  /**
   * World Access
   */
  def getBlock: Block = if (world != null) super.getBlock(world) else null

  def getBlockMetadata: Int = if (world != null) super.getBlockMetadata(world) else -1

  def getTileEntity: TileEntity = if (world != null) super.getTileEntity(world) else null

  def getHardness(): Float = super.getHardness(world)

  def getResistance(cause: Entity, xx: Double, yy: Double, zz: Double): Float =
  {
    return getBlock(world).getExplosionResistance(cause, world, xi, yi, zi, xx, yy, zz)
  }

  def setBlock(block: Block, metadata: Int, notify: Int): Boolean = super.setBlock(world, block, metadata, notify)

  def setBlock(block: Block, metadata: Int): Boolean = super.setBlock(world, block, metadata)

  def setBlock(block: Block): Boolean = super.setBlock(world, block)

  def setBlockToAir(): Boolean = super.setBlockToAir(world)

  def isAirBlock(): Boolean = super.isAirBlock(world)

  def isBlockEqual(block: Block) = super.isBlockEqual(world, block)

  def isBlockFreezable(): Boolean = super.isBlockFreezable(world)

  def rayTraceEntities(target: Vector3): MovingObjectPosition = super.rayTraceEntities(world, target)

  override def clone: VectorWorld = new VectorWorld(world, x, y, z)

  override def equals(o: Any): Boolean =
  {
    if (o.isInstanceOf[IVectorWorld])
    {
      return super.equals(o) && this.world == o.asInstanceOf[IVectorWorld].world
    }

    if (o.isInstanceOf[VectorWorld])
    {
      return super.equals(o) && this.world == o.asInstanceOf[VectorWorld].world
    }

    return false
  }

  override def toString: String =
  {
    return "VectorWorld [" + this.x + "," + this.y + "," + this.z + "," + this.world + "]"
  }
}