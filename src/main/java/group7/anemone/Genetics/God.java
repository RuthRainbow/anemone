package group7.anemone.Genetics;

import group7.anemone.Agent;
import group7.anemone.MNetwork.MFactory;
import group7.anemone.MNetwork.MNeuronParams;

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

	// Next historical marker / id for newly created edges / nodes.
	private int nextEdgeMarker;
	private int nextNodeMarker;
	private ArrayList<NeatEdge> newGenes;

	// The ordered list of all species, with each represented by a member from the
	// previous generation.
	private ArrayList<Species> species;
	// The distances between all genes:
	private ConcurrentHashMap<Pair<AgentFitness>, Double> distances;

	private List<Genome> children;

	public God() {
		this.species = new ArrayList<Species>();
		this.distances = new ConcurrentHashMap<God.Pair<AgentFitness>, Double>();
	}

	public God(double compatabilityThreshold) {
		this.setCompatabilityThreshold(compatabilityThreshold);
		this.species = new ArrayList<Species>();
		this.distances = new ConcurrentHashMap<God.Pair<AgentFitness>, Double>();
	}

	// This is inside it's own method to make unittesting easier.
	public double getRandom() {
		return Math.random();
	}

	// Method to breed the entire population.
	public ArrayList<Genome> BreedPopulation(
			ArrayList<Agent> agents, boolean fitnessOnly) {
		newGenes = new ArrayList<NeatEdge>();

		if (species.size() == 0) {
			species.add(new Species(new AgentFitness(agents.get(0)),0));
			nextNodeMarker = agents.get(0).getStringRep().getNodes().size();
			nextEdgeMarker = agents.get(0).getStringRep().getGene().length;
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
		ArrayList<Genome> children = new ArrayList<Genome>();
		// Breed each species
		for (Species specie : species) {
			ArrayList<Genome> speciesChildren =
					breedSpecies(specie, agents.size(), fitnessOnly);
			children.addAll(speciesChildren);
		}

		// Pre calculate the distances of new children so this is faster next round.
		Runnable r = new CalcAllSpecies(children);
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
		Genome genome = thisAgent.stringRep;
		boolean foundSpecies = false;
		for (Species specie : species) {
			AgentFitness rep = specie.rep;
			double dist = getDistance(thisAgent, rep);

			if (dist < getCompatibilityThreshold()) {
				foundSpecies = true;
				specie.addMember(thisAgent);
				genome.setSpecies(specie.id);
				break;
			}
		}
		if (!foundSpecies) {
			int newSpeciesId = species.size();
			species.add(new Species(thisAgent, newSpeciesId));
			genome.setSpecies(newSpeciesId);
		}
	}

	private ArrayList<Genome> breedSpecies(Species specie, int popSize, boolean fitnessOnly) {
		children = Collections.synchronizedList(new ArrayList<Genome>());
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
			return new ArrayList<Genome>(children);
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
		return new ArrayList<Genome>(children);
	}

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
			return 0;
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
		double averageLength = (maxLength + minLength) / 2;
		if (averageLength == 0) averageLength = 1; // Avoid divide by zero.
		double distance = (getc1()*numExcess)/averageLength +
				(getc2()*numDisjoint)/averageLength +
				(getc3()*weightDiff);
		// Save this distance so we don't need to recalculate:
		distances.put(agentPair, distance);
		return distance;
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
		newGenes = new ArrayList<NeatEdge>();
		ArrayList<Genome> children = new ArrayList<Genome>();

		children.add(crossover(mother, father));
		if (getRandom() < getTwinChance()) {
			children.add(crossover(mother, father));
		}
		for (int i = 0; i < children.size(); i++) {
			children.set(i, mutate(children.get(i)));
		}
		return children;
	}

	protected Genome crossover(AgentFitness mother, AgentFitness father) {
		// If an agent has no edges, it is definitely not dominant.
		if (mother.stringRep.getGene().length > 0 && mother.fitness > father.fitness) {
			return crossover(mother.stringRep, father.stringRep);
		} else {
			return crossover(father.stringRep, mother.stringRep);
		}
	}

	// Method for crossover - return crossover method you want.
	// The mother should always be the parent with the highest fitness.
	// TODO may be a problem if they have equal fitness that one is always dominant
	private Genome crossover(Genome dominant, Genome recessive) {
		List<NeatEdge> child = new ArrayList<NeatEdge>();

		// "Match" genes...
		Map<NeatEdge, NeatEdge> matches = new HashMap<NeatEdge, NeatEdge>();
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
			NeatEdge gene = dominant.getXthGene(i);
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

		NeatEdge[] childGene = new NeatEdge[child.size()];
		for (int i = 0; i < child.size(); i++) {
			childGene[i] = child.get(i);
		}

		Set<NeatNode> nodeSet = new HashSet<NeatNode>(dominant.getNodes());
		nodeSet.addAll(recessive.getNodes());
		return new Genome(childGene, new ArrayList<NeatNode>(nodeSet), -1, dominant, recessive);
	}

	// Possibly mutate a child structurally or by changing edge weights.
	private Genome mutate(Genome child) {
		child = structuralMutation(child);
		parameterMutation(child);
		return weightMutation(child);
	}

	// Mutate the parameters of a gene.
	private void parameterMutation(Genome child) {
		if (getRandom() < getParameterMutationChance()) {
			NeatNode toMutate = child.getNodes().get(
					(int) Math.floor(getRandom()*child.getNodes().size()));
			MNeuronParams params = toMutate.getParams();
			if (getRandom() < getParameterIncreaseChance()) {
				mutateParam(params, getRandom());
			} else {
				mutateParam(params, -1 * getRandom());
			}
		}
	}

	// Method to mutate one of a b c d tau am or ap by the given amount
	private void mutateParam(MNeuronParams params, double amount) {
		double random = getRandom();
		if (random < 0.14) params.a += 0.01;
		else if (random < 0.28) params.b += 0.01;
		else if (random < 0.42) params.c += 0.01;
		else if (random < 0.56) params.ap += 1.0;
		else if (random < 0.7) params.am += 1.0;
		else if (random < 0.84) params.tau += 0.001;
		else params.d += 0.01;
	}

	// Mutate a genome structurally
	private Genome structuralMutation(Genome child) {
		List<NeatEdge> edgeList = new ArrayList<NeatEdge>();
		int max = 0;
		for (NeatEdge gene : child.getGene()) {
			// Copy across all genes to new child
			edgeList.add(gene);
			max = Math.max(gene.getIn().id, max);
			max = Math.max(gene.getOut().id, max);
		}

		List<NeatNode> nodeList = child.getNodes();

		if (getRandom() < getStructuralMutationChance()) {
			// Add a new connection between any two nodes
			if (getRandom() < getAddConnectionChance()) {
				addConnection(nodeList, edgeList);
			}

			// Add a new node in the middle of an old connection/edge.
			if (getRandom() < getAddNodeChance() && edgeList.size() > 0) {
				max = addNodeBetweenEdges(edgeList, max, nodeList);
			}
		}
		NeatEdge[] mutatedGeneArray = new NeatEdge[edgeList.size()];
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
	private void addConnection(List<NeatNode> nodeList, List<NeatEdge> edgeList) {
		// Connect two arbitrary nodes - we don't care if they are already connected.
		// (Similar to growing multiple synapses).
		NeatNode left = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		NeatNode right = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		NeatEdge newGene = new NeatEdge(
				nextEdgeMarker, left, right, 30.0, 1);
		// If this mutated gene has already been created this gen, don't create another
		for (NeatEdge gene : newGenes) {
			if (newGene.equals(gene)) {
				newGene = gene;
			}
		}
		if (!newGenes.contains(newGene)) {
			nextEdgeMarker++;
			newGenes.add(newGene);
			edgeList.add(newGene);
		}
	}

	// Add a node between two pre-existing edges
	private int addNodeBetweenEdges(List<NeatEdge> edgeList, int max, List<NeatNode> nodeList) {
		// Choose a gene to split: (ASSUMED IT DOESN'T MATTER IF ALREADY AN EDGE BETWEEN)
		NeatEdge toMutate = edgeList.get(
				(int) Math.floor(getRandom() * edgeList.size()));
		edgeList.remove(toMutate);
		// Make a new intermediate node TODO can do this more randomly than default params.
		// Increment max to keep track of max node id.
		max += 1;
		NeatNode newNode = new NeatNode(nextNodeMarker, MFactory.createRSNeuronParams());
		nodeList.add(newNode);
		nextNodeMarker++;

		NeatEdge newLeftGene = new NeatEdge(nextEdgeMarker, toMutate.getIn(), newNode, 30.0, 1);
		nextEdgeMarker++;
		edgeList.add(newLeftGene);
		// Weight should be the same as the current Gene between this two nodes:
		NeatEdge newRightGene = new NeatEdge(
				nextEdgeMarker, newNode, toMutate.getOut(), toMutate.getWeight(), 1);
		nextEdgeMarker++;
		edgeList.add(newRightGene);
		return max;
	}

	// Each weight is subject to random mutation.
	private Genome weightMutation(Genome child) {
		for (NeatEdge gene : child.getGene()) {
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
	protected NeatEdge[] RandomlyGenerate() {
		throw new NotImplementedException();
	}

	public class NotImplementedException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public NotImplementedException(){}
	}

	// Class to hold two objects together so we can map to a distance.
	private class Pair<E> implements Serializable{
		private static final long serialVersionUID = -546900858588781203L;
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
	private class Species implements Serializable{
		private static final long serialVersionUID = -4988086681147167058L;
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
	private class AgentFitness implements Comparable<AgentFitness>, Serializable {
		private static final long serialVersionUID = 2130549794698082883L;
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

		@Override
		public String toString() {
			return "Genome: " + this.stringRep + " fitness: " + this.fitness;
		}
	}

	/* Sort into species whilst the simulation is running. */
	private class CalcAllSpecies implements Runnable {
		private ArrayList<Genome> agents;

		public CalcAllSpecies(ArrayList<Genome> allChildren) {
			this.agents = allChildren;
		}

		public synchronized void run() {
			// Clear species for a new gen
			for (Species specie : species) {
				specie.clear();
			}
			// Put each agent given for reproduction into a species.
			for (Genome agent : this.agents) {
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
	public abstract double getCompatibilityThreshold();
	public abstract double getSharingThreshold();
	public abstract double getMinReproduced();

	public abstract void setCompatabilityThreshold(double threshold);
}