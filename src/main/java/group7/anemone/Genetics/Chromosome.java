package group7.anemone.Genetics;

import java.util.ArrayList;

public class Chromosome {

	private int speciesId;
	private Parents parents;
	private ArrayList<Genome> genome;
	
	public Chromosome(ArrayList<Genome> genome,
					  int speciesId,
					  Chromosome mother,
					  Chromosome father) {
		this.genome = genome;
		this.speciesId = speciesId;
		this.parents = new Parents(mother, father);
	}
	
	public void setSpecies(int speciesId) {
		this.speciesId = speciesId;
	}
	
	public ArrayList<Genome> getGenome() {
		return this.genome;
	}
	
	public int getGenomeSize() {
		return this.genome.size();
	}

	public Genome getXthGenome(int i) {
		return this.genome.get(i);
	}

	public int getSpeciesId() {
		return this.speciesId;
	}
	
	public Chromosome getMother() {
		return this.parents.mother;
	}
	
	public Chromosome getFather() {
		return this.parents.father;
	}
	
	private class Parents {
		private final Chromosome mother;
		private final Chromosome father;
		
		public Parents(Chromosome mother, Chromosome father) {
			this.mother = mother;
			this.father = father;
		}
	}
}
