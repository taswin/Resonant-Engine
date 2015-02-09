package com.resonant.core.graph.thermal;

import cpw.mods.fml.common.eventhandler.Event;
import resonantengine.lib.transform.vector.VectorWorld;

public abstract class ThermalEvent extends Event {
	public final VectorWorld position;
	public final int temperature;

	public ThermalEvent(VectorWorld position, int temperature) {
		this.position = position;
		this.temperature = temperature;
	}

	public static class EventThermalUpdate extends ThermalEvent {
		public EventThermalUpdate(VectorWorld position, int temperature) {
			super(position, temperature);
		}
	}
}
