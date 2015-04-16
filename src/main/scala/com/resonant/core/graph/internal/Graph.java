package com.resonant.core.graph.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A graph is a collection of nodes.
 *
 * @author Calclavia
 */
public interface Graph<N> extends Iterable<N> {

	void add(N node);

	void remove(N node);

	default int size() {
		return nodes().size();
	}

	Collection<N> nodes();

	default Iterator<N> iterator() {
		return nodes().iterator();
	}

	default Spliterator<N> spliterator() {
		return Spliterators.spliterator(iterator(), size(), Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.SORTED);
	}

	default Stream<N> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * Builds the grid, refreshing all its internal cache.
	 */
	void build();
}
