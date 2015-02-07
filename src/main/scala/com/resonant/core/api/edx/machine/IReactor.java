package com.resonant.core.api.edx.machine;

import net.minecraft.world.World;

public interface IReactor {
	/**
	 * Transfers heat energy into the reactor
	 * @param energy - Heat in joules
	 */
	public void heat(double energy);

	public World world();
}
