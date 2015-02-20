package com.resonant.core.prefab.block.multiblock;

import nova.core.util.transform.Vector3i;

/**
 * Interface to be applied to block entity blocks that occupies more than one block space. Useful for
 * large machines.
 *
 * @author Calclavia
 */
public interface IMultiBlock {
	/**
	 * @return An array of Vector3d containing the multiblock relative coordinates to be constructed.
	 */
	public Iterable<Vector3i> getMultiBlockVectors();
}
