package group7.anemone.Genetics;

import java.util.ArrayList;

public class Chromosome {

	private int speciesId;
	private Parents parents;
	private ArrayList<Genome> genome;
	private ArrayList<Header> headers;
	
	public Chromosome(ArrayList<Header> header,
					  ArrayList<Genome> genome,
					  int speciesId,
					  Genome mother,
					  Genome father) {
		this.headers = header;
		this.genome = genome;
		this.speciesId = speciesId;
		this.parents = new Parents(mother, father);
	}
	
	public void setSpecies(int speciesId) {
		this.speciesId = speciesId;
	}
	
	public ArrayList<Header> getHeaders() {
		return this.headers;
	}
	
	public ArrayList<Genome> getGenome() {
		return this.genome;
	}
	
	public int getGenomeSize() {
		return this.genome.size();
	}

	public int getSpeciesId() {
		return this.speciesId;
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
