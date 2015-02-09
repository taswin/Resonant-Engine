package com.resonant.wrapper.core.api.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

/**
 * Very simple item renderer used by Resonant Engine to pass Forge item rendering through a method. ResonantBlocks that inherit this will automatically be registered.
 */
public interface ISimpleItemRenderer {
	void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data);
}
