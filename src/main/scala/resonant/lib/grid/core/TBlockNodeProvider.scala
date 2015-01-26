package resonant.lib.grid.core

import java.util.{List => JList}

import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import resonant.api.ISave
import resonant.lib.prefab.tile.spatial.SpatialTile
import resonant.lib.transform.vector.Vector3

import scala.collection.convert.wrapAll._

/**
 * A node trait that can be mixed into any Tile. Mixing this trait will cause nodes to reconstruct/deconstruct as needed.
 * @author Calclavia
 */
trait TBlockNodeProvider extends SpatialTile with TNodeProvider
{
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
}
