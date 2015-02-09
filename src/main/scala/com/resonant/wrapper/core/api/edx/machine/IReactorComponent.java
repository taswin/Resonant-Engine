package com.resonant.wrapper.core.api.edx.machine;

import net.minecraft.item.ItemStack;

/**
 * All items that act as components in the reactor cell implements this method.
 */
public interface IReactorComponent {
	public void onReact(ItemStack itemStack, IReactor reactor);
}
