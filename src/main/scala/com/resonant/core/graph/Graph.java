package com.resonant.core.graph;

import nova.core.item.ItemStack;

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

	public void add(N node);

	public void remove(N node);

	public N get(int i);

	public int size();

	default Iterator<N> iterator() {
		return new GraphIterator(this);
	}

	default Spliterator<N> spliterator() {
		return Spliterators.spliterator(iterator(), size(), Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.SORTED);
	}

	/**
	 * Represents this inventory as {@link ItemStack} {@link java.util.stream.Stream}
	 *
	 * @return This inventory as {@link ItemStack} {@link java.util.stream.Stream}
	 */
	default Stream<N> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * Builds the grid, refreshing all its internal.
	 */
	void build();

	public class GraphIterator<N> implements Iterator<N> {
		private final Graph graph;
		private int i;
		private N next = null;

		public GraphIterator(Graph graph) {
			this.graph = graph;
			findNext();
		}

		private void findNext() {
			while (i < graph.size()) {
				next = (N) graph.get(i++);
			}
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public N next() {
			N current = next;
			findNext();
			return current;
		}
	}
}
