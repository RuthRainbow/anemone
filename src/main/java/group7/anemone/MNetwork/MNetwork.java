package group7.anemone.MNetwork;

import java.util.ArrayList;

/**
 * The MNetwork class encapsulates a set of neurons and a set of synapses
 * that together describe a network.
 */
public class MNetwork {
	private ArrayList<MNeuron> neurons;
	private ArrayList<MSynapse> synapses;

	public MNetwork(ArrayList<MNeuron> neurons, ArrayList<MSynapse> synapses) {
		this.neurons = neurons;
		this.synapses = synapses;
	}

	public ArrayList<MNeuron> getNeurons() {
		return neurons;
	}

	public ArrayList<MSynapse> getSynapses() {
		return synapses;
	}
}