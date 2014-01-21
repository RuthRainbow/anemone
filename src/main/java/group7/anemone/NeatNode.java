package group7.anemone;

import group7.anemone.MNetwork.MNeuronParams;

public class NeatNode {
	MNeuronParams params;
	int id;

	public NeatNode(int id, MNeuronParams params) {
		this.params = params;
		this.id = id;
	}
}