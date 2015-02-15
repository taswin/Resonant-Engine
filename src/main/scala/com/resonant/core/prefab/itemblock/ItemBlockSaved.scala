package com.resonant.core.prefab.itemblock

import java.util
import java.util.Optional

import nova.core.block.Block
import nova.core.util.components.Storable
import nova.core.util.transform.{Vector3d, Vector3i}
import nova.core.world.World

/**
 * An ItemBlock that can store its block's internal data even after the block breaks.
 *
 * @author Calclavia
 */
object ItemBlockSaved {

	def dropBlockWithNBT(block: Block, world: World, x: Int, y: Int, z: Int) {
		if (!world.isRemote && world.getGameRules.getGameRuleBooleanValue("doTileDrops")) {
			val itemStack: ItemStack = getItemStackWithNBT(block, world, x, y, z)
			if (itemStack != null) {
				InventoryUtility.dropItemStack(world, new Vector3d(x, y, z), itemStack)
			}
		}
	}

	def getItemStackWithNBT(b: Block, world: World, x: Int, y: Int, z: Int): ItemStack = {
		val block: Block = (if (b == null) world.getBlock(x, y, z) else b)
		if (block != null) {
			val meta: Int = world.getBlockMetadata(x, y, z)
			val dropStack: ItemStack = new ItemStack(block, block.quantityDropped(meta, 0, world.rand), block.damageDropped(meta))
			val tag: NBTTagCompound = new NBTTagCompound
			val tile: TileEntity = world.getTileEntity(x, y, z)
			if (tile != null) {
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

class ItemBlockSaved(block: Block) extends ItemBlockTooltip(block) with Storable {

	var data: util.Map[String, AnyRef] = new util.HashMap

	override def getMaxStackSize: Int = 1

	override def save(data: util.Map[String, AnyRef]) {
		data.clear()
		data.putAll(this.data)
	}

	override def load(data: util.Map[String, AnyRef]): Unit = this.data = data

	override protected def onPostPlace(world: World, placePos: Vector3i): Boolean = {
		val placedBlock: Optional[Block] = world.getBlock(placePos)

		if (placedBlock.isPresent && placedBlock.get().isInstanceOf[Storable]) {
			//Check if basic NBT data such as x,y,z is retained.
			placedBlock.get().asInstanceOf[Storable].load(data)
		}

		return super.onPostPlace(world, placePos)
	}
}