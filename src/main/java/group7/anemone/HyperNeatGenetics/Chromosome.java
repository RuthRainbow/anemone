package group7.anemone.HyperNeatGenetics;


import group7.anemone.Genetics.GeneticObject;
import group7.anemone.Genetics.Parents;
import group7.anemone.CPPN.CPPN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class Chromosome implements GeneticObject, Serializable {
	private static final long serialVersionUID = 4284948709046079202L;
	
	private int speciesId;
	private final Parents<Chromosome> parents;
	private ArrayList<HyperNeatGenome> genomes;
	private ArrayList<HyperNeatGenome> neuronGenomes;
	private ArrayList<HyperNeatGenome> synapseGenomes;
	
	public Chromosome(ArrayList<HyperNeatGenome> genome,
					  int speciesId,
					  Chromosome mother,
					  Chromosome father) {
		this.speciesId = speciesId;
		this.parents = new Parents<Chromosome>(mother, father);
		this.genomes = genome;
		
		initGenomeLists();
	}
	
	private void initGenomeLists() {
		this.neuronGenomes = new ArrayList<HyperNeatGenome>();
		this.synapseGenomes = new ArrayList<HyperNeatGenome>();
		
		/* Put each genome in the right list. */
		for (HyperNeatGenome g : genomes) {
			if (g.getType() == HyperNeatGenome.Type.NEURON) {
				neuronGenomes.add(g);
			} else {
				synapseGenomes.add(g);
			}
		}
		
		/* Sort each genome based on layer number. */
		Collections.sort(neuronGenomes, new Comparator<HyperNeatGenome>() {
			public int compare(HyperNeatGenome g1, HyperNeatGenome g2) {
				if (g1.getLayerNum() == g2.getLayerNum())
					return 0;
				
				return g1.getLayerNum() < g2.getLayerNum() ? -1 : 1;
			}
		});
		
		/* Sort each synapse genome on layer number. */
		Collections.sort(synapseGenomes, new Comparator<HyperNeatGenome>() {
			public int compare(HyperNeatGenome g1, HyperNeatGenome g2) {
				if (g1.getLayerNum() == g2.getLayerNum())
					return 0;
				
				return g1.getLayerNum() < g2.getLayerNum() ? -1 : 1;
			}
		});
	}
	
	public CPPN getNeuronCPPN(int layer) {
		HyperNeatGenome g;
		
		g = neuronGenomes.get(layer);
		
		return g.generateCPPN();
	}
	
	public CPPN getSynapseCPPN(int layer) {
		HyperNeatGenome g;
		
		g = synapseGenomes.get(layer);
		
		return g.generateCPPN();
	}
	
	public ArrayList<HyperNeatGenome> getGeneticRep() {
		return this.genomes;
	}
	
	public int getSize() {
		return this.genomes.size();
	}

	public HyperNeatGenome getXthGenome(int i) {
		return this.genomes.get(i);
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
