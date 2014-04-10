package group7.anemone.Genetics;

import group7.anemone.HyperNeatGenetics.HyperNeatNode;

import java.io.Serializable;

/*
 * Class to hold genes, which make up the representation of the neural network. Each is roughly an
 * edge/connector within the network, with a single input and output.
 */
public class GenomeEdge implements Serializable{

	private static final long serialVersionUID = -27536139896199028L;
	private int historicalMarker;
	private HyperNeatNode in;
	private HyperNeatNode out;
	private double weight;
	private int delay;

	public GenomeEdge(int hist, HyperNeatNode in, HyperNeatNode out, double weight, int delay) {
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
	
	public HyperNeatNode getIn() {
		return this.in;
	}
	
	public HyperNeatNode getOut() {
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

	/*
	@Override
	public boolean equals(Object gene) {
		 if (gene == null)
	            return false;
	     if (gene == this)
	            return true;
	     if (!(gene instanceof Gene))
	            return false;

	     Gene rhs = (Gene) gene;
	     if (in == rhs.in && out == rhs.out) {
	    	 return true;
	     } else {
	    	 return false;
	     }
	}*/
}