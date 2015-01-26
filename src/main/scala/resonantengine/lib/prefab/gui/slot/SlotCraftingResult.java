package resonantengine.lib.prefab.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import resonantengine.api.gui.ISlotPickResult;
import resonantengine.api.gui.ISlotWatcher;

/**
 * Easy class to create a slot that is used for an event trigger or crafting based event
 */
public class SlotCraftingResult extends SlotWatched
{
	private ISlotPickResult tile;

	public SlotCraftingResult(ISlotPickResult tile, ISlotWatcher container, IInventory inventory, int par2, int par3, int par4)
	{
		super(inventory, par2, par3, par4, container);
		this.tile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack itemStack)
	{
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return true;
	}

	/**
	 * When the slot has changed it calls @ISlotPickResult 's method
	 */
	@Override
	public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack)
	{
		this.tile.onPickUpFromSlot(entityPlayer, this.slotNumber, itemStack);
		super.onPickupFromSlot(entityPlayer, itemStack);
	}
}
