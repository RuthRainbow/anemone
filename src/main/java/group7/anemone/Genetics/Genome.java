package group7.anemone.Genetics;


import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Immutable genome class to hold the Gene array along with a species id.
 */
public class Genome implements Serializable {
	private static final long serialVersionUID = -9023930914349095877L;
	private final Gene[] genome;
	private final ArrayList<NeatNode> nodes;
	private final int speciesId;
	private Parent parents;
	
	public Genome(Gene[] genome, Collection<NeatNode> nodes, int speciesId,
		Genome mother, Genome father)
	{
		this.genome = genome;
		this.nodes = new ArrayList<NeatNode>(nodes);
		this.speciesId = speciesId;
		this.parents = new Parent(mother, father);
	}
	
	@Override
	public String toString() {
		return "Genome: " + Arrays.toString(this.genome) + " Species: " + this.speciesId;
	}
	
	public int getLength() {
		return this.genome.length;
	}
	
	public int getXthIn(int x) {
		return this.genome[x].getIn();
	}
	
	public int getXthOut(int x) {
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
	
	private class Parent {
		private Genome mother;
		private Genome father;
		
		public Parent(Genome mother, Genome father) {
			this.mother = mother;
			this.father = father;
		}
	}
}
