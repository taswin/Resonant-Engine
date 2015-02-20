package com.resonant.core.resources.item

import java.util.Optional

import com.resonant.core.resources.{Resource, ResourceFactory}
import nova.core.item.Item
import nova.core.render.texture.ItemTexture

/**
 * @author Calclavia
 */
class ItemIngot extends Item with Resource {
	override def getTexture: Optional[ItemTexture] = Optional.of(ResourceFactory.ingot)
}
