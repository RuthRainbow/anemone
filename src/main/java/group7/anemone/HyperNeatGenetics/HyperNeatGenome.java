package group7.anemone.HyperNeatGenetics;


import group7.anemone.Genetics.Genome;
import group7.anemone.Genetics.GenomeEdge;

import java.util.Collection;
import java.util.List;

/*
 * Genome class to hold the Gene array along with a species id.
 */
public class HyperNeatGenome extends Genome<HyperNeatNode> {
	private static final long serialVersionUID = -9023930914349095877L;

	private int historicalMarker;
	
	public HyperNeatGenome(
			List<GenomeEdge<HyperNeatNode>> genome,
			Collection<HyperNeatNode> nodes,
			int historicalMarker) {
		super(genome, nodes);
		this.historicalMarker = historicalMarker;
	}
	
	public int getHistoricalMarker() {
		return this.historicalMarker;
	}
}
