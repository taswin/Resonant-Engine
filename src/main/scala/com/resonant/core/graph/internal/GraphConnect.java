package com.resonant.core.graph.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A graph that contains nodes, each with its ability to connect to other nodes.
 * @author Calclavia
 */
public abstract class GraphConnect<N extends Node> implements Graph<N> {

	//A list of nodes in the graph
	protected final List<N> nodes = new ArrayList<>();

	protected boolean[][] adjMat = null;

	@Override
	public void add(N node) {
		if (!nodes.contains(node)) {
			nodes.add(node);
		}
	}

	@Override
	public void remove(N node) {
		nodes.remove(node);
	}

	public int id(Object node) {
		assert nodes.contains(node);
		//TODO: Indexof is O(n). A map may be O(1) instead.
		return nodes.indexOf(node);
	}

	@Override
	public Collection<N> nodes() {
		return nodes;
	}

	public boolean isConnected(N from, N to) {
		return adjMat[id(from)][id(to)];
	}

	public boolean isUndirectlyConnected(N from, N to) {
		return isConnected(from, to) || isConnected(to, from);
	}

	public void connect(N a, N b) {
		adjMat[id(a)][id(b)] = true;
	}

	@Override
	public void build() {
		/**
		 * Builds the adjacency matrix
		 */
		adjMat = new boolean[nodes.size()][nodes.size()];

		for (N node : nodes) {
			for (Object con : node.connections()) {
				if (nodes.contains(con)) {
					connect(node, (N) con);
				}
			}
		}
	}
}
