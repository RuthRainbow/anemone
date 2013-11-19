package group7.anemone;

public class Gene {
	public int historicalMarker;
	public int[] linkedNodes;
	public double weight;
	public int delay;
	
	Gene(int hist, int[]links, double wei, int del) {
		historicalMarker = hist;
		linkedNodes = links;
		weight = wei;
		delay = del;
	}
}
