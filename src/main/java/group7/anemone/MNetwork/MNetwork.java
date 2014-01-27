package group7.anemone.MNetwork;

import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;

/**
 * The MNetwork class encapsulates a set of neurons and a set of synapses
 * that together describe a network.
 */
public class MNetwork implements Serializable{
	private static final long serialVersionUID = 6478084195441289384L;
	private ArrayList<MNeuron> neurons;
	private ArrayList<MSynapse> synapses;

	public MNetwork(Collection<MNeuron> neurons,
		Collection<MSynapse> synapses)
	{
		this.neurons = new ArrayList<MNeuron>(neurons);
		this.synapses = new ArrayList<MSynapse>(synapses);
	}

	public ArrayList<MNeuron> getNeurons() {
		ArrayList<MNeuron> tmpNeurons = new ArrayList<MNeuron>(neurons);
		
		return tmpNeurons;
	}

	public ArrayList<MSynapse> getSynapses() {
		ArrayList<MSynapse> tmpSynapses =
			new ArrayList<MSynapse>(synapses);
		
		return tmpSynapses;
	}
	
	public float getVertexNumber() {
		return neurons.size();
	}
	
	public void setNeurons(Collection<MNeuron> newNeurons) {
		neurons = new ArrayList<MNeuron>(newNeurons);
	}
}