package group7.anemone.HyperNeatGenetics;

import group7.anemone.CPPN.CPPNFunction;
import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.Genetics.God;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class HyperNeatGod extends God<Chromosome> {
	private static final long serialVersionUID = 619717007643693268L;

	private ArrayList<Integer> nextEdgeMarkers;
	private ArrayList<Integer> nextNodeMarkers;
	private int nextGenomeMarker;
	
	public int getRandom(int i) {
		Random rand = new Random();
		return rand.nextInt(i);
	}
	
	protected void setUpInitialMarkers(Chromosome first) {
		//Calc. next historical markers for every Genome:
		Chromosome chromo = (Chromosome) first;
		int genomeSize = chromo.getSize();
		for (int i = 0; i < genomeSize; i++) {
			HyperNeatGenome thisGenome = chromo.getXthGenome(i);
			nextNodeMarkers.add(thisGenome.getNodes().size());
			nextEdgeMarkers.add(thisGenome.getGene().length);
		}
		nextGenomeMarker = genomeSize;
	}
	
	protected ArrayList<Chromosome> breedOneRemaining(ArrayList<AgentFitness> members) {
		for (AgentFitness agent : members) {
			Chromosome chromo = (Chromosome) agent.geneticRep;
			children.add(chromo);
			children.add(new Chromosome(
							mutate(chromo).getGeneticRep(),
							agent.geneticRep.getSpeciesId(),
							chromo,
							chromo));
		}
		return new ArrayList<Chromosome>(children);
	}

	// Compute distance between thisAgent and speciesRep (see NEAT specification).
	protected double calcDistance(AgentFitness thisAgent, AgentFitness speciesRep) {
		Pair<AgentFitness> agentPair = new Pair<AgentFitness>(thisAgent, speciesRep);

		Chromosome agent = (Chromosome) thisAgent.geneticRep;
		Chromosome rep = (Chromosome) speciesRep.geneticRep;
		double intraGenomeDistance = 0.0;
		int minLength = Math.min(agent.getSize(), rep.getSize());
		int maxLength = Math.max(agent.getSize(), rep.getSize());
		int numDisjoint = 0;
		int numExcess = maxLength - minLength;
		// Loop through each genome in chromosome and total distance of matched ones.
		for (int i = 0; i < minLength; i++) {
			HyperNeatGenome a = agent.getXthGenome(i);
			HyperNeatGenome b = rep.getXthGenome(i);
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
	
	private double calcGenomeDistance(HyperNeatGenome a, HyperNeatGenome b) {
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

	// Method to create offspring from 2 given parents.
	protected ArrayList<Chromosome> CreateOffspring(
			AgentFitness mother, AgentFitness father) {
		newGenes = new ArrayList<GenomeEdge>();
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
	
	protected Chromosome crossover(Chromosome dominant, Chromosome recessive) {
		ArrayList<HyperNeatGenome> newGenome = new ArrayList<HyperNeatGenome>();
		
		// Match Genomes by historical marker
		Map<HyperNeatGenome, HyperNeatGenome> matches = new HashMap<HyperNeatGenome, HyperNeatGenome>();
		int marker = 0;
		for (int i = 0; i < dominant.getSize(); i++) {
			for (int j = marker; j < recessive.getSize(); j++) {
				if (dominant.getXthGenome(i).getHistoricalMarker() == 
					recessive.getXthGenome(i).getHistoricalMarker() ) {
					marker = j + 1;
					matches.put(dominant.getXthGenome(i), recessive.getXthGenome(j));
				}
			}
		}
		
		// Perform crossover, taking disjoint Genomes from dominant Chromosome
		for (int i = 0; i < dominant.getSize(); i++) {
			HyperNeatGenome genome = dominant.getXthGenome(i);
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
	@SuppressWarnings("unchecked")
	private HyperNeatGenome crossover(HyperNeatGenome dominant, HyperNeatGenome recessive) {
		List<GenomeEdge> child = new ArrayList<GenomeEdge>();

		// "Match" genes...
		Map<GenomeEdge, GenomeEdge> matches = new HashMap<GenomeEdge, GenomeEdge>();
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
			GenomeEdge gene = dominant.getXthGene(i);
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
		
		GenomeEdge[] childGene = new GenomeEdge[child.size()];
		for (int i = 0; i < child.size(); i++) {
			childGene[i] = child.get(i);
		}
		
		Set<HyperNeatNode> nodeSet = new HashSet<HyperNeatNode>(
				(Collection<? extends HyperNeatNode>) dominant.getNodes());
		nodeSet.addAll((Collection<? extends HyperNeatNode>) recessive.getNodes());
		HyperNeatGenome newGenome = new HyperNeatGenome(
				childGene, new ArrayList<HyperNeatNode>(nodeSet), nextGenomeMarker);
		nextGenomeMarker++;
		return newGenome;
	}
	
	protected Chromosome mutate(Chromosome child) {
		ArrayList<HyperNeatGenome> mutatedGenomes = new ArrayList<HyperNeatGenome>();
		for (int i = 0; i < child.getSize(); i++) {
			mutatedGenomes.add(mutate(child.getXthGenome(i), i));
		}
		
		if (getRandom() < getAddGenomeChance()) {
			int index = getRandom(child.getSize());
			HyperNeatGenome toCopy = child.getXthGenome(index);
			// TODO when adding edge CPPN, also add param. CPPN
			@SuppressWarnings("unchecked")
			Collection<HyperNeatNode> nodes = (Collection<HyperNeatNode>) toCopy.getNodes();
			HyperNeatGenome newGenome = new HyperNeatGenome(
					toCopy.getGene(), nodes, nextGenomeMarker);
			nextGenomeMarker++;
			mutatedGenomes.add(index, newGenome);
		}
		
		return new Chromosome(mutatedGenomes,
							  child.getSpeciesId(),
							  (Chromosome) child.getMother(),
							  (Chromosome) child.getFather());
	}

	// Possibly mutate a child structurally or by changing edge weights.
	private HyperNeatGenome mutate(HyperNeatGenome child, int i) {
		child = structuralMutation(child, i);
		parameterMutation(child);
		return weightMutation(child);
	}

	// Mutate the parameters of a gene.
	private void parameterMutation(HyperNeatGenome child) {
		if (getRandom() < getParameterMutationChance()) {
			@SuppressWarnings("unchecked")
			ArrayList<HyperNeatNode> nodes = (ArrayList<HyperNeatNode>) child.getNodes();
			HyperNeatNode toMutate = nodes.get(
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
	private HyperNeatGenome structuralMutation(HyperNeatGenome child, int index) {
		List<GenomeEdge> edgeList = new ArrayList<GenomeEdge>();
		int max = 0;
		for (GenomeEdge gene : child.getGene()) {
			// Copy across all genes to new child 
			edgeList.add(gene);
			max = Math.max(gene.getIn().id, max);
			max = Math.max(gene.getOut().id, max);
		}

		@SuppressWarnings("unchecked")
		List<HyperNeatNode> nodeList = (List<HyperNeatNode>) child.getNodes();

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
		GenomeEdge[] mutatedGeneArray = new GenomeEdge[edgeList.size()];
		for (int i = 0; i < edgeList.size(); i++) {
			mutatedGeneArray[i] = edgeList.get(i);
		}
		HyperNeatGenome newGenome = new HyperNeatGenome(mutatedGeneArray, nodeList, nextGenomeMarker);
		nextGenomeMarker++;
		return newGenome;
	}
	
	// Add a connection between two existing nodes
	private void addConnection(List<HyperNeatNode> nodeList, List<GenomeEdge> edgeList, int index) {
		// Connect two arbitrary nodes - we don't care if they are already connected.
		// (Similar to growing multiple synapses).
		HyperNeatNode left = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		HyperNeatNode right = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		GenomeEdge newGene = new GenomeEdge(
				nextEdgeMarkers.get(index), left, right, 30.0, 1);
		// If this mutated gene has already been created this gen, don't create another
		for (GenomeEdge gene : newGenes) {
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
			List<GenomeEdge> edgeList, int max, List<HyperNeatNode> nodeList, int index) {
		// Choose a gene to split: (ASSUMED IT DOESN'T MATTER IF ALREADY AN EDGE BETWEEN)
		GenomeEdge toMutate = edgeList.get(
				(int) Math.floor(getRandom() * edgeList.size()));
		edgeList.remove(toMutate);
		// Make a new intermediate node TODO can do this more randomly than default params.
		// Increment max to keep track of max node id.
		max += 1;
		HyperNeatNode newNode = HyperNeatNode.createRandomNeatNode(nextNodeMarkers.get(index));
		nodeList.add(newNode);
		int nextMarker = nextNodeMarkers.get(index) + 1;
		nextNodeMarkers.set(index, nextMarker);
		
		GenomeEdge newLeftGene = new GenomeEdge(
				nextEdgeMarkers.get(index), toMutate.getIn(), newNode, 30.0, 1);
		nextMarker = nextEdgeMarkers.get(index) + 1;
		nextEdgeMarkers.set(index, nextMarker);
		edgeList.add(newLeftGene);
		// Weight should be the same as the current Gene between this two nodes:
		GenomeEdge newRightGene = new GenomeEdge(
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
	private HyperNeatGenome weightMutation(HyperNeatGenome child) {
		for (GenomeEdge gene : child.getGene()) {
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

}