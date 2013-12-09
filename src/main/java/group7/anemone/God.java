package group7.anemone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class God implements Serializable{

	private static final long serialVersionUID = 619717007643693268L;
	
	// Mutation chances:
	private final double structuralMutationChance = 0.7f;
	private final double addConnectionChance = 0.5f;
	private final double addNodeChance = 0.5f;
	private final double weightMutationChance = 0.3f;
	// (chance of decrease is 1 - the chance of increase)
	private final double weightIncreaseChance = 0.5f;
	
	// Crossover chances:
	private final double twinChance = 0.05f;
	private final double matchedGeneChance = 0.5f;

	private double bestFitness;
	private double worstFitness;
	private double averageFitness;
	private int noImprovementCount = 0;

	// ************* THIS DEPENDS ON MINIMAL NETWORK ****************
	private int nextMarker = 89;
	private ArrayList<Gene> newGenes;
	
	// The ordered list of all species, with each represented by a member from the
	// previous generation.
	private ArrayList<Species> species;
	// The distances between all genes:
	private HashMap<AgentPair, Double> distances;
	private final double offspringProportion = 0.5f; // ALSO COMPLETELY ARBITRARY
	
	// Parameters for use in difference calcuation (can be tweaked).
	private final double c1 = 0.5f; //weighting of excess genes
	private final double c2 = 0.5f; //weighting of disjoint genes
	private final double c3 = 0.5f; //weighting of weight differences
	// Threshold for max distance between species member and representative.
	// INCREASE THIS IF YOU THINK THERE ARE TOO MANY SPECIES!
	private final double compatibilityThreshold = 22;
	
	public God() {
		this.species = new ArrayList<Species>();
		this.distances = new HashMap<God.AgentPair, Double>();
	}

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}

	// Method to breed the entire population without species.
	protected HashMap<Gene[], Integer> BreedPopulation(ArrayList<Agent> agents) {
		newGenes = new ArrayList<Gene>();
		ArrayList<AgentFitness> selectedAgents = Selection(agents);
		System.out.println("selecting " + selectedAgents.size());
		ArrayList<Gene[]> children = GenerateChildren(selectedAgents);
		HashMap<Gene[], Integer> childrenSpecies = new HashMap<Gene[], Integer>();
		for (Gene[] child : children) {
			childrenSpecies.put(child, 0);
		}
		return childrenSpecies;
	}
	
	protected HashMap<Gene[], Integer> BreedWithSpecies(ArrayList<Agent> agents) {
		newGenes = new ArrayList<Gene>();
		// Clear species for a new gen
		for (Species specie : species) {
			specie.clear();
		}
		// Put each agent given for reproduction into a species.
		for (Agent agent : agents) {
			// Try to increase efficiency by ignoring awful agents
			boolean foundSpecies = false;
			AgentFitness thisAgent = new AgentFitness(agent);
			for (Species specie : species) {
				AgentFitness rep = specie.rep;
				double dist = getDistance(thisAgent, rep);
				if (dist < compatibilityThreshold) {
					foundSpecies = true;
					specie.addMember(thisAgent);
				}
			}
			if (!foundSpecies) {
				int newSpeciesId = species.size() + 1;
				species.add(new Species(thisAgent, newSpeciesId));
			}
		}
		
		shareFitnesses();
		HashMap<Gene[], Integer> children = new HashMap<Gene[], Integer>();
		// Breed each species
		for (Species specie : species) {
			ArrayList<Gene[]> speciesChildren = breedSpecies(specie, agents.size());
			for (Gene[] child : speciesChildren) {
				children.put(child, specie.id);
			}
		}
		return children;
	}
	
	private ArrayList<Gene[]> breedSpecies(Species specie, int popSize) {
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();
		if (specie.members.size() < 2) {
			for (AgentFitness agent : specie.members) {
				children.add(agent.stringRep);
			}
			return children;
		}
		double summedFitness = 0;
		for (AgentFitness agent : specie.members) {
			summedFitness += agent.fitness;
		}
		int numOffspring = Math.min(2, (int) Math.floor(summedFitness * offspringProportion));
		System.out.println("*************** GENERATING " + numOffspring + " children for species " + specie.id);
		// Breed the top n! (Members is presorted :))
		int i = 0;
		while (children.size() < specie.members.size()/2 && children.size() < numOffspring) {
			children.addAll(
					CreateOffspring(specie.members.get(i), specie.members.get(i+1)));
			i += 2;
		}
		return children;
	}

	// Share fitnesses over species by updating AgentFitness objects (see NEAT paper)
	protected void shareFitnesses() {
		// For every species...
		for (Species specie : species) {
			// For every member of this species...
			for (AgentFitness agent : specie.members) {
				double fitnessTotal = 0;
				// average fitness over all other members of this species...
				for (AgentFitness member : specie.members) {
					if (!member.equals(agent)) {
						fitnessTotal += member.fitness;
					}
				}
				agent.fitness = agent.fitness / fitnessTotal;
			}
		}
	}
	
	protected int sharingFunction(double distance) {
		if (distance > compatibilityThreshold) {
			return 0; // Seems pointless. Why not only compare with dudes from the same species, 
			// if all others will be made 0?!
		} else {
			return 1;
		}
	}
	
	// Return computability distance between two networks (see NEAT speciation).
	protected double getDistance(AgentFitness thisAgent, AgentFitness speciesRep) {
		AgentPair agentPair = new AgentPair(thisAgent, speciesRep);
		// Firstly, check if we have already calculated this distance.
		if (distances.containsKey(agentPair)) {
			return distances.get(agentPair);
		}
		Gene[] a = thisAgent.stringRep;
		Gene[] b = speciesRep.stringRep;
		int numExcess = Math.abs(a.length - b.length);
		int numDisjoint = 0;
		double weightDiff = 0.0;
		int minLength = Math.min(a.length, b.length);
		int maxLength = Math.max(a.length, b.length);
		for (int i = 0; i < minLength; i++) {
			if (a[i].historicalMarker != b[i].historicalMarker) {
				numDisjoint++;
			} else {
				weightDiff += Math.abs(a[i].weight - b[i].weight);
			}
		}
		double distance = (c1*numExcess)/maxLength + (c2*numDisjoint)/maxLength + (c3*weightDiff);
		// Save this distance so we don't need to recalculate:
		distances.put(agentPair, distance);
		return distance;
	}

	protected ArrayList<AgentFitness> Selection(ArrayList<Agent> agents) {
		ArrayList<AgentFitness> selectedAgents = new ArrayList<AgentFitness>();
		double last_best = bestFitness;
		double last_average = averageFitness;
		averageFitness = 0;
		for (Agent agent : agents) {
			double fitness = agent.getFitness();
			averageFitness += fitness;
			// This number is completely arbitrary, depends on fitness function
			if (fitness * getRandom() > last_average) {
				selectedAgents.add(new AgentFitness(agent));
			}
			 if (agent.getFitness() > bestFitness) {
                 bestFitness = agent.getFitness();
			 } else if (agent.getFitness() < worstFitness) {
                 worstFitness = agent.getFitness();
			 }
		}
		averageFitness = averageFitness / agents.size();
		// Keep track of the number of generations without improvement.
		if (last_best >= bestFitness) {
			noImprovementCount++;
		} else {
			noImprovementCount--;
		}
		return selectedAgents;
	}

	protected ArrayList<Gene[]> GenerateChildren(
			ArrayList<AgentFitness> selectedAgents) {
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();

		while (selectedAgents.size() > 1) {
			AgentFitness mother = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(mother);
			AgentFitness father = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(father);
			/*
			// If mother and father are the same, just mutate.
			if (mother.getStringRep().equals(father.getStringRep())) {
				children.add(mutate(mother.getStringRep()));
			} else {*/
			children.add(crossover(father, mother));
			//}
		}

		ArrayList<Gene[]> mutatedChildren = new ArrayList<Gene[]>();
		// Put every child through mutation process
		for (Gene[] child : children) {
			mutatedChildren.add(mutate(child));
		}

		return mutatedChildren;
	}
	
	protected HashMap<Gene[], Integer> createOffspring(Agent mother, Agent father) {
		AgentFitness motherFitness = new AgentFitness(mother);
		AgentFitness fatherFitness = new AgentFitness(father);
		ArrayList<Gene[]> children = CreateOffspring(motherFitness, fatherFitness);
		HashMap<Gene[], Integer> childrenSpecies = new HashMap<Gene[], Integer>();
		for (Gene[] child : children) {
			childrenSpecies.put(child, father.getSpeciesId());
		}
		return childrenSpecies;
	}

	// Method to create offspring from 2 given parents.
	protected ArrayList<Gene[]> CreateOffspring(
			AgentFitness mother, AgentFitness father) {
		newGenes = new ArrayList<Gene>();
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();

		children.add(crossover(mother, father));
		if (getRandom() < twinChance) {
			children.add(crossover(mother, father));
		}
		for (int i = 0; i < children.size(); i++) {
			children.set(i, mutate(children.get(i)));
		}
		return children;
	}

	protected Gene[] crossover(AgentFitness mother, AgentFitness father) {
		if (mother.fitness > father.fitness) {
			return crossover(mother.stringRep, father.stringRep);
		} else {
			return crossover(father.stringRep, mother.stringRep);
		}
	}

	// Method for crossover - return crossover method you want.
	// The mother should always be the parent with the highest fitness.
	// TODO may be a problem if they have equal fitness that one is always dominant
	private Gene[] crossover(Gene[] dominant, Gene[] recessive) {
		List<Gene> child = new ArrayList<Gene>();

		// "Match" genes...
		Map<Gene, Gene> matches = new HashMap<Gene, Gene>();
		int marker = 0;
		for (int i = 0; i < dominant.length; i++) {
			for (int j = marker; j < recessive.length; j++) {
				if (dominant[i].historicalMarker == recessive[j].historicalMarker) {
					marker = j + 1;
					matches.put(dominant[i], recessive[j]);
				}
			}
		}

		// Generate the child
		for (int i = 0; i < dominant.length; i++) {
			Gene gene = dominant[i];
			if (matches.containsKey(gene)) {
				// Randomly select matched gene from either parent
				if (getRandom() < matchedGeneChance) {
					child.add(gene);
				} else {
					child.add(matches.get(gene));
				}
			} else { //Else it didn't match, take it from the dominant
				child.add(gene);
			}
		}

		Gene[] childGene = new Gene[child.size()];
		for (int i = 0; i < child.size(); i++) {
			childGene[i] = child.get(i);
		}
		return childGene;
	}

	private Gene[] mutate(Gene[] child) {
		child = structuralMutation(child);
		return weightMutation(child);
	}

	// Mutate a gene structurally
	public Gene[] structuralMutation(Gene[] child) {
		List<Gene> mutatedChild = new ArrayList<Gene>();
		Set<Integer> historicalMarkersSet = new HashSet<Integer>();
		List<IntPair> edges = new ArrayList<IntPair>();
		int max = 0;
		for (Gene gene:child) {
			mutatedChild.add(gene);
			historicalMarkersSet.add(gene.in);
			historicalMarkersSet.add(gene.out);
			max = Math.max(gene.in, max);
			max = Math.max(gene.out, max);
			edges.add(new IntPair(gene.in, gene.out));
		}

		List<Integer> historicalMarkersList = new ArrayList<Integer>();
		historicalMarkersList.addAll(historicalMarkersSet);

		if (getRandom() < structuralMutationChance) {
			// Add connection
			if (getRandom() < addConnectionChance) {
				int left = 0;
				int right = 0;
				// Connect two arbitrary nodes - we don't care if they are already connected.
				// (Similar to growing multiple synapses).
				left = historicalMarkersList.get(
						(int) Math.floor(getRandom()*historicalMarkersList.size()));
				right = historicalMarkersList.get(
						(int) Math.floor(getRandom()*historicalMarkersList.size()));
				// If this mutated gene has already been created this gen, don't create another
				Gene newGene = new Gene(
						nextMarker, child[left].in, child[right].in, 0.0, 1);
				for (Gene gene : newGenes) {
					if (newGene.equals(gene)) {
						newGene = gene;
					}
				}
				mutatedChild.add(newGene);
				if (!newGenes.contains(newGene)) {
					nextMarker++;
				}
			}

			// Add a new node between two old connections
			if (getRandom() < addNodeChance) {
				// Choose a gene to split: (ASSUMED IT DOESN'T MATTER IF ALREADY AN EDGE BETWEEN)
				Gene toMutate = mutatedChild.get(
						(int) Math.floor(getRandom() * mutatedChild.size()));
				mutatedChild.remove(toMutate);
				Gene newLeftGene = new Gene(nextMarker, toMutate.in, max+1, 0.0, 1);
				for (Gene gene : newGenes) {
					if (newLeftGene.equals(gene)) {
						newLeftGene = gene;
					}
				}
				if (!newGenes.contains(newLeftGene)) {
					nextMarker++;
				}
				mutatedChild.add(newLeftGene);
				Gene newRightGene = new Gene(nextMarker, max+1, toMutate.out, 0.0, 1);
				for (Gene gene : newGenes) {
					if (newRightGene.equals(gene)) {
						newRightGene = gene;
					}
				}
				if (!newGenes.contains(newRightGene)) {
					nextMarker++;
				}
				mutatedChild.add(newRightGene);
			}
		}
		Gene[] mutatedGene = new Gene[mutatedChild.size()];
		for (int i = 0; i < mutatedChild.size(); i++) {
			mutatedGene[i] = mutatedChild.get(i);
		}
		return mutatedGene;
	}

	// Each weight is subject to random mutation.
	public Gene[] weightMutation(Gene[] child) {
		for (Gene gene : child) {
			if (getRandom() < weightMutationChance) {
				if (getRandom() < weightIncreaseChance) {
					gene.weight += getRandom();
				} else {
					gene.weight = Math.abs(gene.weight - getRandom());
				}
			}
		}
		return child;
	}

	// Packing social disaster - all elite individuals randomised except 1
	protected ArrayList<Gene[]> SocialDisasterPacking(ArrayList<Agent> agents, int type) {
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();

		boolean elite_agent_in = false;
		for (Agent agent : agents) {
			if (agent.getFitness() == bestFitness) {
				if (!elite_agent_in) {
					children.add(agent.getStringRep());
				} else {
					children.add(RandomlyGenerate());
				}
			} else {
				children.add(agent.getStringRep());
			}
		}

		return children;
	}

	// Judgement day social disaster - all individuals randomised except 1 elite
	// (might be SUPER EXPENSIVE)
	protected ArrayList<Gene[]> SocialDisasterJudgement(ArrayList<Agent> agents, int type) {
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();


		boolean elite_agent_in = false;
		for (Agent agent : agents) {
			if (agent.getFitness() == bestFitness && !elite_agent_in) {
				children.add(agent.getStringRep());
			} else {
				children.add(RandomlyGenerate());
			}
		}

		return children;
	}

	// Return the string representation of a new agent.
	protected Gene[] RandomlyGenerate() {
		throw new NotImplementedException();
	}

	public class NotImplementedException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public NotImplementedException(){}
	}

	public class IntPair {
	    private int first;
	    private int second;

	    public IntPair(int first, int second) {
	    	this.first = first;
	    	this.second = second;
	    }

	    public boolean equals(Object other) {
	    	if (!(other instanceof IntPair)) {
	    		return false;
	    	} else {
	    		IntPair otherPair = (IntPair) other;
	    		if (this.first == otherPair.first && this.second == otherPair.second ||
	    				this.first == otherPair.second && this.second == otherPair.first) {
	    			return true;
	    		} else {
	    			return false;
	    		}
	    	}
	    }

	    public String toString()
	    {
	           return "(" + first + ", " + second + ")";
	    }
	}
	
	// Class to hold two agents together so we can map to a distance.
	private class AgentPair {
		private AgentFitness first;
		private AgentFitness second;
		
		public AgentPair(AgentFitness first, AgentFitness second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof AgentPair)) {
	    		return false;
	    	} else {
	    		AgentPair otherPair = (AgentPair) other;
	    		if (this.first == otherPair.first && this.second == otherPair.second ||
	    				this.first == otherPair.second && this.second == otherPair.first) {
	    			return true;
	    		} else {
	    			return false;
	    		}
	    	}
		}
	}
	
	// Class used to hold an entire species.
	private class Species {
		private ArrayList<AgentFitness> members;
		private AgentFitness rep;
		private int id;
		
		public Species(AgentFitness rep, int id) {
			this.rep = rep;
			members = new ArrayList<AgentFitness>();
			members.add(rep);
			this.id =id;
		}
		
		private void addMember(AgentFitness newMember) {
			members.add(newMember);
			Collections.sort(members);
		}
		
		private void clear() {
			members.clear();
			members.add(rep);
		}
	}
	
	// This class is used so we can easily compare agents by fitness.
	// Also used to be more lightweight than Agent class.
	private class AgentFitness implements Comparable<AgentFitness> {
		private Gene[] stringRep;
		private double fitness;
		
		public AgentFitness(Agent agent) {
			this.stringRep = agent.getStringRep();
			this.fitness = agent.getFitness();
		}

		@Override
		public int compareTo(AgentFitness other) {
			if (this.fitness > other.fitness) {
				return 1;
			} else if (this.fitness < other.fitness) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}