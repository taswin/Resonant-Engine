package com.resonant.api.graph.api;

import java.util.Set;

/**
 * A graph are structures used to model pairwise relations between objects
 * @author Calclavia
 */
public interface IGraph<N> {
	/** Gets all objects that act as nodes in this grid */
	public Set<N> getNodes();

	/** Adds a node to the grid */
	public void add(N node);

	/** Removes a node from the grid */
	public void remove(N node);

	/** Asks the grid to rebuild */
	public void reconstruct();

	/** Asks teh grid to destroy */
	public void deconstruct();
}
