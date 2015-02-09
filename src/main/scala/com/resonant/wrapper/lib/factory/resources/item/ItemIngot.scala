package com.resonant.wrapper.lib.factory.resources.item

import com.resonant.wrapper.core.Reference
import net.minecraft.creativetab.CreativeTabs

/**
 * @author Calclavia
 */
class ItemIngot extends TItemResource {
	setTextureName(Reference.prefix + "ingot")
	setCreativeTab(CreativeTabs.tabMaterials)
}
