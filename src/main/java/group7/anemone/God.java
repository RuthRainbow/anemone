package group7.anemone;

import java.util.ArrayList;

public abstract class God {
	
	// Need an object relating to the environment
	Environment env;

	public void main(String[] args) {
		while(true) {
			Tick();
		}
	}	
	
	// Method to move all agents each tick
	void Tick() {
		env.MoveAll();
	}
	
	// Method to add n foods to the environment randomly.
	abstract void CreateFood(int num_food);
	
	// Method to create offspring from 2 given parents
	abstract void CreateOffspring(Agent mother, Agent father);
	
	// Method to breed the entire population
	abstract void BreedPopulation(ArrayList<Agent> agents);
	

	
}