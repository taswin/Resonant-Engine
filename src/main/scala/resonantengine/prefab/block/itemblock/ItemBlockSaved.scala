package resonantengine.prefab.block.itemblock

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import resonantengine.lib.transform.vector.Vector3
import resonantengine.lib.utility.inventory.InventoryUtility
import resonantengine.lib.utility.nbt.NBTUtility

/**
 * An ItemBlock that can store its block's internal data.
 *
 * @author Calclavia
 */
object ItemBlockSaved
{

  def dropBlockWithNBT(block: Block, world: World, x: Int, y: Int, z: Int)
  {
    if (!world.isRemote && world.getGameRules.getGameRuleBooleanValue("doTileDrops"))
    {
      val itemStack: ItemStack = getItemStackWithNBT(block, world, x, y, z)
      if (itemStack != null)
      {
        InventoryUtility.dropItemStack(world, new Vector3(x, y, z), itemStack)
      }
    }
  }

  def getItemStackWithNBT(b: Block, world: World, x: Int, y: Int, z: Int): ItemStack =
  {
    val block: Block = (if (b == null) world.getBlock(x, y, z) else b)
    if (block != null)
    {
      val meta: Int = world.getBlockMetadata(x, y, z)
      val dropStack: ItemStack = new ItemStack(block, block.quantityDropped(meta, 0, world.rand), block.damageDropped(meta))
      val tag: NBTTagCompound = new NBTTagCompound
      val tile: TileEntity = world.getTileEntity(x, y, z)
      if (tile != null)
      {
        tile.writeToNBT(tag)
      }
      tag.removeTag("id")
      tag.removeTag("x")
      tag.removeTag("y")
      tag.removeTag("z")
      dropStack.setTagCompound(tag)
      return dropStack
    }
    return null
  }
}

class ItemBlockSaved(block: Block) extends ItemBlockTooltip(block)
{
  setMaxDamage(0)
  setHasSubtypes(true)
  setMaxStackSize(1)

  override def placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): Boolean =
  {
    val flag: Boolean = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)
    val tile: TileEntity = world.getTileEntity(x, y, z)
    if (tile != null)
    {
      val essentialNBT: NBTTagCompound = new NBTTagCompound
      tile.writeToNBT(essentialNBT)
      val setNbt: NBTTagCompound = NBTUtility.getNBTTagCompound(stack)
      if (essentialNBT.hasKey("id"))
      {
        setNbt.setString("id", essentialNBT.getString("id"))
        setNbt.setInteger("x", essentialNBT.getInteger("x"))
        setNbt.setInteger("y", essentialNBT.getInteger("y"))
        setNbt.setInteger("z", essentialNBT.getInteger("z"))
      }
      tile.readFromNBT(setNbt)
    }
    return flag
  }
}