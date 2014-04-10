package group7.anemone.Genetics;

import group7.anemone.Agent;
import group7.anemone.CPPN.CPPNFunction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public abstract class God implements Serializable{
	private static final long serialVersionUID = 619717007643693268L;

	private double bestFitness;
	private double worstFitness;
	private double averageFitness;
	//private int noImprovementCount = 0;

	private ArrayList<Integer> nextEdgeMarkers;
	private ArrayList<Integer> nextNodeMarkers;
	private int nextGenomeMarker;
	private ArrayList<Gene> newGenes;

	// The ordered list of all species, with each represented by a member from the
	// previous generation.
	private ArrayList<Species> species;
	// The distances between all genes:
	private ConcurrentHashMap<Pair<AgentFitness>, Double> distances;
	
	private List<Chromosome> children;

	public God() {
		this.species = new ArrayList<Species>();
		this.distances = new ConcurrentHashMap<God.Pair<AgentFitness>, Double>();
	}

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}

	// Method to breed the entire population without species.
	protected HashMap<Chromosome, Integer> BreedPopulation(ArrayList<Agent> agents) {
		newGenes = new ArrayList<Gene>();
		ArrayList<AgentFitness> selectedAgents = Selection(agents);
		ArrayList<Chromosome> children = GenerateChildren(selectedAgents);
		HashMap<Chromosome, Integer> childrenSpecies = new HashMap<Chromosome, Integer>();
		for (Chromosome child : children) {
			childrenSpecies.put(child, 0);
		}
		return childrenSpecies;
	}

	public ArrayList<Chromosome> BreedWithSpecies(ArrayList<Agent> agents, boolean fitnessOnly) {
		newGenes = new ArrayList<Gene>();
		
		if (species.size() == 0) {
			species.add(new Species(new AgentFitness(agents.get(0)),0));
			//Calc. next historical markers for every Genome:
			Chromosome chromo = agents.get(0).getChromosome();
			int genomeSize = chromo.getGenomeSize();
			for (int i = 0; i < genomeSize; i++) {
				Genome thisGenome = chromo.getXthGenome(i);
				nextNodeMarkers.add(thisGenome.getNodes().size());
				nextEdgeMarkers.add(thisGenome.getGene().length);
			}
			nextGenomeMarker = genomeSize;
		}
		
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
		
		propagateFitnesses(agents);
		
		shareFitnesses();
		ArrayList<Chromosome> children = new ArrayList<Chromosome>();
		// Breed each species
		for (Species specie : species) {
			ArrayList<Chromosome> speciesChildren =
					breedSpecies(specie, agents.size(), fitnessOnly);
			children.addAll(speciesChildren);
		}

		// Pre calculate the distances of new children so this is faster next round.
		Runnable r = new CalcAllDistances(children);
		Thread thread = new Thread(r);
		thread.start();
		return children;
	}

	// Copy across agent's fitness from simulation to specie members.
	private void propagateFitnesses(ArrayList<Agent> agents) {
		for (Agent agent : agents) {
			boolean speciesFound = false;
			for (Species specie : species) {
				for (AgentFitness member : specie.members) {
					if (member.stringRep.equals(agent.getStringRep())) {
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
		Chromosome chromo = thisAgent.stringRep;
		boolean foundSpecies = false;
		for (Species specie : species) {
			AgentFitness rep = specie.rep;
			double dist = getDistance(thisAgent, rep);
			
			if (dist < getCompatibilityThreshold()) {
				foundSpecies = true;
				specie.addMember(thisAgent);
				chromo.setSpecies(specie.id);
				break;
			}
		}
		if (!foundSpecies) {
			int newSpeciesId = species.size();
			species.add(new Species(thisAgent, newSpeciesId));
			chromo.setSpecies(newSpeciesId);
		}
	}

	private ArrayList<Chromosome> breedSpecies(Species specie, int popSize, boolean fitnessOnly) {
		children = Collections.synchronizedList(new ArrayList<Chromosome>());
		if (specie.members.size() < 2) {
			for (AgentFitness agent : specie.members) {
				children.add(agent.stringRep);
				children.add(new Chromosome(
								mutate(agent.stringRep).getGenome(),
								agent.stringRep.getSpeciesId(),
								agent.stringRep,
								agent.stringRep));
			}
			return new ArrayList<Chromosome>(children);
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
		// Make sure all species enter the while loop, by duplicating if only species member.
		if (specie.members.size() == 1) {
			specie.addMember(specie.members.get(i));
		}
		while (children.size() < specie.members.size()/2 && children.size() < numOffspring) {
			final AgentFitness mother = specie.members.get(i);
			final AgentFitness father = specie.members.get(i+1);
			
			Runnable r = new CreateOffspring(mother, father);
			Thread thread = new Thread(r);
			thread.start();
			
			if (fitnessOnly) {
				children.add(mother.stringRep);
				children.add(father.stringRep);
			}
			i += 2;
		}
		// If not enough children, repeat some random offspring:
		i = 0;
		while (children.size() < numOffspring) {
			children.add(children.get(i % children.size()));
			i += 1;
		}
		return new ArrayList<Chromosome>(children);
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
		if (distance > getCompatibilityThreshold()) {
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

		Chromosome agent = thisAgent.stringRep;
		Chromosome rep = speciesRep.stringRep;
		double intraGenomeDistance = 0.0;
		int minLength = Math.min(agent.getGenomeSize(), rep.getGenomeSize());
		int maxLength = Math.max(agent.getGenomeSize(), rep.getGenomeSize());
		int numDisjoint = 0;
		int numExcess = maxLength - minLength;
		// Loop through each genome in chromosome and total distance of matched ones.
		for (int i = 0; i < minLength; i++) {
			Genome a = agent.getXthGenome(i);
			Genome b = rep.getXthGenome(i);
			if (a.getHistoricalMarker() != b.getHistoricalMarker()) {
				numDisjoint++;
			} else {
				intraGenomeDistance += calcGenomeDistance(a, b);
			}
		}
		double averageLength = (maxLength + minLength) / 2;
		if (averageLength == 0) averageLength = 1; // Avoid divide by zero.
		double totalDistance = intraGenomeDistance +
							   (getc4()*numExcess/averageLength) +
							   (getc5()*numDisjoint/averageLength);
		
		// Save this distance so we don't need to recalculate:
		distances.put(agentPair, totalDistance);
		return totalDistance;
	}
	
	private double calcGenomeDistance(Genome a, Genome b) {
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
		double averageLength = (maxLength + minLength) / 2;
		if (averageLength == 0) averageLength = 1; // Avoid divide by zero.
		return (getc1()*numExcess)/averageLength +
				(getc2()*numDisjoint)/averageLength +
				(getc3()*weightDiff);
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

	protected ArrayList<Chromosome> GenerateChildren(
			ArrayList<AgentFitness> selectedAgents) {
		// Crossover - should select partner randomly (unless we are having genders).
		ArrayList<Chromosome> children = new ArrayList<Chromosome>();

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

		ArrayList<Chromosome> mutatedChildren = new ArrayList<Chromosome>();
		// Put every child through mutation process
		for (Chromosome child : children) {
			mutatedChildren.add(mutate(child));
		}

		return mutatedChildren;
	}

	protected ArrayList<Chromosome> createOffspring(Agent mother, Agent father) {
		AgentFitness motherFitness = new AgentFitness(mother);
		AgentFitness fatherFitness = new AgentFitness(father);
		ArrayList<Chromosome> children = CreateOffspring(motherFitness, fatherFitness);
		return children;
	}

	// Method to create offspring from 2 given parents.
	protected ArrayList<Chromosome> CreateOffspring(
			AgentFitness mother, AgentFitness father) {
		newGenes = new ArrayList<Gene>();
		ArrayList<Chromosome> children = new ArrayList<Chromosome>();

		children.add(crossover(mother, father));
		if (getRandom() < getTwinChance()) {
			children.add(crossover(mother, father));
		}
		for (int i = 0; i < children.size(); i++) {
			children.set(i, mutate(children.get(i)));
		}
		return children;
	}

	protected Chromosome crossover(AgentFitness mother, AgentFitness father) {
		// If an agent has no edges, it is definitely not dominant.
		if (mother.fitness > father.fitness) {
			return crossover(mother.stringRep, father.stringRep);
		} else {
			return crossover(father.stringRep, mother.stringRep);
		}
	}
	
	protected Chromosome crossover(Chromosome dominant, Chromosome recessive) {
		ArrayList<Genome> newGenome = new ArrayList<Genome>();
		
		// Match Genomes by historical marker
		Map<Genome, Genome> matches = new HashMap<Genome, Genome>();
		int marker = 0;
		for (int i = 0; i < dominant.getGenomeSize(); i++) {
			for (int j = marker; j < recessive.getGenomeSize(); j++) {
				if (dominant.getXthGenome(i).getHistoricalMarker() == 
					recessive.getXthGenome(i).getHistoricalMarker() ) {
					marker = j + 1;
					matches.put(dominant.getXthGenome(i), recessive.getXthGenome(j));
				}
			}
		}
		
		// Perform crossover, taking disjoint Genomes from dominant Chromosome
		for (int i = 0; i < dominant.getGenomeSize(); i++) {
			Genome genome = dominant.getXthGenome(i);
			if (matches.containsKey(genome)) {
				// Perform crossover with the matched Genome
				newGenome.add(crossover(genome, matches.get(genome)));
			} else { //Else it didn't match, take it from the dominant
				// TODO may break CPPN structure!!!!!!
				newGenome.add(genome);
			}
		}
		
		return new Chromosome(newGenome, -1, dominant, recessive);
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
				if (getRandom() < getMatchedGeneChance()) {
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
		
		Set<NeatNode> nodeSet = new HashSet<NeatNode>(dominant.getNodes());
		nodeSet.addAll(recessive.getNodes());
		Genome newGenome = new Genome(
				childGene, new ArrayList<NeatNode>(nodeSet), nextGenomeMarker);
		nextGenomeMarker++;
		return newGenome;
	}
	
	private Chromosome mutate(Chromosome child) {
		ArrayList<Genome> mutatedGenomes = new ArrayList<Genome>();
		for (int i = 0; i < child.getGenomeSize(); i++) {
			mutatedGenomes.add(mutate(child.getXthGenome(i), i));
		}
		return new Chromosome(mutatedGenomes,
							  child.getSpeciesId(),
							  child.getMother(),
							  child.getFather());
	}

	// Possibly mutate a child structurally or by changing edge weights.
	private Genome mutate(Genome child, int i) {
		child = structuralMutation(child, i);
		parameterMutation(child);
		return weightMutation(child);
	}

	// Mutate the parameters of a gene.
	private void parameterMutation(Genome child) {
		if (getRandom() < getParameterMutationChance()) {
			NeatNode toMutate = child.getNodes().get(
					(int) Math.floor(getRandom()*child.getNodes().size()));
			
			CPPNFunction func = toMutate.getCPPNFunction();

			if (getRandom() < getParameterIncreaseChance()) {
				mutateParam(func, 1);
			} else {
				mutateParam(func, -1);
			}
		}
	}
	
	// Method to mutate one of a b c d tau am or ap by the given amount
	private void mutateParam(CPPNFunction func, double delta) {
		double random = getRandom();
		double pA, pB, pC;
		
		pA = func.getParamA();
		pB = func.getParamB();
		pC = func.getParamC();
		
		if (random < 0.3) {
			func.setParamA(pA + delta*0.1);
		} else if (random < 0.6) {
			func.setParamB(pB + delta*0.1);
		} else {
			func.setParamC(pC + delta*0.1);
		}
	}

	// Mutate a genome structurally
	private Genome structuralMutation(Genome child, int index) {
		List<Gene> edgeList = new ArrayList<Gene>();
		int max = 0;
		for (Gene gene : child.getGene()) {
			// Copy across all genes to new child 
			edgeList.add(gene);
			max = Math.max(gene.getIn().id, max);
			max = Math.max(gene.getOut().id, max);
		}

		List<NeatNode> nodeList = child.getNodes();

		if (getRandom() < getStructuralMutationChance()) {
			// Add a new connection between any two nodes
			if (getRandom() < getAddConnectionChance()) {
				addConnection(nodeList, edgeList, index);
			}

			// Add a new node in the middle of an old connection/edge.
			if (getRandom() < getAddNodeChance() && edgeList.size() > 0) {
				max = addNodeBetweenEdges(edgeList, max, nodeList, index);
			}
		}
		Gene[] mutatedGeneArray = new Gene[edgeList.size()];
		for (int i = 0; i < edgeList.size(); i++) {
			mutatedGeneArray[i] = edgeList.get(i);
		}
		Genome newGenome = new Genome(mutatedGeneArray, nodeList, nextGenomeMarker);
		nextGenomeMarker++;
		return newGenome;
	}
	
	// Add a connection between two existing nodes
	private void addConnection(List<NeatNode> nodeList, List<Gene> edgeList, int index) {
		// Connect two arbitrary nodes - we don't care if they are already connected.
		// (Similar to growing multiple synapses).
		NeatNode left = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		NeatNode right = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		Gene newGene = new Gene(
				nextEdgeMarkers.get(index), left, right, 30.0, 1);
		// If this mutated gene has already been created this gen, don't create another
		for (Gene gene : newGenes) {
			if (newGene.equals(gene)) {
				newGene = gene;
			}
		}
		if (!newGenes.contains(newGene)) {
			int nextMarker = nextEdgeMarkers.get(index) + 1;
			nextEdgeMarkers.set(index, nextMarker);
			newGenes.add(newGene);
			edgeList.add(newGene);
		}
	}

	// Add a node between two pre-existing edges
	private int addNodeBetweenEdges(
			List<Gene> edgeList, int max, List<NeatNode> nodeList, int index) {
		// Choose a gene to split: (ASSUMED IT DOESN'T MATTER IF ALREADY AN EDGE BETWEEN)
		Gene toMutate = edgeList.get(
				(int) Math.floor(getRandom() * edgeList.size()));
		edgeList.remove(toMutate);
		// Make a new intermediate node TODO can do this more randomly than default params.
		// Increment max to keep track of max node id.
		max += 1;
		NeatNode newNode = NeatNode.createRandomNeatNode(nextNodeMarkers.get(index));
		nodeList.add(newNode);
		int nextMarker = nextNodeMarkers.get(index) + 1;
		nextNodeMarkers.set(index, nextMarker);
		
		Gene newLeftGene = new Gene(
				nextEdgeMarkers.get(index), toMutate.getIn(), newNode, 30.0, 1);
		nextMarker = nextEdgeMarkers.get(index) + 1;
		nextEdgeMarkers.set(index, nextMarker);
		edgeList.add(newLeftGene);
		// Weight should be the same as the current Gene between this two nodes:
		Gene newRightGene = new Gene(
				nextEdgeMarkers.get(index),
				newNode,
				toMutate.getOut(),
				toMutate.getWeight(),
				1);
		nextMarker = nextEdgeMarkers.get(index) + 1;
		nextEdgeMarkers.set(index, nextMarker);
		edgeList.add(newRightGene);
		return max;
	}

	// Each weight is subject to random mutation.
	private Genome weightMutation(Genome child) {
		for (Gene gene : child.getGene()) {
			if (getRandom() < getWeightMutationChance()) {
				if (getRandom() < getWeightIncreaseChance()) {
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
		private Chromosome stringRep;
		private double fitness;

		public AgentFitness(Agent agent) {
			this.stringRep = agent.getChromosome();
			this.fitness = agent.getFitness();
		}
		
		public AgentFitness(Chromosome chromo) {
			this.stringRep = chromo;
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
			return "Genome: " + this.stringRep + " fitness: " + this.fitness;
		}
	}
	
	/* Calculate distances whilst the simulation is running. */
	private class CalcAllDistances implements Runnable { 
		private ArrayList<Chromosome> agents;

		public CalcAllDistances(ArrayList<Chromosome> allChildren) {
			this.agents = allChildren;
		}

		public synchronized void run() {
			// Clear species for a new gen
			for (Species specie : species) {
				specie.clear();
			}
			// Put each agent given for reproduction into a species.
			for (Chromosome agent : this.agents) {
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
	
	private class CreateOffspring implements Runnable {
		private AgentFitness mother;
		private AgentFitness father;
		
		public CreateOffspring(AgentFitness mother, AgentFitness father) {
			this.mother = mother;
			this.father = father;
		}

		public void run() {
			children.addAll(CreateOffspring(mother, father));
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
	public abstract double getc4();
	public abstract double getc5();
	public abstract double getCompatibilityThreshold();
	public abstract double getMinReproduced();
}