package group7.anemone.HyperNeatGenetics;


import group7.anemone.Genetics.GeneticObject;

import java.util.ArrayList;

public class Chromosome extends GeneticObject {
	private static final long serialVersionUID = 4284948709046079202L;
	
	private ArrayList<HyperNeatGenome> genome;
	
	public Chromosome(ArrayList<HyperNeatGenome> genome,
					  int speciesId,
					  Chromosome mother,
					  Chromosome father) {
		super(speciesId, mother, father);
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

}
