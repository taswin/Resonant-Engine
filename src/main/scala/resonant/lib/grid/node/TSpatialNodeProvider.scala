package resonant.lib.grid.node

import java.util

import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection
import resonant.api.ISave
import resonant.api.grid.{INode, INodeProvider}
import resonant.content.spatial.block.SpatialTile
import resonant.lib.transform.vector.Vector3

import scala.collection.convert.wrapAll._

/**
 * A node trait that can be mixed into any SpatialTile. Mixing this trait will cause nodes to reconstruct/deconstruct when needed.
 * @author Calclavia
 */
trait TSpatialNodeProvider extends SpatialTile with INodeProvider
{
  protected val nodes = new util.HashSet[Node]

  override def start()
  {
    super.start()

    if (!world.isRemote)
      nodes.foreach(_.reconstruct())
  }

  override def onWorldJoin()
  {
    if (!world.isRemote)
      nodes.foreach(_.reconstruct())
  }

  override def onNeighborChanged(block: Block)
  {
    if (!world.isRemote)
      nodes.foreach(_.reconstruct())
  }

  override def onNeighborChanged(pos: Vector3)
  {
    if (!world.isRemote)
      nodes.foreach(_.reconstruct())
  }

  override def onWorldSeparate()
  {
    if (!world.isRemote)
      nodes.foreach(_.deconstruct())
  }

  override def invalidate()
  {
    if (!world.isRemote)
      nodes.foreach(_.deconstruct())
  }

  override def writeToNBT(nbt: NBTTagCompound)
  {
    super.writeToNBT(nbt)
    nodes.filter(_.isInstanceOf[ISave]).foreach(_.asInstanceOf[ISave].save(nbt))
  }

  override def readFromNBT(nbt: NBTTagCompound)
  {
    super.readFromNBT(nbt)
    nodes.filter(_.isInstanceOf[ISave]).foreach(_.asInstanceOf[ISave].load(nbt))
  }

  override def getNode[N <: INode](nodeType: Class[_ <: N], from: ForgeDirection): N =
  {
    return nodes.filter(node => nodeType.isAssignableFrom(node.getClass)).headOption.getOrElse(null).asInstanceOf[N]
  }
}
