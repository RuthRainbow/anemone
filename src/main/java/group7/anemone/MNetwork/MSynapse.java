package group7.anemone.MNetwork;

import java.io.Serializable;

/**
 * The MSynapse class represents one synapse. We allow an MSynapse to
 * connect two neurons, a 'preneuron' and a 'postneuron'. An MSynapse
 * has a weight and propagation delay associated with it, that should
 * form the basis for future implementations of short/long term memory
 * (via STDP, say).
 * <p>
 * TODO:
 * - STDP/Memory R and D
 */
public class MSynapse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8827613839693448412L;
	private MNeuron pre, post;
	private double weight;
	private int delay;

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

	public void doPSP() {
		post.addCurrent(weight);
	}
}