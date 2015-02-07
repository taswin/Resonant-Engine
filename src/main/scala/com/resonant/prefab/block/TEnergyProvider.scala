package com.resonant.prefab.block

import com.resonant.graph.energy.EnergyStorage
import nova.core.util.Direction

import scala.beans.BeanProperty

/**
 * Any object that can store energy
 * @author Calclavia
 */
trait TEnergyProvider {
	@BeanProperty
	var energy = new EnergyStorage

	def getEnergyStorage(from: Direction): EnergyStorage = {
		return energy
	}
}
