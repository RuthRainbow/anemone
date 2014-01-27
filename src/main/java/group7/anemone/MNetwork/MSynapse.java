package group7.anemone.MNetwork;

import java.io.Serializable;

/**
 * The MSynapse class represents one synapse. We allow an MSynapse to connect
 * two neurons, a 'preneuron' and a 'postneuron'. An MSynapse has a weight and
 * propagation delay associated with it, that should form the basis for future
 * implementations of short/long term memory (via STDP, say).
 * <p>
 * TODO: - STDP/Memory R and D
 */
public class MSynapse implements Serializable {
	private static final long serialVersionUID = -8827613839693448412L;

	/* The pre- and post-synaptic neurons. */
	private MNeuron pre, post;

	/* The weight (efficacy) of the synapse. */
	private double weight;

	/*
	 The delay asociate with this synapse (must be less than the
	 simulation event horizon.
	 */
	private int delay;

	/**
	 * Copy constructor.
	 * 
	 * @param synapse	the synapse to be copied
	 */
	public MSynapse(MSynapse synapse) {
		this.pre = new MNeuron(synapse.pre);
		this.post = new MNeuron(synapse.post);
		this.weight = synapse.weight;
		this.delay = synapse.delay;
	}
	
	/**
	 * Constructs a synapse with the given parameters.
	 * 
	 * @param pre		the pre-synaptic neuron
	 * @param post		the post-synaptic neuron
	 * @param weight	the weight for this synapse
	 * @param delay		the delay for this synapse
	 */
	public MSynapse(MNeuron pre, MNeuron post, double weight, int delay) {
		this.pre = pre;
		this.post = post;
		this.weight = weight;
		this.delay = delay;
	}

	public MNeuron getPreNeuron() {
		return pre;
	}

	public MNeuron getPostNeuron() {
		return post;
	}

	public double getWeight() {
		return weight;
	}

	public int getDelay() {
		return delay;
	}

	/**
	 * Adds current (`post-synaptic potentiation') to the post-synaptic
	 * neuron.
	 */
	public void doPSP() {
		post.addCurrent(weight);
	}
}
