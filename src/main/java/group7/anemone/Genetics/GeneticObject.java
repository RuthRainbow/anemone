package group7.anemone.Genetics;

import java.io.Serializable;

public abstract class GeneticObject implements Serializable {
	private static final long serialVersionUID = -6042005379241215107L;
	
	private int speciesId;
	private final Parents parents;

	public GeneticObject(int speciesId, GeneticObject mother, GeneticObject father) {
		this.speciesId = speciesId;
		this.parents = new Parents(mother, father);
	}
	
	public abstract int getSize();
	
	public abstract Object getGeneticRep();
	
	public int getSpeciesId() {
		return this.speciesId;
	}
	
	public void setSpecies(int speciesId) {
		this.speciesId = speciesId;
	}
	
	public GeneticObject getMother() {
		return this.parents.mother;
	}
	
	public GeneticObject getFather() {
		return this.parents.father;
	}
	
	public class Parents {
		private final GeneticObject mother;
		private final GeneticObject father;
		
		public Parents(GeneticObject mother, GeneticObject father) {
			this.mother = mother;
			this.father = father;
		}
	}
}
