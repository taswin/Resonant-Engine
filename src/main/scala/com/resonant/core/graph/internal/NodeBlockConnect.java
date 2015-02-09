package com.resonant.core.graph.internal;

import com.resonant.core.graph.api.NodeProvider;
import nova.core.block.Block;
import nova.core.block.BlockAccess;
import nova.core.util.Direction;
import nova.core.util.exception.NovaException;
import nova.core.util.transform.Vector3i;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A node that connects to adjacent blocks.
 *
 * @author Calclavia
 */
public class NodeBlockConnect<N extends NodeConnect<N>> extends NodeConnect<N> {

	public final NodeProvider parent;
	//The cached connection map
	protected Map<N, Direction> connectedMap;

	public NodeBlockConnect(NodeProvider parent) {
		this.parent = parent;
	}

	@Override
	public Set<N> connections() {

		Map<Direction, Optional<Block>> adjacentBlocks = Arrays.stream(Direction.DIRECTIONS)
			.collect(Collectors.toMap(Function.identity(), dir -> blockAccess().getBlock(position().add(dir.toVector()))));

		Map<Direction, N> adjacentNodes = adjacentBlocks.entrySet().stream()
			.filter(entry -> entry.getValue().isPresent())
			.filter(entry -> entry.getValue().get().getClass().isAssignableFrom(compareClass()))
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> getNodeFrom(entry.getValue().get(), entry.getKey())));

		//Generates a map of connections and their directions
		connectedMap = adjacentNodes.entrySet().stream()
			.filter(entry -> canConnect(entry.getValue(), entry.getKey()))
			.filter(entry -> entry.getValue().canConnect((N) this, entry.getKey().opposite()))
			.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		//Sets the connection mask based on the connections
		connectedMask = connectedMap.values().stream().map(Enum::ordinal).map(i -> 1 << i).reduce((a, b) -> a | b).orElseGet(() -> 0);

		return connectedMap.keySet();
	}

	protected N getNodeFrom(Block block, Direction from) {
		if (block instanceof NodeProvider) {
			return ((NodeProvider) block).getNode(compareClass(), from);
		}

		return null;
	}

	public BlockAccess blockAccess() {
		if (parent instanceof Block) {
			return ((Block) parent).blockAccess();
		}
		throw new NovaException("NodeProvider type not supported.");
	}

	public Vector3i position() {
		if (parent instanceof Block) {
			return ((Block) parent).position();
		}
		throw new NovaException("NodeProvider type not supported.");
	}

	protected <N> Class<N> compareClass() {
		return (Class<N>) getClass();
	}
}
