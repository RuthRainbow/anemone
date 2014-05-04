package group7.anemone.Genetics;

import java.io.Serializable;

/**
 * Class to hold edges as a representation of the neural network.
 */
public class GenomeEdge<node extends GenomeNode> implements Serializable{

	private static final long serialVersionUID = -27536139896199028L;
	private int historicalMarker;
	private node in;
	private node out;
	private double weight;
	private int delay;

	public GenomeEdge(int hist, node in, node out, double weight, int delay) {
		this.historicalMarker = hist;
		this.in = in;
		this.out = out;
		this.weight = weight;
		this.delay = delay;
	}

	@Override
	public String toString() {
		return "MARKER: " + historicalMarker +
				" IN: " + in +
				" OUT: " + out +
				" WEIGHT: " + weight +
				" DELAY: " + delay;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof GenomeEdge)) {
			return false;
		} else {
			@SuppressWarnings("unchecked")
			GenomeEdge<node> otherEdge = (GenomeEdge<node>) other;
			if (this.historicalMarker != otherEdge.historicalMarker) return false;
			if (this.delay != otherEdge.delay) return false;
			if (!this.in.equals(otherEdge.in)) return false;
			if (!this.out.equals(otherEdge.out)) return false;
			if (this.weight != otherEdge.weight) return false;
			return true;
		}
	}
	
	public node getIn() {
		return this.in;
	}
	
	public node getOut() {
		return this.out;
	}

	public double getWeight() {
		return this.weight;
	}
	
	public int getDelay() {
		return this.delay;
	}

	public int getHistoricalMarker() {
		return this.historicalMarker;
	}

	public void addWeight(double change) {
		this.weight = Math.abs(this.weight + change);
	}
}