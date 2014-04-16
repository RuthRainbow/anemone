package group7.anemone.NeatGenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.Genetics.God;
import group7.anemone.MNetwork.MNeuronParams;

public abstract class NeatGod extends God<NeatGenome> {
	private static final long serialVersionUID = 2301852261915045927L;
	// Next historical marker / id for newly created edges / nodes.
	private int nextEdgeMarker;
	private int nextNodeMarker;
	private ArrayList<GenomeEdge<NeatNode>> newGenes;
	
	@Override
	protected void initialiseDataStructures() {
		super.initialiseDataStructures();
		resetNewGenes();
	}
	
	protected void resetNewGenes() {
		this.newGenes = new ArrayList<GenomeEdge<NeatNode>>();
	}

	public void setNextEdgeMarker(int i) {
		this.nextEdgeMarker = i;
	}

	public void setNextNodeMarker(int i) {
		this.nextNodeMarker = i;
	}
	
	public void setUpInitialMarkers(NeatGenome first) {
		nextNodeMarker = first.getNodes().size();
		nextEdgeMarker = first.getSize();
	}
	
	protected ArrayList<NeatGenome> breedOneRemaining(ArrayList<AgentFitness> members) {
		for (AgentFitness agent : members) {
			NeatGenome genome = (NeatGenome) agent.geneticRep;
			children.add(genome);
			children.add(new NeatGenome(
					mutate(genome).getGene(),
					genome.getNodes(),
					genome.getSpeciesId(),
					genome,
					genome));
		}
		return new ArrayList<NeatGenome>(children);
	}
	
