package group7.anemone.MNetwork;

/**
 * A factory class that produces common Izhikevich neuron types and related
 * objects
 */
public class MFactory {
	/**
	 * Creates a regular spiking neuron as per (Izhikevich, 2003).
	 * 
	 * @param nid	the neuron id to use when creating the neuron
	 * @return	an instanciated neuron object
	 */
	static public MNeuron createRSNeuron(int nid) {
		MNeuronState nstate = createInitialRSNeuronState();
		MNeuronParams nparams = createRSNeuronParams();
		MNeuron neuron = new MNeuron(nparams, nstate, nid);

		return neuron;
	}

	/**
	 * Creates neuron parameters that describe a regular spiking neuron as
	 * per (Izhikevich, 2003).
	 * 
	 * @return	an instanciated neuron parameter object
	 */
	static public MNeuronParams createRSNeuronParams() {
		MNeuronParams nparams;
		
		/* Set default neuron coordinates. */
		MVec3f spatialCoords = new MVec3f(0, 0, 0);

		/* Set the neurons to RS (regular spiking) neurons. */
		nparams = new MNeuronParams(
			0.1,
			0.2,
			-65.0,
			8.0,
			0.0,
			0.0,
			0.01,
			true,
			spatialCoords);

		return nparams;
	}

	/**
	 * Creates an initial neuron state suitable for a regular spiking
	 * neuron.
	 * 
	 * @return	an instanciated neuron state object
	 */
	static public MNeuronState createInitialRSNeuronState() {
		MNeuronState nstate = new MNeuronState(-65.0, 0.0, 0.0, 0.0);

		return nstate;
	}
}