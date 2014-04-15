package group7.anemone.NeatGenetics;

import group7.anemone.Genetics.GenomeNode;
import group7.anemone.MNetwork.MFactory;
import group7.anemone.MNetwork.MNeuronParams;

public class NeatNode extends GenomeNode {
	private MNeuronParams params;

	public NeatNode(int id, MNeuronParams params) {
		super(id);
		this.params = new MNeuronParams(params);
	}

	public static NeatNode createRSNeatNode(int id) {
		MNeuronParams params;
		NeatNode node;

		params = MFactory.createRSNeuronParams();
		node = new NeatNode(id, params);

		return node;
	}

	public MNeuronParams getParams() {
		return new MNeuronParams(params);
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ID: " + this.id ;//+ " Params: " + this.params;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof NeatNode)) {
			return false;
		} else {
			NeatNode othernode = (NeatNode) other;
			if (this.id != othernode.id) return false;
			if (this.params.equals(othernode.params)) return true;
		}
		return false;
	}
}
