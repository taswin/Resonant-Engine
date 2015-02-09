package com.resonant.api.graph.api.node;

import com.resonant.core.api.misc.ISave;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

/**
 * @author DarkGuardsman
 */
public interface IExternalInventory extends ISidedInventory, ISave {
	/**
	 * Gets the inventory array. Direction.UNKOWN must return all sides
	 */
	public ItemStack[] getContainedItems();

	/**
	 * Deletes all the items in the inventory
	 */
	public void clear();
}
