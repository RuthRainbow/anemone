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
		MNeuronParams nparams = new MNeuronParams();

		/* Set the neurons to RS (regular spiking) neurons. */
		nparams.a = 0.1;
		nparams.b = 0.2;
		nparams.c = -65.0;
		nparams.d = 8.0;

		/* Set default neuron coordinates. */
		nparams.spatialCoords = new MVec3f(0, 0, 0);

		return nparams;
	}

	/**
	 * Creates an initial neuron state suitable for a regular spiking
	 * neuron.
	 * 
	 * @return	an instanciated neuron state object
	 */
	static public MNeuronState createInitialRSNeuronState() {
		MNeuronState nstate = new MNeuronState();

		/* Set the neurons to start in a resting state. */
		nstate.v = -65.0;
		nstate.u = 0.0;
		nstate.I = 0.0;

		return nstate;
	}
}