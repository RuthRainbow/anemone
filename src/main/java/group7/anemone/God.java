package group7.anemone;

import java.util.ArrayList;

public class God {
	// Need an object relating to the environment
	Environment env;

	public God(Environment env) {
		this.env = env;
	}

	// Method to create offspring from 2 given parents.
	protected String CreateOffspring(Agent mother, Agent father) {
		String child = crossover(mother.getStringRep(), father.getStringRep());
		if (Math.random() < 0.05) {
			child = mutate(child);
		}
		
		return child;
	}
	
	// Crossover by simply picking the first half from the mother and second half from father.
	private String crossover(String father, String mother) {
		int crossover = (int) Math.floor(mother.length()/2);
		StringBuilder child_builder = new StringBuilder();
		child_builder.append(mother.substring(0, crossover));
		child_builder.append(father.substring(crossover+1));
		return child_builder.toString();
	}
	
	// Mutate a single gene in the child by replacing with a '!' character
	private String mutate(String child) {
		int mutationPoint = (int) Math.floor(Math.random() * child.length());
		char[] child_arr = child.toCharArray();
		child_arr[mutationPoint] = '!';
		return child_arr.toString();
	}

	// Method to breed the entire population
	protected ArrayList<String> BreedPopulation(ArrayList<Agent> agents) {
		// Selection
		ArrayList<Agent> selectedAgents = new ArrayList<Agent>();
		for (Agent agent : agents) {
			double fitness = agent.getFitness();
			// This number is completely arbitrary, depends on fitness function
			if (fitness * Math.random() > 10) {
				selectedAgents.add(agent);
			}
		}
		
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<String> children = new ArrayList<String>();
		while (selectedAgents.size() > 1) {
			Agent mother = selectedAgents.get((int) (Math.random() * selectedAgents.size()));
			selectedAgents.remove(mother);
			Agent father = selectedAgents.get((int) (Math.random() * selectedAgents.size()));
			selectedAgents.remove(father);
			// Will need extra things in here where mother and father have converged.
			children.add(crossover(mother.getStringRep(), father.getStringRep()));
		}
		
		// Random mutation
		for (String child : children) {
			if (Math.random() < 0.05) {
				child = mutate(child);
			}
		}
		
		return children;
	}

}