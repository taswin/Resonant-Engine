package com.resonant.wrapper.core.api.tile;

import nova.core.util.Direction;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IIO {
	public Set<Direction> getInputDirections();

	public Set<Direction> getOutputDirections();

	public void setIO(Direction dir, int type);

	public int getIO(Direction dir);
}
