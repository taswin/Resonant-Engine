package com.resonant.wrapper.core.api.tile;

import com.resonant.core.graph.api.IGraph;

/**
 * Any node that is part of a grid system such as a power network
 */
public interface IGridProvider {
	/** Gets the grid reference */
	public IGraph getGrid();

	/** Sets the grid reference */
	public void setGrid(IGraph grid);
}
