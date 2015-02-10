package com.resonant.core.graph.internal;

import nova.core.util.Direction;

import java.util.function.BiFunction;

/**
 * @author Calclavia
 */
public abstract class NodeConnect<N extends Node<?>> implements Node<N> {

	//The bitmask containing sides this node may connect to
	public int connectionMask = 0x3F;

	//Event called when a connection occurs
	public Runnable connectListener = () -> {
	};

	//Called to check if this node can connect to another node
	public BiFunction<N, Direction, Boolean> canConnect = (node, dir) -> (connectionMask & (1 << dir.ordinal())) != 0;

	protected int connectedMask = 0x0;

	public boolean canConnect(N node, Direction dir) {
		return canConnect.apply(node, dir);
	}

	public int connectedMask() {
		return connectedMask;
	}
}
