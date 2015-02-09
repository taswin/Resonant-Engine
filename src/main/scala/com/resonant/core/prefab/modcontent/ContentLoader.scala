package com.resonant.core.prefab.modcontent

import nova.core.block.Block
import nova.core.game.Game
import nova.core.item.Item
import nova.core.loader.Loadable

/**
 * Automatic content registration for all Blocks, Items, Entities and Textures.
 *
 * Extend this trait from the main mod loading class and all fields will be registered. Elegantly.
 *
 * @author Calclavia
 */
trait ContentLoader extends Loadable {
	self =>

	override def preInit() = {
		//Automated handler for registering blocks & items vars
		for (field <- self.getClass.getDeclaredFields) {
			//Set it so we can access the field
			field.setAccessible(true)

			//Get contents for reference
			val obj = field.get(self)

			if (obj != null) {
				// Get type of object, then register it if supported
				obj match {
					case itemWrapper: ItemWrapper => field.set(self, Game.instance.get.itemManager.registerItem(obj.asInstanceOf[ItemWrapper].wrapped))
					case blockWrapper: BlockWrapper => field.set(self, Game.instance.get.blockManager.registerBlock(obj.asInstanceOf[BlockWrapper].wrapped))
				}
			}
		}
	}

	/**
	 * Creates a dummy instances temporarily until the preInit stage has passed
	 */
	implicit protected class BlockWrapper(val wrapped: Class[_ <: Block]) extends Block {
		override def getID: String = ""
	}

	implicit protected class ItemWrapper(val wrapped: Class[_ <: Item]) extends Item {
		override def getID: String = ""
	}

}
