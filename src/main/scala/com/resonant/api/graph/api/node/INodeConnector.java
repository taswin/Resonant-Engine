package com.resonant.api.graph.api.node;

import nova.core.util.Direction;

import java.util.Map;
import java.util.Set;

/**
 * Applied to anything that can connect to another object either part of a grid or tile system.
 * @author Darkguardsman
 * @deprecated use IConnector
 */
@Deprecated
public interface INodeConnector<A> extends INode {
	/**
	 * Can the object connect to this node. Only called on connection update logic, and shouldn't be called per tick.
	 * @param other - The other has to be a connectable object.
	 * @param from - Side of the node this connection is connecting from
	 * @return True if and only if the connection is allowed
	 */
	public <B extends A> boolean canConnect(B other, Direction from);

	/**
	 * @return empty list
	 * @deprecated - return an empty list and use directionMap
	 */
	@Deprecated
	public Set<A> connections();

	public Map<A, Direction> directionMap();
}
