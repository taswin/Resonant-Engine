package com.resonant.core.prefab.fluid

import net.minecraftforge.fluids.FluidTank

/**
 * Prefab interface to create a few common getTank methods for most tank handlers
 * Created by robert(Darkguardsman) on 9/25/2014.
 */
trait TTankProvider {
	/**
	 * Gets the tank for this object
	 */
	def getTank: FluidTank
}