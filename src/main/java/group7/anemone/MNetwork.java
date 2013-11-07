package group7.anemone;

import java.util.ArrayList;

/**
 * The MNetwork class encapsulates a set of neurons and a set of synapses
 * that together describe a network.
 */
public class MNetwork {
	private ArrayList<MNeuron> neurons;
	private ArrayList<MSynapse> synapses;

	MNetwork(ArrayList<MNeuron> neurons, ArrayList<MSynapse> synapses) {
		this.neurons = neurons;
		this.synapses = synapses;
	}

	ArrayList<MNeuron> getNeurons() {
		return neurons;
	}

	ArrayList<MSynapse> getSynapses() {
		return synapses;
	}
}