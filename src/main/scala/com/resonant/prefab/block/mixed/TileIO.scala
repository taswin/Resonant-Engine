package com.resonant.prefab.block.mixed

import com.resonant.prefab.block.impl.TIO
import net.minecraft.block.material.Material

abstract class TileIO(material: Material) extends ResonantTile(material: Material) with TIO {
}
