package resonantengine.api.mffs.card;

import net.minecraft.item.ItemStack;
import resonantengine.lib.access.AbstractAccess;

/**
 * Applied to Item id and group cards.
 *
 * @author Calclavia
 */
public interface IAccessCard extends ICard
{
	public AbstractAccess getAccess(ItemStack stack);

	public void setAccess(ItemStack stack, AbstractAccess access);
}
