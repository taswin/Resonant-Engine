package com.resonant.core.prefab.block

import java.util

import nova.core.inventory.Inventory
import nova.core.inventory.components.SidedInventoryProvider
import nova.core.util.Direction

import scala.collection.convert.wrapAll._
/**
 * A trait applied to inventory objects.
 */
trait InventorySimpleProvider extends SidedInventoryProvider {

	protected val inventory: Inventory

	override def getInventory(side: Direction): util.Set[Inventory] = Set(inventory)
}
