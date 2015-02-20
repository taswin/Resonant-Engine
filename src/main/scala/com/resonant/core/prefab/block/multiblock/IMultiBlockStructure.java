package com.resonant.core.prefab.block.multiblock;

import nova.core.util.transform.Vector3i;
import nova.core.world.World;

public interface IMultiBlockStructure<W extends IMultiBlockStructure> extends IMultiBlock {
	public World world();

	public void onMultiBlockChanged();

	public Vector3i position();

	public MultiBlockHandler<W> getMultiBlock();
}