	// Compute distance between thisAgent and speciesRep (see NEAT specification).
	public double calcDistance(AgentFitness thisAgent, AgentFitness speciesRep) {
		Pair<AgentFitness> agentPair = new Pair<AgentFitness>(thisAgent, speciesRep);

		NeatGenome a = (NeatGenome) thisAgent.geneticRep;
		NeatGenome b = (NeatGenome) speciesRep.geneticRep;
		int numExcess = Math.abs(a.getSize() - b.getSize());
		int numDisjoint = 0;
		double weightDiff = 0.0;
		int minLength = Math.min(a.getSize(), b.getSize());
		int maxLength = Math.max(a.getSize(), b.getSize());
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

	// Method to create offspring from 2 given parents.
	protected ArrayList<NeatGenome> createOffspring(
			AgentFitness mother, AgentFitness father) {
		newGenes = new ArrayList<GenomeEdge<NeatNode>>();
		ArrayList<NeatGenome> children = new ArrayList<NeatGenome>();

		children.add(crossover(mother, father));
		if (getRandom() < getTwinChance()) {
			children.add(crossover(mother, father));
		}
		for (int i = 0; i < children.size(); i++) {
			children.set(i, mutate(children.get(i)));
		}
		return children;
	}

	// Method for crossover - return crossover method you want.
	// The mother should always be the parent with the highest fitness.
	// TODO may be a problem if they have equal fitness that one is always dominant
	public NeatGenome crossover(NeatGenome dominant, NeatGenome recessive) {
		ArrayList<GenomeEdge<NeatNode>> child = new ArrayList<GenomeEdge<NeatNode>>();

		// "Match" genes...
		Map<GenomeEdge<NeatNode>, GenomeEdge<NeatNode>> matches = 
				new HashMap<GenomeEdge<NeatNode>, GenomeEdge<NeatNode>>();
		int marker = 0;
		for (int i = 0; i < dominant.getSize(); i++) {
			for (int j = marker; j < recessive.getSize(); j++) {
				if (dominant.getXthHistoricalMarker(i) == recessive.getXthHistoricalMarker(j)) {
					marker = j + 1;
					matches.put(dominant.getXthGene(i), recessive.getXthGene(j));
				}
			}
		}

		// Generate the child
		for (int i = 0; i < dominant.getSize(); i++) {
			GenomeEdge<NeatNode> gene = dominant.getXthGene(i);
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

		HashMap<Integer, NeatNode> nodeSet = new HashMap<Integer, NeatNode>();
		for (NeatNode node : dominant.getNodes()) {
			nodeSet.put(node.getId(), node);
		}
		for (NeatNode node : recessive.getNodes()) {
			if (!nodeSet.containsKey(node.getId())) nodeSet.put(node.getId(), node);
		}
		return new NeatGenome(child, new ArrayList<NeatNode>(nodeSet.values()), -1, dominant, recessive);
	}

	// Possibly mutate a child structurally or by changing edge weights.
	protected NeatGenome mutate(NeatGenome child) {
		child = structuralMutation(child);
		child = parameterMutation(child);
		return weightMutation(child);
	}

	// Mutate the parameters of a gene.
	public NeatGenome parameterMutation(NeatGenome child) {
		for (NeatNode node : child.getNodes()) {
			if (getRandom() < getParameterMutationChance()) {
				MNeuronParams params = node.getParams();
				if (getRandom() < getParameterIncreaseChance()) {
					mutateParam(params, getRandom());
				} else {
					mutateParam(params, -1 * getRandom());
				}
			}
		}
		return child;
	}

	// Method to mutate one of a b c d tau am or ap by the given amount
	private void mutateParam(MNeuronParams params, double amount) {
		double random = getRandom();
		if (random < 0.14) params.a += amount * 0.1;
		else if (random < 0.28) params.b += amount * 0.1;
		else if (random < 0.42) params.c += amount * 0.1;
		else if (random < 0.56) params.ap += amount * 3;
		else if (random < 0.7) params.am += amount * 3;
		else if (random < 0.84) params.tau += amount * 0.01;
		else params.d += amount * 0.1;
	}

	// Mutate a genome structurally
	public NeatGenome structuralMutation(NeatGenome child) {
		List<GenomeEdge<NeatNode>> edgeList = new ArrayList<GenomeEdge<NeatNode>>();
		for (GenomeEdge<NeatNode> gene : child.getGene()) {
			// Copy across all genes to new child
			edgeList.add(gene);
		}

		List<NeatNode> nodeList = child.getNodes();

		if (getRandom() < getStructuralMutationChance()) {
			// Add a new connection between any two nodes
			if (getRandom() < getAddConnectionChance()) {
				addConnection(nodeList, edgeList);
			}

			// Add a new node in the middle of an old connection/edge.
			if (getRandom() < getAddNodeChance() && edgeList.size() > 0) {
				addNodeBetweenEdges(edgeList, nodeList);
			}
		}

		return new NeatGenome(
				(ArrayList<GenomeEdge<NeatNode>>) edgeList,
				nodeList,
				child.getSpeciesId(),
				child.getMother(),
				child.getFather());
	}

	// Add a connection between two existing nodes
	private synchronized void addConnection(
			List<NeatNode> nodeList, List<GenomeEdge<NeatNode>> edgeList) {
		// Connect two arbitrary nodes - we don't care if they are already connected.
		// (Similar to growing multiple synapses).
		NeatNode left = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		NeatNode right = nodeList.get(
				(int) Math.floor(getRandom()*nodeList.size()));
		GenomeEdge<NeatNode> newGene = new GenomeEdge<NeatNode>(
				nextEdgeMarker, left, right, 30.0, 1);
		// If this mutated gene has already been created this gen, don't create another
		for (GenomeEdge<NeatNode> gene : newGenes) {
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
	private void addNodeBetweenEdges(
			List<GenomeEdge<NeatNode>> edgeList, List<NeatNode> nodeList) {
		// Choose a gene to split: (ASSUMED IT DOESN'T MATTER IF ALREADY AN EDGE BETWEEN)
		GenomeEdge<NeatNode> toMutate = edgeList.get(
				(int) Math.floor(getRandom() * edgeList.size()));
		edgeList.remove(toMutate);
		// Make a new intermediate node TODO can do this more randomly than default params.
		NeatNode newNode = NeatNode.createRSNeatNode(nextNodeMarker);
		nodeList.add(newNode);
		nextNodeMarker++;

		GenomeEdge<NeatNode> newLeftGene = new GenomeEdge<NeatNode>(nextEdgeMarker, toMutate.getIn(), newNode, 30.0, 1);
		nextEdgeMarker++;
		edgeList.add(newLeftGene);
		// Weight should be the same as the current Gene between this two nodes:
		GenomeEdge<NeatNode> newRightGene = new GenomeEdge<NeatNode>(
				nextEdgeMarker, newNode, toMutate.getOut(), toMutate.getWeight(), 1);
		nextEdgeMarker++;
		edgeList.add(newRightGene);
	}

	// Each weight is subject to random mutation.
	public NeatGenome weightMutation(NeatGenome child) {
		for (GenomeEdge<NeatNode> gene : child.getGene()) {
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
