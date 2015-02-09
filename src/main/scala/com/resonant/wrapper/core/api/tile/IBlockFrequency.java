package com.resonant.wrapper.core.api.tile;

/**
 * Applied to all blocks that has a frequency.
 * @author Calclavia
 */

public interface IBlockFrequency {
	/**
	 * @return The frequency of this object.
	 */
	public int getFrequency();

	/**
	 * Sets the frequency
	 * @param frequency - The frequency of this object.
	 */
	public void setFrequency(int frequency);
}
