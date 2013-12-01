package group7.anemone.MNetwork;

import java.io.Serializable;

/**
 * The MSimulationConfig class encapsulates configuration
 * parameters for the simulation.
 */
public class MSimulationConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2401937745798596340L;
	/* How far into the future shall we allow events? */
	public int eventHorizon;
}