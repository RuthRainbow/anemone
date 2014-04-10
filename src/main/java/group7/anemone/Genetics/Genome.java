package group7.anemone.Genetics;

import group7.anemone.Genetics.GenomeEdge;

import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Genome class to hold the Gene array along with a species id.
 */
public abstract class Genome implements Serializable {
	private static final long serialVersionUID = -9023930914349095877L;
	// Genes represent edges
	private final GenomeEdge[] genome;
	private final ArrayList<GenomeNode> nodes;

	public Genome(GenomeEdge[] genome, Collection<? extends GenomeNode> nodes) {
		this.genome = genome;
		this.nodes = new ArrayList<GenomeNode>(nodes);
	}

	@Override
	public String toString() {
		return "Genome: " + Arrays.toString(this.genome);
	}

	public int getLength() {
		return this.genome.length;
	}

	public GenomeNode getXthIn(int x) {
		return this.genome[x].getIn();
	}

	public GenomeNode getXthOut(int x) {
		return this.genome[x].getOut();
	}

	public double getXthWeight(int x) {
		return this.genome[x].getWeight();
	}

	public int getXthDelay(int x) {
		return this.genome[x].getDelay();
	}

	public GenomeEdge[] getGene() {
		return this.genome;
	}

	public Collection<? extends GenomeNode> getNodes() {
		return this.nodes;
	}

	public int getXthHistoricalMarker(int x) {
		return this.genome[x].getHistoricalMarker();
	}

	public GenomeEdge getXthGene(int x) {
		return this.genome[x];
	}
}
