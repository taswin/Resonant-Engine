package com.resonant.core.graph.internal.frequency

import java.util

import com.resonant.core.graph.internal.Graph
import com.resonant.wrapper.core.api.tile.BlockFrequency

import scala.collection.convert.wrapAll._

object GridFrequency {
	var client = new GridFrequency
	var server = new GridFrequency
}

class GridFrequency extends Graph[BlockFrequency] {

	private var _nodes = Set.empty[BlockFrequency]
	private var frequencyMap = Map.empty[Int, Set[BlockFrequency]].withDefaultValue(Set.empty)

	override def add(node: BlockFrequency) {
		_nodes += node
		build()
	}

	override def remove(node: BlockFrequency) {
		_nodes -= node
		build()
	}

	override def markBuild() {
		build()
	}

	override def build() {
		frequencyMap = Map.empty

		nodes.map(n => (n.getFrequency, n)).foreach(kv =>
			frequencyMap += (kv._1 -> (frequencyMap(kv._1) + kv._2))
		)
	}

	override def nodes(): util.Set[BlockFrequency] = _nodes

	def get(frequency: Int) = frequencyMap(frequency)
}