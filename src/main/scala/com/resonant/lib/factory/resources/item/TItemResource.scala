package com.resonant.lib.factory.resources.item

import net.minecraft.item.{Item, ItemStack}

/**
 * A class used by rubble, dusts and refined dusts
 * @author Calclavia
 */
trait TItemResource extends Item {
	var material: String = ""

	override def getColorFromItemStack(p_82790_1_ : ItemStack, p_82790_2_ : Int): Int = ResourceFactory.getColor(material)
}
