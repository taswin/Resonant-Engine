package com.resonant.core.prefab.block

/**
 * An object that can handle ticks.
 * @author Calclavia
 */
trait Updater extends nova.core.util.components.Updater {

	protected var ticks = 0L

	def getTicks = ticks

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
	 * Called on the first tick.
	 */
	def start() {
	}
}
