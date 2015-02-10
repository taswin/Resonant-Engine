package com.resonant.core.graph.internal;

/**
 * A node that provides a reference to the graph that it is in.
 * @author Calclavia
 */
public interface GraphProvider<G extends Graph> {

	G getGraph();

	void setGraph(G graph);
}
