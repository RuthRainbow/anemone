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
	private int speciesId;
	private Parents parents;
	
	public Genome(Gene[] genome, Collection<NeatNode> nodes, int speciesId,
		Genome mother, Genome father) {
		this.genome = genome;
		this.nodes = new ArrayList<NeatNode>(nodes);
		this.speciesId = speciesId;
		this.parents = new Parents(mother, father);
	}
	
	public void setSpecies(int speciesId) {
		this.speciesId = speciesId;
	}
	
	@Override
	public String toString() {
		return "Genome: " + Arrays.toString(this.genome) + " Species: " + this.speciesId;
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

	public int getSpeciesId() {
		return this.speciesId;
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
	
	public Genome getMother() {
		return this.parents.mother;
	}
	
	public Genome getFather() {
		return this.parents.father;
	}
	
	private class Parents {
		private Genome mother;
		private Genome father;
		
		public Parents(Genome mother, Genome father) {
			this.mother = mother;
			this.father = father;
		}
	}
}
