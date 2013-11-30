package group7.anemone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class God {

	// NEAT uses an absolutely massive mutation chance
	private double mutation_chance = 0.03f;
	private final double twin_chance = 0.05f;

	private double best_fitness = 0;
	private double worst_fitness = 1;
	private double average_fitness = 0.1;
	private int no_improvement_count = 0;

	// ************* THIS DEPENDS ON MINIMAL NETWORK ****************
	private int next_marker = 89;
	private ArrayList<Gene> newGenes;
	
	// The ordered list of species, each represented by a member from the
	// previous generation
	private ArrayList<Gene[]> species;
	
	// Parameters for use in difference calcuation (can be tweaked).
	private final double c1 = 0.5;
	private final double c2 = 0.5;
	private final double c3 = 0.5;
	// Threshold for max distance between species member and representative.
	private final double compatibilityThreshold = 5;
	
	public God() {
		this.species = new ArrayList<Gene[]>();
	}

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}

	// Method to breed the entire population
	protected ArrayList<Gene[]> BreedPopulation(ArrayList<Agent> agents) {
		newGenes = new ArrayList<Gene>();
		ArrayList<Agent> selectedAgents = Selection(agents);
		System.out.println("selecting " + selectedAgents.size());
		return GenerateChildren(selectedAgents);
	}
	
	protected ArrayList<Gene[]> BreedWithSpecies(ArrayList<Agent> agents) {
		newGenes = new ArrayList<Gene>();
		HashMap<Gene[], ArrayList<Gene[]>> speciesMap =
				new HashMap<Gene[], ArrayList<Gene[]>>();
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();
		for (Gene[] gene : species) {
			ArrayList<Gene[]> newSpecies = new ArrayList<Gene[]>();
			speciesMap.put(gene, newSpecies);
		}
		// Put each agent given for reproduction into a species.
		for (Agent agent : agents) {
			Gene[] stringRep = agent.getStringRep();
			boolean foundSpecies = false;
			for (Gene[] gene : species) {
				double dist = getDistance(stringRep, gene);
				if (dist < compatibilityThreshold) {
					foundSpecies = true;
					speciesMap.get(gene).add(stringRep);
				}
			}
			if (!foundSpecies) {
				ArrayList<Gene[]> newSpecies = new ArrayList<Gene[]>();
				speciesMap.put(stringRep, newSpecies);
			}
		}
		
		return children;
	}
	
	// Return computability distance between two networks (see NEAT speciation).
	protected double getDistance(Gene[] a, Gene[] b) {
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
		return (c1*numExcess)/maxLength + (c2*numDisjoint)/maxLength + (c3*weightDiff);
	}

	protected ArrayList<Agent> Selection(ArrayList<Agent> agents) {
		ArrayList<Agent> selectedAgents = new ArrayList<Agent>();
		double last_best = best_fitness;
		double last_average = average_fitness;
		average_fitness = 0;
		for (Agent agent : agents) {
			double fitness = agent.getFitness();
			average_fitness += fitness;
			// This number is completely arbitrary, depends on fitness function
			if (fitness * getRandom() > last_average) {
				selectedAgents.add(agent);
			}
			if (agent.getFitness() > best_fitness) {
				best_fitness = agent.getFitness();
			} else if (agent.getFitness() < worst_fitness) {
				worst_fitness = agent.getFitness();
			}
		}
		average_fitness = average_fitness / agents.size();
		// Keep track of the number of generations without improvement.
		if (last_best >= best_fitness) {
			no_improvement_count++;
		} else {
			no_improvement_count--;
		}
		return selectedAgents;
	}

	protected ArrayList<Gene[]> GenerateChildren(ArrayList<Agent> selectedAgents) {
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();

		while (selectedAgents.size() > 1) {
			Agent mother = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(mother);
			Agent father = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(father);
			// If mother and father are the same, just mutate.
			if (mother.getStringRep().equals(father.getStringRep())) {
				children.add(mutate(mother.getStringRep()));
			} else {
				children.add(crossover(father, mother));
			}
		}

		ArrayList<Gene[]> mutatedChildren = new ArrayList<Gene[]>();
		// Put every child through mutation process
		for (Gene[] child : children) {
			mutatedChildren.add(mutate(child));
		}

		return mutatedChildren;
	}

	// Method to create offspring from 2 given parents.
	protected ArrayList<Gene[]> CreateOffspring(Agent mother, Agent father) {
		newGenes = new ArrayList<Gene>();
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();

		children.add(crossover(mother, father));
		if (getRandom() < twin_chance) {
			children.add(crossover(mother, father));
		}
		for (int i = 0; i < children.size(); i++) {
			children.set(i, mutate(children.get(i)));
		}
		return children;
	}

	protected Gene[] crossover(Agent mother, Agent father) {
		if (mother.getFitness() > father.getFitness()) {
			return crossover(mother.getStringRep(), father.getStringRep());
		} else {
			return crossover(father.getStringRep(), mother.getStringRep());
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
				if (getRandom() < 0.5) {
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

		// TODO make these mutation chances not magical numbers!
		if (getRandom() < 0.7) {
			// Add connection
			if (getRandom() < 0.5) {
				int left = 0;
				int right = 0;
				boolean connected = true;
				int count = 0;
				// Put a good effort into finding two unconnected nodes in the network (it may be
				// impossible).
				while (connected && count < max * max) {
					count++;
					left = historicalMarkersList.get(
							(int) Math.floor(getRandom()*historicalMarkersList.size()));
					right = historicalMarkersList.get(
							(int) Math.floor(getRandom()*historicalMarkersList.size()));
					//Check if the connected left <-> is already within a gene
					// ASSUMES EDGES ARE BIDIRECTIONAL. IS THIS RIGHT??????
					IntPair newPair = new IntPair(left, right);
					//if (!edges.contains(newPair)) {
						connected = false;
					//}
				}
				// If this mutated gene has already been created this gen, don't create another
				Gene newGene = new Gene(next_marker, child[left].in, child[right].in, 4.0, 1);
				for (Gene gene : newGenes) {
					if (newGene.equals(gene)) {
						newGene = gene;
					}
				}
				mutatedChild.add(newGene);
				if (!newGenes.contains(newGene)) {
					next_marker++;
				}
			}

			// Add a new node between two old connections
			if (getRandom() < 0.5) {
				// Choose a gene to split: (ASSUMED IT DOESN'T MATTER IF ALREADY AN EDGE BETWEEN)
				Gene toMutate = mutatedChild.get(
						(int) Math.floor(getRandom() * mutatedChild.size()));
				mutatedChild.remove(toMutate);
				Gene newLeftGene = new Gene(next_marker, toMutate.in, max+1, 4.0, 1);
				for (Gene gene : newGenes) {
					if (newLeftGene.equals(gene)) {
						newLeftGene = gene;
					}
				}
				if (!newGenes.contains(newLeftGene)) {
					next_marker++;
				}
				mutatedChild.add(newLeftGene);
				Gene newRightGene = new Gene(next_marker, max+1, toMutate.out, 4.0, 1);
				for (Gene gene : newGenes) {
					if (newRightGene.equals(gene)) {
						newRightGene = gene;
					}
				}
				if (!newGenes.contains(newRightGene)) {
					next_marker++;
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
			if (getRandom() < 0.3) {
				if (getRandom() < 0.5) {
					gene.weight += getRandom();
				} else {
					gene.weight = Math.abs(gene.weight - getRandom());
				}
			}
		}
		return child;
	}

	// Packing social disaster - all elite individuals randomised except 1
	protected ArrayList<Gene[]> SocialDisasterPacking(ArrayList<Agent> agents) {
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();

		boolean elite_agent_in = false;
		for (Agent agent : agents) {
			if (agent.getFitness() == best_fitness) {
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
	protected ArrayList<Gene[]> SocialDisasterJudgement(ArrayList<Agent> agents) {
		ArrayList<Gene[]> children = new ArrayList<Gene[]>();


		boolean elite_agent_in = false;
		for (Agent agent : agents) {
			if (agent.getFitness() == best_fitness && !elite_agent_in) {
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
}