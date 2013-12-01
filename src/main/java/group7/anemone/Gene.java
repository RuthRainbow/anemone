package group7.anemone;

import java.io.Serializable;

/*
 * Class to hold genes, which make up the representation of the neural network. Each is roughly an
 * edge/connector within the network, with a single input and output.
 */
public class Gene implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -27536139896199028L;
	public int historicalMarker;
	public int in;
	public int out;
	public double weight;
	public int delay;

	Gene(int hist, int in, int out, double wei, int del) {
		this.historicalMarker = hist;
		this.in = in;
		this.out = out;
		this.weight = wei;
		this.delay = del;
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
	}
}