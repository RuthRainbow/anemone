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
	public ArrayList<String> CreateOffspring(Agent mother, Agent father) {
		ArrayList<String> children = new ArrayList<String>();
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
	public String crossover(String mother, String father) {
		return SinglePointCrossover(mother, father);
	}

	// Crossover by simply picking the first half from the mother and second half from father.
	public String SinglePointCrossover(String mother, String father) {
		int crossover = (int) Math.floor(mother.length()/2);
		StringBuilder child_builder = new StringBuilder();
		child_builder.append(mother.substring(0, crossover));
		child_builder.append(father.substring(crossover));
		return child_builder.toString();
	}

	// Crossover where each gene is taken at random from either mother or father.
	public String UniformCrossover(String mother, String father) {
		StringBuilder child_builder = new StringBuilder();
		char[] mother_arr = mother.toCharArray();
		char[] father_arr = father.toCharArray();
		for (int i = 0; i < mother.length(); i++) {
			double crossover_chance = getRandom();
			if (crossover_chance < 0.5) {
				child_builder.append(mother_arr[i]);
			} else {
				child_builder.append(father_arr[i]);
			}
		}
		return child_builder.toString();
	}

	// Mutate a single gene in the child by replacing with a '!' character
	public String mutate(String child) {
		int mutationPoint = (int) Math.floor(getRandom() * child.length());
		char[] child_arr = child.toCharArray();
		child_arr[mutationPoint] = '!';
		StringBuilder result = new StringBuilder();
		for (char c : child_arr) {
			result.append(c);
		}
		return result.toString();
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
	protected ArrayList<String> BreedPopulation(ArrayList<Agent> agents) {
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
	protected ArrayList<String> SocialDisasterPacking(ArrayList<Agent> agents) {
		ArrayList<String> children = new ArrayList<String>();
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
	protected ArrayList<String> SocialDisasterJudgement(ArrayList<Agent> agents) {
		ArrayList<String> children = new ArrayList<String>();
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

	protected ArrayList<String> GenerateChildren(ArrayList<Agent> selectedAgents) {
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<String> children = new ArrayList<String>();
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
		for (String child : children) {
			if (getRandom() < 0.05) {
				child = mutate(child);
			}
		}

		return children;
	}

	// Return the string representation of a new agent.
	protected String RandomlyGenerate() {
		throw new NotImplementedException();
	}

	public class NotImplementedException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public NotImplementedException(){}
	}
}