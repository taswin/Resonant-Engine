package com.resonant.core.graph.api;

import com.resonant.core.graph.internal.Node;
import nova.core.util.Direction;

/**
 * @author Calclavia
 */
public interface NodeProvider {
	/**
	 * @param nodeType - The type of node we are looking for.
	 * @param from - The direction.
	 * @return Returns the node object.
	 */
	public <N extends Node> N getNode(Class<? extends N> nodeType, Direction from);
}
