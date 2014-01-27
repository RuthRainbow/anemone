package group7.anemone.MNetwork;

import java.io.Serializable;

/**
 * The MSimulationConfig class encapsulates configuration
 * parameters for the simulation.
 */
public class MSimulationConfig implements Serializable{
	private static final long serialVersionUID = 2401937745798596340L;
	
	/* How far into the future shall we allow events? */
	public int eventHorizon;
	
	/**
	 * Copy constructor.
	 * 
	 * @param config	the simulation configuration to be copied
	 */
	public MSimulationConfig(MSimulationConfig config) {
		this.eventHorizon = config.eventHorizon;
	}
	
	/**
	 * Constructs a simulation configuration.
	 * 
	 * @param eventHorizon	the horizon for possible future events
	 */
	public MSimulationConfig(int eventHorizon) {
		this.eventHorizon = eventHorizon;
	}
}