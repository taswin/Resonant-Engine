package com.resonant.core.graph.internal.path;

import nova.core.util.transform.Vector3d;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A class that allows flexible pathfinding for different positions. Compared to AStar pathfinding,
 * this version is faster but does not calculated the most optimal path.
 * @author Calclavia
 */
public class Pathfinder {
	/**
	 * A pathfinding call back interface used to call back on paths.
	 */
	public IPathCallBack callBackCheck;

	/**
	 * A list of permissions that the pathfinder already went through.
	 */
	public Set<Vector3d> closedSet;

	/**
	 * The resulted path found by the pathfinder. Could be null if no path was found.
	 */
	public List<Vector3d> results;

	private Vector3d start;

	public Pathfinder(IPathCallBack callBack) {
		this.callBackCheck = callBack;
		this.reset();
	}

	/**
	 * @return True on success finding, false on failure.
	 */
	public boolean findNodes(Vector3d currentNode) {
		if (this.start == null) {
			this.start = currentNode;
		}

		this.closedSet.add(currentNode);

		if (this.callBackCheck.onSearch(this, this.start, currentNode)) {
			return false;
		}

		for (Vector3d node : this.callBackCheck.getConnectedNodes(this, currentNode)) {
			if (!this.closedSet.contains(node)) {
				if (this.findNodes(node)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Called to execute the pathfinding operation.
	 */
	public Pathfinder init(Vector3d startNode) {
		this.findNodes(startNode);
		return this;
	}

	public Pathfinder reset() {
		this.closedSet = new LinkedHashSet<Vector3d>();
		this.results = new LinkedList<Vector3d>();
		return this;
	}
}
