package resonant.api.mffs.card;

import net.minecraft.item.ItemStack;
import resonant.lib.access.AbstractAccess;

/**
 * Applied to Item ID and group cards.
 *
 * @author Calclavia
 */
public interface IAccessCard extends ICard
{
	public AbstractAccess getAccess(ItemStack stack);

	public void setAccess(ItemStack stack, AbstractAccess access);
}
