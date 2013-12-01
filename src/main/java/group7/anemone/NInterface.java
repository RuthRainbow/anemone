package group7.anemone;

import java.io.Serializable;

/**
 * NInterface (neural interface) is a data structure class that represents
 * the current state of an agent's afferent and efferent organs. Afferent
 * organs are updated by the simulation and read by the neural network.
 * Efferent organs are updated by the neural network and read by the simulation.
 */
class NInterface implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6748348291714735362L;
	public NIAffectors affectors;
	public NIEffectors effectors;

	/**
	 * The constructor takes an integer that describes the dimension of the
	 * agent's visual sensor array.
	 *
	 * @param	visionDim	dimension of the agent's visual sensor array
	 */
	NInterface(int visionDim) {
		affectors = new NIAffectors(visionDim);
		effectors = new NIEffectors();
	}

	public String toString() {
		return affectors.toString()+", "+effectors.toString();
	}
}