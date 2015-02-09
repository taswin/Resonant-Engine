package com.resonant.core.graph.internal;

import java.util.Set;

/**
 * A node is a object with defined connections in a graph structure.
 */
public interface Node<N extends Node<?>> {

	/**
	 * Gets a list of nodes connected to this node.
	 */
	public Set<N> connections();
}
