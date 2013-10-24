package group7.anemone;

import java.util.ArrayList;

public class God {

	// Need an object relating to the environment
	Environment env;

	public God(Environment env) {
		this.env = env;
	}

	// Method to create offspring from 2 given parents
	protected void CreateOffspring(Agent mother, Agent father) {
		throw new NotImplementedException();
	}

	// Method to breed the entire population
	protected void BreedPopulation(ArrayList<Agent> agents) {
		throw new NotImplementedException();
	}

}