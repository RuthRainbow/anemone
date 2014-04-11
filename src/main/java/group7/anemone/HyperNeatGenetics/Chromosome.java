package group7.anemone.HyperNeatGenetics;


import group7.anemone.Genetics.GeneticObject;
import group7.anemone.Genetics.Parents;

import java.io.Serializable;
import java.util.ArrayList;

public class Chromosome implements GeneticObject, Serializable {
	private static final long serialVersionUID = 4284948709046079202L;
	
	private int speciesId;
	private final Parents<Chromosome> parents;
	private ArrayList<HyperNeatGenome> genome;
	
	public Chromosome(ArrayList<HyperNeatGenome> genome,
					  int speciesId,
					  Chromosome mother,
					  Chromosome father) {
		this.speciesId = speciesId;
		this.parents = new Parents<Chromosome>(mother, father);
		this.genome = genome;
	}
	
	public ArrayList<HyperNeatGenome> getGeneticRep() {
		return this.genome;
	}
	
	public int getSize() {
		return this.genome.size();
	}

	public HyperNeatGenome getXthGenome(int i) {
		return this.genome.get(i);
	}

	public int getSpeciesId() {
		return this.speciesId;
	}

	public void setSpecies(int speciesId) {
		this.speciesId = speciesId;
	}

	public GeneticObject getMother() {
		return this.parents.getMother();
	}

	public GeneticObject getFather() {
		return this.parents.getFather();
	}

}
