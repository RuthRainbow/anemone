package group7.anemone.Genetics;

import group7.anemone.Agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public abstract class God<t extends GeneticObject> implements Serializable{
	private static final long serialVersionUID = 4056200256851797548L;

	private double bestFitness;
	private double worstFitness;
	private double averageFitness;
	//private int noImprovementCount = 0;

	// The ordered list of all species, with each represented by a member from the
	// previous generation.
	protected ArrayList<Species> species;
	// The distances between all genes:
	protected ConcurrentHashMap<Pair<AgentFitness>, Double> distances;

	protected List<t> children;

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}

	public God() {
		initialiseDataStructures();
	}

	public God(double compatabilityThreshold) {
		setCompatabilityThreshold(compatabilityThreshold);
		initialiseDataStructures();
	}

	protected void initialiseDataStructures() {
		this.species = new ArrayList<Species>();
		this.distances = new ConcurrentHashMap<Pair<AgentFitness>, Double>();
	}

	// Method to breed the entire population without species.
	protected HashMap<t, Integer> BreedPopulation(ArrayList<Agent> agents) {
		resetNewGenes();
		ArrayList<AgentFitness> selectedAgents = Selection(agents);
		ArrayList<t> children = GenerateChildren(selectedAgents);
		HashMap<t, Integer> childrenSpecies = new HashMap<t, Integer>();
		for (t child : children) {
			childrenSpecies.put(child, 0);
		}
		return childrenSpecies;
	}

	protected abstract void resetNewGenes();

	@SuppressWarnings("unchecked")
	public ArrayList<t> BreedWithSpecies(ArrayList<Agent> agents, boolean fitnessOnly) {
		resetNewGenes();

		if (this.species.size() == 0) {
			species.add(new Species(new AgentFitness(agents.get(0)),0));
			setUpInitialMarkers((t) agents.get(0).getGeneticObject());
		}

		countDownDistances(agents);

		propagateFitnesses(agents);

		shareFitnesses();

		ArrayList<t> children = new ArrayList<t>();
		// Breed each species
		for (Species specie : species) {
			ArrayList<t> speciesChildren =
				breedSpecies(specie, agents.size(), fitnessOnly);
			children.addAll(speciesChildren);
		}

		// Pre calculate the distances of new children so this is faster next round.
		Runnable r = new CalcAllDistances(children);
		Thread thread = new Thread(r);
		thread.start();
		return children;
	}

	protected abstract void setUpInitialMarkers(t first);

	protected void countDownDistances(ArrayList<Agent> agents) {
		CountDownLatch latch = new CountDownLatch(agents.size() * species.size());
		// Set up threads for each distance calculation to speed this up.
		for (Agent agent : agents) {
			if (agent.getSpeciesId() == -1) {
				AgentFitness thisAgent = new AgentFitness(agent);
				for (Species specie : species) {
					AgentFitness speciesRep = specie.rep;
					if (!distances.containsKey(new Pair<AgentFitness>(thisAgent, speciesRep))) {
						Runnable r = new CalcDistance(thisAgent, speciesRep, latch);
						Thread thread = new Thread(r);
						thread.start();
					} else {
						latch.countDown();
					}
				}
			} else {
				for (@SuppressWarnings("unused") Species specie : species) {
					latch.countDown();
				}
			}
		}

		// Wait for all threads to complete:
		try {
			latch.await();
		} catch (InterruptedException e) {
			// Continue; we'll just have to calculate the distances in sequence.
		}
	}

	protected abstract double calcDistance(AgentFitness thisAgent, AgentFitness speciesRep);

	// Copy across agent's fitness from simulation to specie members.
	private void propagateFitnesses(ArrayList<Agent> agents) {
		for (Agent agent : agents) {
			boolean speciesFound = false;
			for (Species specie : species) {
				for (AgentFitness member : specie.members) {
					if (member.geneticRep.equals(agent.getGeneticRep())) {
						member.fitness = agent.getFitness();
						speciesFound = true;
						break;
					}
				}
			}
			// This case could happen if the main species sorter thread was slower than the sim.
			if (!speciesFound) {
				AgentFitness thisAgent = new AgentFitness(agent);
				sortIntoSpecies(thisAgent);
			}
		}
	}

	// Sort given AgentFitness into a species or create a new one.
	private void sortIntoSpecies(AgentFitness thisAgent) {
		GeneticObject geneticObj = thisAgent.geneticRep;
		boolean foundSpecies = false;
		for (Species specie : species) {
			AgentFitness rep = specie.rep;
			double dist = getDistance(thisAgent, rep);

			if (dist < getCompatibilityThreshold()) {
				foundSpecies = true;
				specie.addMember(thisAgent);
				geneticObj.setSpecies(specie.id);
				break;
			}
		}
		if (!foundSpecies) {
			int newSpeciesId = species.size();
			species.add(new Species(thisAgent, newSpeciesId));
			geneticObj.setSpecies(newSpeciesId);
		}
	}


	@SuppressWarnings("unchecked")
	private ArrayList<t> breedSpecies(Species specie, int popSize, boolean fitnessOnly) {
		children = Collections.synchronizedList(new ArrayList<t>());
		if (specie.members.size() < 2) {
			return new ArrayList<t>(breedOneRemaining(specie.members));
		}
		double summedFitness = 0;
		for (AgentFitness agent : specie.members) {
			summedFitness += agent.fitness;
		}
		int numOffspring = Math.max(
				(int) getMinReproduced(),
				(int) Math.ceil(summedFitness * getOffspringProportion()));
		System.out.println("Generating " + numOffspring + " children for species " + specie.id + " summed fitness is " + summedFitness);
		// Breed the top n! (Members is presorted :))
		int i = 0;

		while (children.size() < specie.members.size()/2 && children.size() < numOffspring) {
			final AgentFitness mother = specie.members.get(i);
			final AgentFitness father = specie.members.get(i+1);

			Runnable r = new CreateOffspring(mother, father);
			Thread thread = new Thread(r);
			thread.start();

			if (fitnessOnly) {
				children.add((t) mother.geneticRep);
				children.add((t) father.geneticRep);
			}
			i += 2;
		}
		// If not enough children, repeat some random offspring:
		i = 0;
		while (children.size() < numOffspring) {
			children.add(children.get(i % children.size()));
			i += 1;
		}
		return new ArrayList<t>(children);
	}

	protected abstract ArrayList<t> breedOneRemaining(ArrayList<AgentFitness> members);

	// Share fitnesses over species by updating AgentFitness objects (see NEAT paper)
	protected void shareFitnesses() {
		// For every species...
		for (Species specie : species) {
			// Calculate the average fitness
			double fitnessTotal = 0;
			for (AgentFitness member : specie.members) {
				fitnessTotal += sharingFunction(member.fitness);	
			}
			for (AgentFitness agent : specie.members) {
				// Check for 0 to avoid NaNs
				agent.fitness = fitnessTotal == 0 ? 0 :
					(agent.fitness / Math.abs(fitnessTotal));
			}
		}
	}

	protected int sharingFunction(double distance) {
		if (distance > getSharingThreshold()) {
			return 0; // Seems pointless. Why not only compare with dudes from the same species,
			// if all others will be made 0?!
		} else {
			return 1;
		}
	}

	// Return computability distance between two networks (see NEAT speciation).
	// Only calculate if not previously stored.
	protected double getDistance(AgentFitness thisAgent, AgentFitness speciesRep) {
		if (distances.contains(thisAgent)) {
			return distances.get(thisAgent);
		} else {
			return calcDistance(thisAgent, speciesRep);
		}
	}

	protected ArrayList<AgentFitness> Selection(ArrayList<Agent> agents) {
		ArrayList<AgentFitness> selectedAgents = new ArrayList<AgentFitness>();
		//double last_best = bestFitness;
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
		/*if (last_best >= bestFitness) {
noImprovementCount++;
} else {
noImprovementCount--;
}*/
		return selectedAgents;
	}

	protected ArrayList<t> GenerateChildren(
			ArrayList<AgentFitness> selectedAgents) {
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<t> children = new ArrayList<t>();

		while (selectedAgents.size() > 1) {
			AgentFitness mother = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(mother);
			AgentFitness father = selectedAgents.get((int) (getRandom() * selectedAgents.size()));
			selectedAgents.remove(father);
			children.add(crossover(father, mother));
		}

		ArrayList<t> mutatedChildren = new ArrayList<t>();
		// Put every child through mutation process
		for (t child : children) {
			mutatedChildren.add(mutate(child));
		}

		return mutatedChildren;
	}

	protected ArrayList<t> createOffspring(Agent mother, Agent father) {
		AgentFitness motherFitness = new AgentFitness(mother);
		AgentFitness fatherFitness = new AgentFitness(father);
		resetNewGenes();
		ArrayList<t> children = createOffspring(motherFitness, fatherFitness);
		return children;
	}

	protected abstract ArrayList<t> createOffspring(
			AgentFitness mother, AgentFitness father);

	protected t crossover(AgentFitness mother, AgentFitness father) {
		// If an agent has no edges, it is definitely not dominant.
		@SuppressWarnings("unchecked")
		t motherGen = (t) mother.geneticRep;
		@SuppressWarnings("unchecked")
		t fatherGen = (t) father.geneticRep;
		if (mother.fitness > father.fitness) {
			return crossover(motherGen, fatherGen);
		} else {
			return crossover(fatherGen, motherGen);
		}
	}

	protected abstract t crossover(t dominant, t recessive);

	protected abstract t mutate(t child);

	// Class used to hold an entire species.
	public class Species implements Serializable {
		private static final long serialVersionUID = 4956311681035778159L;
		private ArrayList<AgentFitness> members;
		public AgentFitness rep;
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
	public class AgentFitness implements Comparable<AgentFitness>, Serializable {
		private static final long serialVersionUID = -8299234421823218363L;
		public GeneticObject geneticRep;
		public double fitness;

		public AgentFitness(Agent agent) {
			this.geneticRep = agent.getGeneticObject();
			this.fitness = agent.getFitness();
		}

		public AgentFitness(t geneticObj) {
			this.geneticRep = geneticObj;
			this.fitness = 0;
		}

		public int compareTo(AgentFitness other) {
			if (this.fitness < other.fitness) {
				return 1;
			} else if (this.fitness > other.fitness) {
				return -1;
			} else {
				return 0;
			}
		}

		@Override
		public String toString() {
			return "Genome: " + this.geneticRep + " fitness: " + this.fitness;
		}
	}

	// Class to hold two objects together so we can map to a distance.
	public static class Pair<E> {
		private E first;
		private E second;

		public Pair(E first, E second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof God.Pair<?>)) {
				return false;
			} else {
				// We need pairs to be of the same type to compare.
				@SuppressWarnings("unchecked")
				Pair<E> otherPair = (Pair<E>) other;
				if (this.first == otherPair.first && this.second == otherPair.second ||
						this.first == otherPair.second && this.second == otherPair.first) {
					return true;
				} else {
					return false;
				}
			}
		}

		public String toString() {
			return "(" + first.toString() + ", " + second.toString() + ")";
		}
	}

	private class CreateOffspring implements Runnable {
		private AgentFitness mother;
		private AgentFitness father;

		public CreateOffspring(AgentFitness mother, AgentFitness father) {
			this.mother = mother;
			this.father = father;
		}

		public void run() {
			children.addAll(createOffspring(mother, father));
		}
	}

	/* Sort into species whilst the simulation is running. */
	private class CalcAllDistances implements Runnable {
		private List<t> agents = Collections.synchronizedList(new ArrayList<t>());

		public CalcAllDistances(ArrayList<t> allChildren) {
			this.agents = allChildren;
		}

		public synchronized void run() {
			// Clear species for a new gen
			for (Species specie : species) {
				specie.clear();
			}
			// Put each agent given for reproduction into a species.
			for (t agent : this.agents) {
				AgentFitness thisAgent = new AgentFitness(agent);
				sortIntoSpecies(thisAgent);
			}
		}
	}

	private class CalcDistance implements Runnable {
		private AgentFitness thisAgent;
		private AgentFitness speciesRep;
		private CountDownLatch latch;

		public CalcDistance(AgentFitness thisAgent, AgentFitness speciesRep, CountDownLatch latch) {
			this.thisAgent = thisAgent;
			this.speciesRep = speciesRep;
			this.latch = latch;
		}

		public synchronized void run() {
			calcDistance(thisAgent, speciesRep);
			latch.countDown();
		}
	}

	/* Getter methods for variables that may differ between God types.*/
	public abstract double getStructuralMutationChance();
	public abstract double getAddConnectionChance();
	public abstract double getAddNodeChance();
	public abstract double getWeightMutationChance();
	public abstract double getWeightIncreaseChance();
	public abstract double getParameterMutationChance();
	public abstract double getParameterIncreaseChance();
	public abstract double getTwinChance();
	public abstract double getMatchedGeneChance();
	public abstract double getOffspringProportion();
	public abstract double getc1();
	public abstract double getc2();
	public abstract double getc3();
	public abstract double getCompatibilityThreshold();
	public abstract double getSharingThreshold();
	public abstract double getMinReproduced();

	public abstract void setCompatabilityThreshold(double compatabilityThreshold);
}