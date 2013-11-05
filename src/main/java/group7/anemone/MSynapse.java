package group7.anemone;

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