package com.resonant.core.api.gui;

/**
 * Add this to a container class if using WatchedSlot to trigger the container on slot change
 */
public interface ISlotWatcher {
	/**
	 * Will trigger if the watched slot has changed
	 */
	public void slotContentsChanged(int slot);
}
