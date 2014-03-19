package group7.anemone.Genetics;


import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;

/*
 * Genome class to hold the Gene array along with a species id.
 */
public class Genome implements Serializable {
	private static final long serialVersionUID = -9023930914349095877L;
	// Genes represent edges
	private final ArrayList<NeatEdge> genome;
	private final ArrayList<NeatNode> nodes;
	private final Parents parents;
	// Species must be changeable after creation.
	private int speciesId;
	
	public Genome(ArrayList<NeatEdge> genome, Collection<NeatNode> nodes, int speciesId,
		Genome mother, Genome father) {
		this.genome = genome;
		this.nodes = new ArrayList<NeatNode>(nodes);
		this.speciesId = speciesId;
		this.parents = new Parents(mother, father);
	}
	
	// We know this is an ArrayList so the cast should be safe.
	@SuppressWarnings("unchecked")
	public Genome clone() {
		return new Genome(
				(ArrayList<NeatEdge>) this.genome.clone(),
				(Collection<NeatNode>) this.nodes.clone(),
				this.speciesId,
				this.parents.mother,
				this.parents.father);
	}
	
	public void setSpecies(int speciesId) {
		this.speciesId = speciesId;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Genome)) {
    		return false;
    	} else {
			Genome otherGenome = (Genome) other;
			if (this.genome.size() != otherGenome.getGeneLength()) return false;
			for (int i = 0; i < this.genome.size(); i++) {
				if (!this.genome.get(i).equals(otherGenome.getXthGene(i))) return false;
			}
			if (this.nodes.size() != otherGenome.getNodes().size()) return false;
			for (int i = 0; i < this.nodes.size(); i++) {
				if (!this.nodes.get(i).equals(otherGenome.getNodes().get(i))) return false;
			}
    		if (this.parents.father != otherGenome.getFather()) return false;
    		if (this.parents.mother != otherGenome.getMother()) return false;
    		if (this.speciesId != otherGenome.getSpeciesId()) return false;
    		return true;
    	}
	}
	
	@Override
	public String toString() {
		return "Genome: " + this.genome + " Nodes: " + this.nodes +
				" Species: " + this.speciesId + " Parents: " + this.parents;
	}
	
	public int getGeneLength() {
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

	public ArrayList<NeatEdge> getGene() {
		return this.genome;
	}

	public int getSpeciesId() {
		return this.speciesId;
	}
	
	public ArrayList<NeatNode> getNodes() {
		return this.nodes;
	}

	public int getXthHistoricalMarker(int x) {
		return this.genome.get(x).getHistoricalMarker();
	}

	public NeatEdge getXthGene(int x) {
		return this.genome.get(x);
	}
	
	public Genome getMother() {
		return this.parents.mother;
	}
	
	public Genome getFather() {
		return this.parents.father;
	}
	
	private class Parents implements Serializable{
		private static final long serialVersionUID = -4797717951064837306L;
		private Genome mother;
		private Genome father;
		
		public Parents(Genome mother, Genome father) {
			this.mother = mother;
			this.father = father;
		}
		
		@Override
		public String toString() {
			return "Mother: " + this.mother + " Father: " + this.father;
		}
	}
}
