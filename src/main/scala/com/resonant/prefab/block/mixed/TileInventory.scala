package com.resonant.prefab.block.mixed

import com.resonant.prefab.block.impl.TInventory
import net.minecraft.block.material.Material

/**
 * Used for Java class inheritance of Scala traits
 * @author Calclavia
 */
abstract class TileInventory(material: Material) extends ResonantTile(material: Material) with TInventory {
}