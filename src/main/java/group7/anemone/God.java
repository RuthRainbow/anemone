package group7.anemone;

import java.util.ArrayList;
import java.util.HashMap;

public class God {

	// NEAT uses an absolutely massive mutation chance
	private double mutation_chance = 0.03f;
	private final double twin_chance = 0.05f;

	private double best_fitness = 0;
	private double worst_fitness = 1;
	private int no_improvement_count = 0;

	// ************* THIS DEPENDS ON MINIMAL NETWORK ****************
	private int next_marker = 5;
	private ArrayList<Gene> newGenes;

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}

	// Method to breed the entire population
	protected ArrayList<Gene[]> BreedPopulation(ArrayList<Agent> agents) {
		newGenes = new ArrayList<Gene>();
		ArrayList<Agent> selectedAgents = Selection(agents);
		return GenerateChildren(selectedAgents);
	}

	protected ArrayList<Agent> Selection(ArrayList<Agent> agents) {
		ArrayList<Agent> selectedAgents = new ArrayList<Agent>();
		double last_best = best_fitness;
		for (Agent agent : agents) {
			double fitness = agent.getFitness();
			// This number is completely arbitrary, depends on fitness function
			if (fitness * getRandom() > 0.7) {
				selectedAgents.add(agent);
			}
			if (agent.getFitness() > best_fitness) {
				best_fitness = agent.getFitness();
			} else if (agent.getFitness() < worst_fitness) {
				worst_fitness = agent.getFitness();
			}
		}
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

		// Put every child through mutation process
		for (Gene[] child : children) {
			child = mutate(child);
		}

		return children;
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
		// TODO this is horrid and won't work with mutation
		int length = dominant.length > recessive.length? dominant.length : recessive.length;
		Gene[] child = new Gene[length];
		int childIndex = 0;

		// "Match" genes...
		HashMap<Gene, Gene> matches = new HashMap<Gene, Gene>();
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
					child[childIndex] = gene;
				} else {
					child[childIndex] = matches.get(gene);
				}
			} else { //Else it didn't match, take it from the dominant
				child[childIndex] = gene;
			}
			childIndex++;
		}

		return child;
	}

	private Gene[] mutate(Gene[] child) {
		child = structuralMutation(child);
		return weightMutation(child);
	}

	// Mutate a single gene in the child by replacing with a '!' character
	public Gene[] structuralMutation(Gene[] child) {
		ArrayList<Gene> mutatedChild = new ArrayList<Gene>();
		for (Gene gene:child) {
			mutatedChild.add(gene);
		}

		if (getRandom() < 0.7) {
			// Add connection
			if (getRandom() < 0.5) {
				int left = (int) Math.floor(getRandom() * child.length);
				int right = (int) Math.floor(getRandom() * child.length);
				boolean connected = false;
				for (Gene gene : mutatedChild) {
					//if (gene.in == child[left].in || gene.in == child[right].in)
				}
				// Ensure you are connecting previously unconnected nodes... TODO
				while (child[left].in == child[right].in) {
					right = (int) Math.floor(getRandom() * child.length);
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
			// Add node
			if (getRandom() < 0.5) {
				int left = (int) Math.floor(getRandom() * child.length);
				int right = (int) Math.floor(getRandom() * child.length);
				// Ensure you are connecting two different nodes...
				while (child[left].in == child[right].in) {
					right = (int) Math.floor(getRandom() * child.length);
				}
			}
		}

		//TODO mutate
		return child;
	}

	// Each weight is subject to random mutation.
	public Gene[] weightMutation(Gene[] child) {
		for (Gene gene : child) {
			if (getRandom() < 0.3) {
				if (getRandom() < 0.5) {
					gene.weight += getRandom();
				} else {
					gene.weight -= getRandom();
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
}