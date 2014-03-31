package group7.anemone.Genetics;


import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Genome class to hold the Gene array along with a species id.
 */
public class Genome implements Serializable {
	private static final long serialVersionUID = -9023930914349095877L;
	// Genes represent edges
	private final Gene[] genome;
	private final ArrayList<NeatNode> nodes;
	
	public Genome(Gene[] genome, Collection<NeatNode> nodes) {
		this.genome = genome;
		this.nodes = new ArrayList<NeatNode>(nodes);
	}
	
	@Override
	public String toString() {
		return "Genome: " + Arrays.toString(this.genome);
	}
	
	public int getLength() {
		return this.genome.length;
	}
	
	public NeatNode getXthIn(int x) {
		return this.genome[x].getIn();
	}
	
	public NeatNode getXthOut(int x) {
		return this.genome[x].getOut();
	}
	
	public double getXthWeight(int x) {
		return this.genome[x].getWeight();
	}

	public int getXthDelay(int x) {
		return this.genome[x].getDelay();
	}

	public Gene[] getGene() {
		return this.genome;
	}
	
	public ArrayList<NeatNode> getNodes() {
		return this.nodes;
	}

	public int getXthHistoricalMarker(int x) {
		return this.genome[x].getHistoricalMarker();
	}

	public Gene getXthGene(int x) {
		return this.genome[x];
	}

}
