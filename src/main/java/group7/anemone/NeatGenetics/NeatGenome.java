package group7.anemone.NeatGenetics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import group7.anemone.Genetics.GeneticObject;
import group7.anemone.Genetics.Genome;
import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.Genetics.Parents;

/**
 * NEAT Genome class adding parents and species ID of a genetic object.
 */
public class NeatGenome extends Genome<NeatNode> implements GeneticObject {
	private static final long serialVersionUID = -3199121174997990956L;
	// Genes represent edges
	private final Parents<NeatGenome> parents;
	// Species must be changeable after creation.
	private int speciesId;

	public NeatGenome(
			ArrayList<GenomeEdge<NeatNode>> genome,
			Collection<NeatNode> nodes,
			int speciesId,
			NeatGenome mother,
			NeatGenome father) {
		super(genome, nodes);
		this.speciesId = speciesId;
		this.parents = new Parents<NeatGenome>(mother, father);
	}

	// We know this is an ArrayList so the cast should be safe.
	@SuppressWarnings("unchecked")
	public NeatGenome clone() {
		return new NeatGenome(
				(ArrayList<GenomeEdge<NeatNode>>) this.genome.clone(),
				(Collection<NeatNode>) this.copyNodes(),
				this.speciesId,
				this.parents.getMother(),
				this.parents.getFather());
	}

	public void setSpecies(int speciesId) {
		this.speciesId = speciesId;
	}

	public int getSize() {
		return this.genome.size();
	}

	public NeatNode getXthIn(int x) {
		return this.genome.get(x).getIn();
	}

	public NeatNode getXthOut(int x) {
		return this.genome.get(x).getOut();
	}

	public double getXthWeight(int x) {
		return this.genome.get(x).getWeight();
	}

	public int getXthDelay(int x) {
		return this.genome.get(x).getDelay();
	}

	public ArrayList<GenomeEdge<NeatNode>> getGeneticRep() {
		return this.genome;
	}

	public int getSpeciesId() {
		return this.speciesId;
	}

	public List<NeatNode> getNodes() {
		return this.nodes;
	}

	public int getXthHistoricalMarker(int x) {
		return this.genome.get(x).getHistoricalMarker();
	}

	public GenomeEdge<NeatNode> getXthGene(int x) {
		return this.genome.get(x);
	}

	public NeatGenome getMother() {
		return this.parents.getMother();
	}

	public NeatGenome getFather() {
		return this.parents.getFather();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof NeatGenome)) {
			return false;
		} else {
			NeatGenome otherGenome = (NeatGenome) other;
			if (this.genome.size() != otherGenome.getSize()) return false;
			for (int i = 0; i < this.genome.size(); i++) {
				if (!this.genome.get(i).equals(otherGenome.getXthGene(i))) return false;
			}
			if (this.nodes.size() != otherGenome.getNodes().size()) return false;
			for (int i = 0; i < this.nodes.size(); i++) {
				if (!this.nodes.get(i).equals(otherGenome.getNodes().get(i))) return false;
			}
			if (this.parents.getMother() == null && otherGenome.getMother() != null) return false;
			if (this.parents.getMother() != null && otherGenome.getMother() == null) return false;
			if (this.parents.getMother() != null && otherGenome.getMother() != null
					&& !this.parents.getMother().equals(otherGenome.getMother())) return false;
			if (this.parents.getFather() == null && otherGenome.getFather() != null) return false;
			if (this.parents.getFather() != null && otherGenome.getFather() == null) return false;
			if (this.parents.getFather() != null && otherGenome.getFather() != null
					&& !this.parents.getFather().equals(otherGenome.getFather())) return false;
			if (this.speciesId != otherGenome.getSpeciesId()) return false;
			return true;
		}
	}

	@Override
	public String toString() {
		return "Genome: " + this.genome + " Nodes: " + this.nodes +
				" Species: " + this.speciesId;// + " Parents: " + this.parents;
	}

}
