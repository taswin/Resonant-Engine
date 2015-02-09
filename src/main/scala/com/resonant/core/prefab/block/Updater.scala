package com.resonant.core.prefab.block

/**
 * An object that can handle ticks.
 * @author Calclavia
 */
trait Updater extends nova.core.util.components.Updater {
	protected var ticks = 0L

	override def update(deltaTime: Double) {
		if (ticks == 0) {
			start()
		}

		if (ticks >= Long.MaxValue) {
			ticks = 1
		}

		ticks += 1
	}

	/**
	 * Called on the TileEntity's first tick.
	 */
	def start() {
	}
}
