package com.resonant.wrapper.lib.factory.resources.item

import com.resonant.wrapper.core.Reference
import com.resonant.wrapper.lib.factory.resources.Resource
import net.minecraft.creativetab.CreativeTabs
import nova.core.item.Item

/**
 * @author Calclavia
 */
class ItemIngot extends Item with Resource {
	setTextureName(Reference.prefix + "ingot")
	setCreativeTab(CreativeTabs.tabMaterials)
}
