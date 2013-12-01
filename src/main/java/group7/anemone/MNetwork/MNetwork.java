package group7.anemone.MNetwork;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The MNetwork class encapsulates a set of neurons and a set of synapses
 * that together describe a network.
 */
public class MNetwork implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6478084195441289384L;
	private ArrayList<MNeuron> neurons;
	private ArrayList<MSynapse> synapses;
	private final float width, height, depth;

	public MNetwork(ArrayList<MNeuron> neurons, ArrayList<MSynapse> synapses,
		float width, float height, float depth)
	{
		this.neurons = neurons;
		this.synapses = synapses;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public ArrayList<MNeuron> getNeurons() {
		return neurons;
	}

	public ArrayList<MSynapse> getSynapses() {
		return synapses;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getDepth() {
		return depth;
	}
}