package com.resonant.prefab.block.multiblock;

import net.minecraft.world.World;
import nova.core.util.transform.Vector3d;

public interface IMultiBlockStructure<W extends IMultiBlockStructure> extends IMultiBlock {
	public World getWorld();

	public void onMultiBlockChanged();

	public Vector3d getPosition();

	public MultiBlockHandler<W> getMultiBlock();
}
