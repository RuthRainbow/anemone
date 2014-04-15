package group7.anemone.Genetics;

import group7.anemone.Genetics.GenomeEdge;

import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/*
 * Genome class to hold the Gene array along with a species id.
 */
public abstract class Genome<node extends GenomeNode> implements Serializable {
	private static final long serialVersionUID = -9023930914349095877L;
	// Genes represent edges
	protected final ArrayList<GenomeEdge<node>> genome;
	protected final ArrayList<node> nodes;

	public Genome(List<GenomeEdge<node>> genome, Collection<node> nodes) {
		this.genome = new ArrayList<GenomeEdge<node>>(genome);
		this.nodes = new ArrayList<node>(nodes);
	}

	@Override
	public String toString() {
		return "Genome: " + this.genome;
	}

	public int getLength() {
		return this.genome.size();
	}

	public node getXthIn(int x) {
		return this.genome.get(x).getIn();
	}

	public node getXthOut(int x) {
		return this.genome.get(x).getOut();
	}

	public double getXthWeight(int x) {
		return this.genome.get(x).getWeight();
	}

	public int getXthDelay(int x) {
		return this.genome.get(x).getDelay();
	}

	public ArrayList<GenomeEdge<node>> getGene() {
		return this.genome;
	}

	public Collection<node> getNodes() {
		return this.nodes;
	}

	public int getXthHistoricalMarker(int x) {
		return this.genome.get(x).getHistoricalMarker();
	}

	public GenomeEdge<node> getXthGene(int x) {
		return this.genome.get(x);
	}
}