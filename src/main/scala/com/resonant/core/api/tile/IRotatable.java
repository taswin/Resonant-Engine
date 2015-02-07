package com.resonant.core.api.tile;

import nova.core.util.Direction;

/**
 * Applied to any object that has a facing direction.
 * @author Calclavia
 */
public interface IRotatable {
	/**
	 * Gets the facing direction of the TileEntity
	 * @return Front of the tile
	 */
	public Direction getDirection();

	/**
	 * Sets the facing direction, is not supported by all machines
	 */
	public void setDirection(Direction direction);
}
