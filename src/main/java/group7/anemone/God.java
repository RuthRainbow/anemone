package group7.anemone;

import java.util.ArrayList;

public class God {

	private double mutation_chance = 0.001f;
	private final double twin_chance = 0.05f;

	private double best_fitness = 0;
	private double worst_fitness = 1;
	private int no_improvement_count = 0;

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}

	// Method to create offspring from 2 given parents.
	public ArrayList<int[][]> CreateOffspring(Agent mother, Agent father) {
		ArrayList<int[][]> children = new ArrayList<int[][]>();
		children.add(crossover(mother.getStringRep(), father.getStringRep()));
		if (getRandom() < mutation_chance) {
			children.add(crossover(mother.getStringRep(), father.getStringRep()));
		}
		for (int i = 0; i < children.size(); i++) {
			if (getRandom() < twin_chance) {
				children.set(i, mutate(children.get(i)));
			}
		}

		return children;
	}

	// Method for crossover - return crossover method you want.
	public int[][] crossover(int[][] mother, int[][] father) {
		return SinglePointCrossover(mother, father);
	}

	// Crossover by simply picking the first half from the mother and second half from father.
	public int[][] SinglePointCrossover(int[][] mother, int[][] father) {
		int crossover = (int) Math.floor(mother.length/2);
		int[][] child = new int[mother.length][mother[0].length];
		// THIS ASSUMES MOTHER AND FATHER'S GENOMES ARE THE SAME LENGTH
		for (int y = 0; y < mother[0].length; y++) {
			for (int x = 0; x < crossover; x++) {
				child[x][y] = mother[x][y];
			}
		}
		for (int y = 0; y < mother[0].length; y++) {
			for (int x = crossover; x < mother.length; x++) {
				child[x][y] = father[x][y];
			}
		}
		return child;
	}

	private void Print(int[][] array) {
		for (int y = 0; y < array[0].length; y++) {
			for (int x = 0; x < array.length; x++) {
				System.out.print(array[x][y] + " ");
			}
			System.out.println();
		}
	}

	// Crossover where each gene is taken at random from either mother or father.
	public int[][] UniformCrossover(int[][] mother, int[][] father) {
		int[][] child = new int[mother.length][mother[0].length];
		for (int y = 0; y < mother[0].length; y++) {
			for (int x = 0; x < mother.length; x++) {
				double crossover_chance = getRandom();
				if (crossover_chance < 0.5) {
					child[x][y] = mother[x][y];
				} else {
					child[x][y] = father[x][y];
				}
			}
		}
		Print(child);
		return child;
	}

	// Mutate a single gene in the child by replacing with a '!' character
	public int[][] mutate(int[][] child) {
		int mutationPoint = (int) Math.floor(getRandom() * child.length*child[0].length);
		child[mutationPoint % child.length][mutationPoint % child[0].length] = 9;
		return child;
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

	// Method to breed the entire population
	protected ArrayList<int[][]> BreedPopulation(ArrayList<Agent> agents) {
		// Selection
		ArrayList<Agent> selectedAgents = Selection(agents);

		// If not enough genetic diversity increase the chance of mutation.
		// If plenty of genetic diversity reset the change of mutation.
		if (best_fitness - worst_fitness < 0.1 || no_improvement_count > 10) {
			mutation_chance *= 2;
		} else if (best_fitness - worst_fitness > 0.3 || no_improvement_count == 0) {
			mutation_chance = 0.001;
		}

		// If no improvement for many generations, start a social disaster.
		if (no_improvement_count > 100) {
			return SocialDisasterPacking(agents);
		} else { // Else generate children normally...
			return GenerateChildren(selectedAgents);
		}
	}

	// Packing social disaster - all elite individuals randomised except 1
	protected ArrayList<int[][]> SocialDisasterPacking(ArrayList<Agent> agents) {
		ArrayList<int[][]> children = new ArrayList<int[][]>();
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
	protected ArrayList<int[][]> SocialDisasterJudgement(ArrayList<Agent> agents) {
		ArrayList<int[][]> children = new ArrayList<int[][]>();
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

	protected ArrayList<int[][]> GenerateChildren(ArrayList<Agent> selectedAgents) {
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<int[][]> children = new ArrayList<int[][]>();
		while (selectedAgents.size() > 1) {
			Agent mother = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(mother);
			Agent father = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(father);
			// If mother and father are the same, randomise the child to give more genetic diversity.
			if (mother == father) {
				children.add(RandomlyGenerate());
			} else {
				children.add(crossover(mother.getStringRep(), father.getStringRep()));
			}
		}

		// Random mutation
		for (int[][] child : children) {
			if (getRandom() < 0.05) {
				child = mutate(child);
			}
		}

		return children;
	}

	// Return the string representation of a new agent.
	protected int[][] RandomlyGenerate() {
		throw new NotImplementedException();
	}

	public class NotImplementedException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public NotImplementedException(){}
	}
}