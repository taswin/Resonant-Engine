package com.resonant.core.graph

super.reconstruct ()
		grid.reconstruct(this.asInstanceOf[A])
	}

	override def deconstruct() {
		/**
		 * Attempt to remove connections to this node in other nodes
		 */
		connections.foreach(_.disconnect(this.asInstanceOf[A]))
		super.deconstruct()
		grid.deconstruct(this.asInstanceOf[A])
	}

	def setGrid(grid: GridNode[_]) {
		this._grid = grid.asInstanceOf[GridNode[A]]
	}

	def grid: GridNode[A] = {
		if (_grid == null)
			_grid = newGrid

		_grid
	}

	protected def newGrid: GridNode[A]
