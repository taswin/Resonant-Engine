package com.resonant.core.graph.internal.path;

import nova.core.util.transform.Vector3d;

import java.util.Set;

public interface IPathCallBack {
	/**
	 * @param finder - The Pathfinder object.
	 * @param currentNode - The node being iterated through.
	 * @return A set of permissions connected to the currentNode. Essentially one should return a set of
	 * neighboring permissions.
	 */
	public Set<Vector3d> getConnectedNodes(Pathfinder finder, Vector3d currentNode);

	/**
	 * Called when looping through permissions.
	 * @param finder - The Pathfinder.
	 * @param start - The starting node.
	 * @param currentNode - The node being searched.
	 * @return True to stop the path finding operation.
	 */
	public boolean onSearch(Pathfinder finder, Vector3d start, Vector3d currentNode);
}