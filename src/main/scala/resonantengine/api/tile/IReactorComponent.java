package resonantengine.api.tile;

import net.minecraft.item.ItemStack;

/**
 * All items that act as components in the reactor cell implements this method.
 */
public interface IReactorComponent
{
	public void onReact(ItemStack itemStack, IReactor reactor);
}
