package group7.anemone.Genetics;

import group7.anemone.Agent;
import group7.anemone.MNetwork.MFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class God implements Serializable{
	private static final long serialVersionUID = 619717007643693268L;

	/** Start of possible graphical vars **/
	// Mutation chances:
	public double structuralMutationChance = 0.9f;
	public double addConnectionChance = 0.8f;
	public double addNodeChance = 0.8f;
	public double weightMutationChance = 0.8f;
	// (chance of decrease is 1 - the chance of increase)
	public double weightIncreaseChance = 0.5f;

	// Crossover chances:
	public double twinChance = 0.05f;
	public double matchedGeneChance = 0.5f;

	public double offspringProportion = 0.3f; // ALSO COMPLETELY ARBITRARY
	// Parameters for use in difference calculation (can be tweaked).
	public double c1 = 0.3f; //weighting of excess genes
	public double c2 = 0.5f; //weighting of disjoint genes
	public double c3 = 0.5f; //weighting of weight differences
	// Threshold for max distance between species member and representative.
	// INCREASE THIS IF YOU THINK THERE ARE TOO MANY SPECIES!
	public double compatibilityThreshold = 0.4;
	public double minReproduced = 5;
	/** End of possible graphical vars **/

	private double bestFitness;
	private double worstFitness;
	private double averageFitness;
	//private int noImprovementCount = 0;

	// ************* THIS DEPENDS ON MINIMAL NETWORK ****************
	private int nextMarker = 0;
	private ArrayList<Gene> newGenes;

	// The ordered list of all species, with each represented by a member from the
	// previous generation.
	private ArrayList<Species> species;
	// The distances between all genes:
	private ConcurrentHashMap<Pair<AgentFitness>, Double> distances;

	public God() {
		this.species = new ArrayList<Species>();
		this.distances = new ConcurrentHashMap<God.Pair<AgentFitness>, Double>();
	}

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}

	// Method to breed the entire population without species.
	protected HashMap<Genome, Integer> BreedPopulation(ArrayList<Agent> agents) {
		newGenes = new ArrayList<Gene>();
		ArrayList<AgentFitness> selectedAgents = Selection(agents);
		System.out.println("selecting " + selectedAgents.size());
		ArrayList<Genome> children = GenerateChildren(selectedAgents);
		HashMap<Genome, Integer> childrenSpecies = new HashMap<Genome, Integer>();
		for (Genome child : children) {
			childrenSpecies.put(child, 0);
		}
		return childrenSpecies;
	}

	public ArrayList<Genome> BreedWithSpecies(ArrayList<Agent> agents, boolean fitnessOnly) {
		newGenes = new ArrayList<Gene>();
		
		if (species.size() == 0) {
			species.add(new Species(new AgentFitness(agents.get(0)),0));
		}
		
		shareFitnesses();
		ArrayList<Genome> children = new ArrayList<Genome>();
		// Breed each species
		for (Species specie : species) {
			ArrayList<Genome> speciesChildren =
					breedSpecies(specie, agents.size(), fitnessOnly);
			children.addAll(speciesChildren);
		}
		// Pre calculate the distances of new children so this is faster next round.
		Runnable r = new CalcDistance(children);
		Thread thread = new Thread(r);
		thread.run();
		return children;
	}

	private ArrayList<Genome> breedSpecies(Species specie, int popSize, boolean fitnessOnly) {
		ArrayList<Genome> children = new ArrayList<Genome>();
		if (specie.members.size() < 2) {
			for (AgentFitness agent : specie.members) {
				children.add(agent.stringRep);
				children.add(new Genome(
								mutate(agent.stringRep).getGene(),
								agent.stringRep.getNodes(),
								agent.stringRep.getSpeciesId(),
								agent.stringRep,
								agent.stringRep));
			}
			return children;
		}
		double summedFitness = 0;
		for (AgentFitness agent : specie.members) {
			summedFitness += agent.fitness;
			//System.out.println("this agent's fitness is " + agent.fitness);
		}
		int numOffspring = Math.max(
				(int) minReproduced, (int) Math.ceil(summedFitness * offspringProportion));
		//int numOffspring = 2;
		System.out.println("Generating " + numOffspring + " children for species " + specie.id + " summed fitness is " + summedFitness);
		// Breed the top n! (Members is presorted :))
		int i = 0;
		while (children.size() < specie.members.size()/2 && children.size() < numOffspring) {
			AgentFitness mother = specie.members.get(i);
			AgentFitness father = specie.members.get(i+1);
			children.addAll(CreateOffspring(mother, father));
			if (fitnessOnly) {
				children.add(mother.stringRep);
				children.add(mother.stringRep);
			}
			i += 2;
		}
		// If not enough children, repeat some random offspring:
		i = 0;
		while (children.size() < numOffspring) {
			children.add(children.get(i % children.size()));
			i += 1;
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
					fitnessTotal += member.fitness;
				}
				// Check for 0 to avoid NaNs
				agent.fitness = fitnessTotal == 0 ? 0 : (agent.fitness / Math.abs(fitnessTotal));
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
	// Only calculate if not previously stored.
	protected double getDistance(AgentFitness thisAgent, AgentFitness speciesRep) {
		if (distances.contains(thisAgent)) {
			return distances.get(thisAgent);
		} else {
			return calcDistance(thisAgent, speciesRep);
		}
	}

	// Compute distance between thisAgent and speciesRep (see NEAT specification).
	protected double calcDistance(AgentFitness thisAgent, AgentFitness speciesRep) {
		Pair<AgentFitness> agentPair = new Pair<AgentFitness>(thisAgent, speciesRep);

		Genome a = thisAgent.stringRep;
		Genome b = speciesRep.stringRep;
		int numExcess = Math.abs(a.getLength() - b.getLength());
		int numDisjoint = 0;
		double weightDiff = 0.0;
		int minLength = Math.min(a.getLength(), b.getLength());
		int maxLength = Math.max(a.getLength(), b.getLength());
		for (int i = 0; i < minLength; i++) {
			if (a.getXthHistoricalMarker(i) != b.getXthHistoricalMarker(i)) {
				numDisjoint++;
			} else {
				weightDiff += Math.abs(a.getXthWeight(i) - b.getXthWeight(i));
			}
		}
		if (maxLength == 0) maxLength = 1; // Avoid divide by zero.
		double distance = (c1*numExcess)/maxLength + (c2*numDisjoint)/maxLength + (c3*weightDiff);
		// Save this distance so we don't need to recalculate:
		distances.put(agentPair, distance);
		return distance;
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

	protected ArrayList<Genome> GenerateChildren(
			ArrayList<AgentFitness> selectedAgents) {
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<Genome> children = new ArrayList<Genome>();

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

		ArrayList<Genome> mutatedChildren = new ArrayList<Genome>();
		// Put every child through mutation process
		for (Genome child : children) {
			mutatedChildren.add(mutate(child));
		}

		return mutatedChildren;
	}

	protected ArrayList<Genome> createOffspring(Agent mother, Agent father) {
		AgentFitness motherFitness = new AgentFitness(mother);
		AgentFitness fatherFitness = new AgentFitness(father);
		ArrayList<Genome> children = CreateOffspring(motherFitness, fatherFitness);
		return children;
	}

	// Method to create offspring from 2 given parents.
	protected ArrayList<Genome> CreateOffspring(
			AgentFitness mother, AgentFitness father) {
		newGenes = new ArrayList<Gene>();
		ArrayList<Genome> children = new ArrayList<Genome>();

		children.add(crossover(mother, father));
		if (getRandom() < twinChance) {
			children.add(crossover(mother, father));
		}
		for (int i = 0; i < children.size(); i++) {
			children.set(i, mutate(children.get(i)));
		}
		return children;
	}

	protected Genome crossover(AgentFitness mother, AgentFitness father) {
		if (mother.fitness > father.fitness) {
			return crossover(mother.stringRep, father.stringRep);
		} else {
			return crossover(father.stringRep, mother.stringRep);
		}
	}

	// Method for crossover - return crossover method you want.
	// The mother should always be the parent with the highest fitness.
	// TODO may be a problem if they have equal fitness that one is always dominant
	private Genome crossover(Genome dominant, Genome recessive) {
		List<Gene> child = new ArrayList<Gene>();

		// "Match" genes...
		Map<Gene, Gene> matches = new HashMap<Gene, Gene>();
		int marker = 0;
		for (int i = 0; i < dominant.getLength(); i++) {
			for (int j = marker; j < recessive.getLength(); j++) {
				if (dominant.getXthHistoricalMarker(i) == recessive.getXthHistoricalMarker(j) ) {
					marker = j + 1;
					matches.put(dominant.getXthGene(i), recessive.getXthGene(j));
				}
			}
		}

		// Generate the child
		for (int i = 0; i < dominant.getLength(); i++) {
			Gene gene = dominant.getXthGene(i);
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
		return new Genome(childGene, dominant.getNodes(), -1, dominant, recessive);
	}

	// Possibly mutate a child structurally or by changing edge weights.
	// TODO add parameter mutation.
	private Genome mutate(Genome child) {
		child = structuralMutation(child);
		return weightMutation(child);
	}

	// Mutate a genome structurally
	private Genome structuralMutation(Genome child) {
		List<Gene> edgeList = new ArrayList<Gene>();
		int max = 0;
		for (Gene gene : child.getGene()) {
			// Copy across all genes to new child 
			edgeList.add(gene);
			max = Math.max(gene.getIn().id, max);
			max = Math.max(gene.getOut().id, max);
		}

		List<NeatNode> nodeList = child.getNodes();

		if (getRandom() < structuralMutationChance) {
			// Add a new connection between any two nodes
			if (getRandom() < addConnectionChance) {
				addConnection(nodeList, edgeList);
			}

			// Add a new node in the middle of an old connection/edge.
			if (getRandom() < addNodeChance && edgeList.size() > 0) {
				max = addNodeBetweenEdges(edgeList, max, nodeList);
			}
		}
		Gene[] mutatedGeneArray = new Gene[edgeList.size()];
		for (int i = 0; i < edgeList.size(); i++) {
			mutatedGeneArray[i] = edgeList.get(i);
		}
		return new Genome(
				mutatedGeneArray,
				nodeList,
				child.getSpeciesId(),
				child.getMother(),
				child.getFather());
	}
	
	// Add a connection between two existing nodes
	private void addConnection(List<NeatNode> nodeList, List<Gene> edgeList) {
		// Connect two arbitrary nodes - we don't care if they are already connected.
		// (Similar to growing multiple synapses).
		NeatNode left = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		NeatNode right = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		Gene newGene = new Gene(
				nextMarker, left, right, 30.0, 1);
		// If this mutated gene has already been created this gen, don't create another
		for (Gene gene : newGenes) {
			if (newGene.equals(gene)) {
				newGene = gene;
			}
		}
		if (!newGenes.contains(newGene)) {
			nextMarker++;
			newGenes.add(newGene);
			edgeList.add(newGene);
		}
	}

	// Add a node between two pre-existing edges
	private int addNodeBetweenEdges(List<Gene> edgeList, int max, List<NeatNode> nodeList) {
		// Choose a gene to split: (ASSUMED IT DOESN'T MATTER IF ALREADY AN EDGE BETWEEN)
		Gene toMutate = edgeList.get(
				(int) Math.floor(getRandom() * edgeList.size()));
		edgeList.remove(toMutate);
		// Make a new intermediate node TODO can do this more randomly than default params.
		// Increment max to keep track of max node id.
		max += 1;
		NeatNode newNode = new NeatNode(nextMarker, MFactory.createRSNeuronParams());
		nodeList.add(newNode);
		Gene newLeftGene = new Gene(nextMarker, toMutate.getIn(), newNode, 30.0, 1);
		// If this mutated gene has already been created this gen, don't create another
		for (Gene gene : newGenes) {
			if (newLeftGene.equals(gene)) {
				newLeftGene = gene;
				max -= 1;
			}
		}
		// Only increment the marker if this gene is new.
		if (!newGenes.contains(newLeftGene)) {
			nextMarker++;
			newGenes.add(newLeftGene);
		}
		edgeList.add(newLeftGene);
		// Weight should be the same as the current Gene between this two nodes:
		Gene newRightGene = new Gene(
				nextMarker, newNode, toMutate.getOut(), toMutate.getWeight(), 1);
		for (Gene gene : newGenes) {
			if (newRightGene.equals(gene)) {
				newRightGene = gene;
			}
		}
		if (!newGenes.contains(newRightGene)) {
			nextMarker++;
			newGenes.add(newRightGene);
		}
		edgeList.add(newRightGene);
		return max;
	}

	// Each weight is subject to random mutation.
	private Genome weightMutation(Genome child) {
		for (Gene gene : child.getGene()) {
			if (getRandom() < weightMutationChance) {
				if (getRandom() < weightIncreaseChance) {
					gene.addWeight(getRandom());
				} else {
					gene.addWeight(-1 * getRandom());
				}
			}
		}
		return child;
	}

	// Return the string representation of a new agent.
	protected Gene[] RandomlyGenerate() {
		throw new NotImplementedException();
	}

	public class NotImplementedException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public NotImplementedException(){}
	}

	// Class to hold two objects together so we can map to a distance.
	private class Pair<E> {
		private E first;
		private E second;

		public Pair(E first, E second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Pair<?>)) {
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
		private Genome stringRep;
		private double fitness;

		public AgentFitness(Agent agent) {
			this.stringRep = agent.getStringRep();
			this.fitness = agent.getFitness();
		}
		
		public AgentFitness(Genome genome) {
			this.stringRep = genome;
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
	}
	
	/* Calculate distances whilst the simulation is running. */
	private class CalcDistance implements Runnable { 
		private ArrayList<Genome> agents;

		public CalcDistance(ArrayList<Genome> allChildren) {
			this.agents = allChildren;
		}

		public synchronized void run() {
			// Clear species for a new gen
			for (Species specie : species) {
				specie.clear();
			}

			// Put each agent given for reproduction into a species.
			for (Genome agent : this.agents) {
				boolean foundSpecies = false;
				AgentFitness thisAgent = new AgentFitness(agent);
				for (Species specie : species) {
					AgentFitness rep = specie.rep;
					// TODO IF NEEDED THIS COULD BE THREADED!!!!
					double dist = getDistance(thisAgent, rep);
					
					if (dist < compatibilityThreshold) {
						foundSpecies = true;
						specie.addMember(thisAgent);
						agent.setSpecies(specie.id);
					}
				}
				if (!foundSpecies) {
					int newSpeciesId = species.size() + 1;
					species.add(new Species(thisAgent, newSpeciesId));
					agent.setSpecies(newSpeciesId);
				}
			}
		}

	}
}