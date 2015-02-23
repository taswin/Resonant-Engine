package com.resonant.core.prefab.block

import java.util.Optional

import nova.core.inventory.components.InventoryProvider
import nova.core.inventory.{Inventory, InventorySimple}
import nova.core.retention.Stored
import nova.core.util.Direction

/**
 * A trait applied to inventory objects.
 */
trait InventorySimpleProvider extends InventoryProvider {

	@Stored
	protected val inventory: InventorySimple

	override def getInventory(side: Direction): Optional[Inventory] = Optional.of(inventory)
}
