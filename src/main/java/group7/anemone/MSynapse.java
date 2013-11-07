package group7.anemone;

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
public class MSynapse {
	private MNeuron pre, post;
	private double weight;
	private int delay;

	MSynapse(MNeuron pre, MNeuron post, double weight, int delay) {
		this.pre = pre;
		this.post = post;
		this.weight = weight;
		this.delay = delay;
	}

	MNeuron getPreNeuron() {
		return pre;
	}

	MNeuron getPostNeuron() {
		return post;
	}

	double getWeight() {
		return weight;
	}

	int getDelay() {
		return delay;
	}

	void doPSP() {
		post.addCurrent(weight);
	}
}