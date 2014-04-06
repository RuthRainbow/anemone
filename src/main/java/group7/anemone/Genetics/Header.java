package group7.anemone.Genetics;

public class Header {
	private final int historicalMarker;
	private int numNodes;
	
	public Header(int hm, int numNodes) {
		this.historicalMarker = hm;
		this.numNodes = numNodes;
	}
	
	public int getHistoricalMarker() {
		return this.historicalMarker;
	}
	
	public int getNumNodes() {
		return this.numNodes;
	}
}
