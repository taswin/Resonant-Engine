package com.resonant.prefab.gui.slot;

import com.resonant.core.api.gui.ISlotWatcher;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * A slot that triggers the container class if changed
 */
public class SlotWatched extends Slot {
	private ISlotWatcher slotWatcher;

	public SlotWatched(IInventory inventory, int id, int xPosition, int yPosition, ISlotWatcher slotWatcher) {
		super(inventory, id, xPosition, yPosition);
		this.slotWatcher = slotWatcher;
	}

	@Override
	public void onSlotChanged() {
		if (this.slotWatcher != null) {
			this.slotWatcher.slotContentsChanged(this.slotNumber);
		}
	}

}
