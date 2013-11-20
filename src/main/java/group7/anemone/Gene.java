package group7.anemone;


public class Gene {
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
				"\nIN: " + in +
				"\nOUT: " + out +
				"\nWEIGHT: " + weight +
				"\nDELAY: " + delay;
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