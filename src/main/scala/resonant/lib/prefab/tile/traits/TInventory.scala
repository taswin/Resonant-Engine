package resonant.lib.content.prefab

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection
import resonant.api.tile.IInventoryProvider
import resonant.api.tile.node.IExternalInventory
import resonant.lib.prefab.tile.spatial.SpatialBlock
import resonant.lib.transform.vector.Vector3
import resonant.lib.utility.inventory.{ExternalInventory, InventoryUtility}

/**
 * A trait applied to inventory objects.
 */
trait TInventory extends SpatialBlock with IInventoryProvider with ISidedInventory
{
  protected lazy val inventory = new ExternalInventory(this, getSizeInventory())

  override def decrStackSize(index: Int, amount: Int): ItemStack = this.getInventory().decrStackSize(index, amount)

  def incrStackSize(slot: Int, stack: ItemStack)
  {
    if (this.getStackInSlot(slot) == null)
    {
      setInventorySlotContents(slot, stack.copy())
    }
    else if (this.getStackInSlot(slot).isItemEqual(stack))
    {
      getStackInSlot(slot).stackSize += stack.stackSize
      onInventoryChanged()
    }

    markDirty()
  }

  override def getStackInSlot(index: Int): ItemStack = this.getInventory().getStackInSlot(index)

  override def getInventory(): IExternalInventory = inventory

  override def setInventorySlotContents(index: Int, stack: ItemStack)
  {
    this.getInventory().setInventorySlotContents(index, stack)
    onInventoryChanged()
  }

  /** Called each time the inventory changes */
  def onInventoryChanged()
  {

  }

  override def getStackInSlotOnClosing(index: Int): ItemStack = this.getInventory().getStackInSlotOnClosing(index)

  override def getInventoryName: String = getBlockType.getLocalizedName

  override def hasCustomInventoryName: Boolean = inventory.hasCustomInventoryName()

  override def getInventoryStackLimit = getInventory.getInventoryStackLimit

  override def isUseableByPlayer(entityplayer: EntityPlayer) = getInventory.isUseableByPlayer(entityplayer)

  override def openInventory = getInventory.openInventory()

  override def closeInventory = getInventory.closeInventory()

  def isItemValidForSlot(i: Int, itemstack: ItemStack): Boolean = this.getInventory.isItemValidForSlot(i, itemstack)

  def getAccessibleSlotsFromSide(var1: Int): Array[Int] = this.getInventory.getAccessibleSlotsFromSide(var1)

  def canInsertItem(i: Int, itemstack: ItemStack, j: Int): Boolean = this.getInventory.canInsertItem(i, itemstack, j)

  def canExtractItem(i: Int, itemstack: ItemStack, j: Int): Boolean = this.getInventory.canExtractItem(i, itemstack, j)

  def canStore(stack: ItemStack, slot: Int, side: ForgeDirection): Boolean = false

  def canRemove(stack: ItemStack, slot: Int, side: ForgeDirection): Boolean = true

  /**
   * Player-Inventory interaction methods.
   */
  def interactCurrentItem(slotID: Int, player: EntityPlayer): Boolean = interactCurrentItem(this, slotID, player)

  def interactCurrentItem(inventory: IInventory, slotID: Int, player: EntityPlayer): Boolean =
  {
    val stackInInventory: ItemStack = inventory.getStackInSlot(slotID)
    val current: ItemStack = player.inventory.getCurrentItem
    if (current != null)
    {
      if (stackInInventory == null || ItemStack.areItemStacksEqual(stackInInventory, current))
      {
        return insertCurrentItem(inventory, slotID, player)
      }
    }
    return extractItem(inventory, slotID, player)
  }

  def insertCurrentItem(inventory: IInventory, slotID: Int, player: EntityPlayer): Boolean =
  {
    val stackInInventory: ItemStack = inventory.getStackInSlot(slotID)
    var current: ItemStack = player.inventory.getCurrentItem
    if (current != null)
    {
      if (stackInInventory == null || ItemStack.areItemStacksEqual(stackInInventory, current))
      {
        if (inventory.isItemValidForSlot(slotID, current))
        {
          if (isControlDown(player))
          {
            if (stackInInventory == null)
            {
              inventory.setInventorySlotContents(slotID, current.splitStack(1))
            }
            else
            {
              stackInInventory.stackSize += 1
              current.stackSize -= 1
            }
          }
          else
          {
            if (stackInInventory == null)
            {
              inventory.setInventorySlotContents(slotID, current)
            }
            else
            {
              stackInInventory.stackSize += current.stackSize
              current.stackSize = 0
            }
            current = null
          }
          if (current == null || current.stackSize <= 0)
          {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null)
          }
          onInventoryChanged()
          return true
        }
      }
    }
    return false
  }

  def extractItem(inventory: IInventory, slotID: Int, player: EntityPlayer): Boolean =
  {
    var stackInInventory: ItemStack = inventory.getStackInSlot(slotID)
    if (stackInInventory != null)
    {
      if (isControlDown(player))
      {
        InventoryUtility.dropItemStack(player.worldObj, new Vector3(player), stackInInventory.splitStack(1), 0)
      }
      else
      {
        InventoryUtility.dropItemStack(player.worldObj, new Vector3(player), stackInInventory, 0)
        stackInInventory = null
      }
      if (stackInInventory == null || stackInInventory.stackSize <= 0)
      {
        inventory.setInventorySlotContents(slotID, null)
      }
      onInventoryChanged()
      return true
    }
    return false
  }

  override def onRemove(block: Block, metadata: Int)
  {
    super.onRemove(block, metadata)
    dropEntireInventory(block, metadata)
  }

  def dropEntireInventory(block: Block, par6: Int)
  {
    if (!world.isRemote)
    {
      (0 until getSizeInventory)
        .filter(getStackInSlot(_) != null)
        .foreach(
          i =>
          {
            InventoryUtility.dropItemStack(world, center, getStackInSlot(i))
            setInventorySlotContents(i, null)
          }
        )
    }
    onInventoryChanged()
    markDirty()
  }

  override def readFromNBT(nbt: NBTTagCompound)
  {
    super.readFromNBT(nbt)
    getInventory.load(nbt)
  }

  override def writeToNBT(nbt: NBTTagCompound)
  {
    super.writeToNBT(nbt)
    getInventory.save(nbt)
  }
}
