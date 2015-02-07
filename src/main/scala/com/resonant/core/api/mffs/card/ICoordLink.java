package com.resonant.core.api.mffs.card;

import net.minecraft.item.ItemStack;
import resonantengine.lib.transform.vector.VectorWorld;

public interface ICoordLink {
	public void setLink(ItemStack itemStack, VectorWorld position);

	public VectorWorld getLink(ItemStack itemStack);
}
