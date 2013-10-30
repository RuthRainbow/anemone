package group7.anemone;

import java.util.ArrayList;

public class God {

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}
	
	// Method to create offspring from 2 given parents.
	public String CreateOffspring(Agent mother, Agent father) {
		String child = crossover(mother.getStringRep(), father.getStringRep());
		if (getRandom() < 0.05) {
			child = mutate(child);
		}
		return child;
	}
	
	// Crossover by simply picking the first half from the mother and second half from father.
	public String crossover(String mother, String father) {
		int crossover = (int) Math.floor(mother.length()/2);
		StringBuilder child_builder = new StringBuilder();
		child_builder.append(mother.substring(0, crossover));
		child_builder.append(father.substring(crossover));
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
		for (Agent agent : agents) {
			double fitness = agent.getFitness();
			// This number is completely arbitrary, depends on fitness function
			if (fitness * getRandom() > 10) {
				selectedAgents.add(agent);
			}
		}
		return selectedAgents;
	}

	// Method to breed the entire population
	protected ArrayList<String> BreedPopulation(ArrayList<Agent> agents) {
		// Selection
		ArrayList<Agent> selectedAgents = Selection(agents);
		
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<String> children = new ArrayList<String>();
		while (selectedAgents.size() > 1) {
			Agent mother = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(mother);
			Agent father = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(father);
			// Will need extra things in here where mother and father have converged.
			children.add(crossover(mother.getStringRep(), father.getStringRep()));
		}
		
		// Random mutation
		for (String child : children) {
			if (getRandom() < 0.05) {
				child = mutate(child);
			}
		}
		
		return children;
	}

}