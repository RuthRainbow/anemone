
import java.util.ArrayList;

import org.mockito.Mockito;

import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.Genetics.God;
import group7.anemone.NeatGenetics.NeatFishGod;
import group7.anemone.NeatGenetics.NeatGenome;
import group7.anemone.NeatGenetics.NeatNode;
import junit.framework.TestCase;

public class NeatGodTest extends TestCase {
	static NeatFishGod god = new NeatFishGod();

	public static void testSetup() {}

	// Create a very simple child - 2 nodes with 1 edge between them.
	private NeatGenome createParent() {
		ArrayList<NeatNode> nodes = new ArrayList<NeatNode>();
		for (int i = 0; i < 2; i++) {
			nodes.add(NeatNode.createRSNeatNode(i));
		}
		ArrayList<GenomeEdge<NeatNode>> edge = new ArrayList<GenomeEdge<NeatNode>>();
		edge.add(new GenomeEdge<NeatNode>(1, nodes.get(0), nodes.get(1), 0.0, 0));
		return new NeatGenome(edge, nodes, -1, null, null);
	}

	private NeatGenome createLargerGenome() {
		ArrayList<NeatNode> nodes = new ArrayList<NeatNode>();
		for (int i = 0; i < 3; i++) {
			nodes.add(NeatNode.createRSNeatNode(i));
		}
		ArrayList<GenomeEdge<NeatNode>> edge = new ArrayList<GenomeEdge<NeatNode>>();
		edge.add(new GenomeEdge<NeatNode>(1, nodes.get(0), nodes.get(1), 0.0, 0));
		edge.add(new GenomeEdge<NeatNode>(2, nodes.get(1), nodes.get(2), 0.0, 0));
		return new NeatGenome(edge, nodes, -1, null, null);
	}

	private NeatGenome createChild() {
		ArrayList<NeatNode> nodes = new ArrayList<NeatNode>();
		for (int i = 0; i < 2; i++) {
			nodes.add(NeatNode.createRSNeatNode(i));
		}
		ArrayList<GenomeEdge<NeatNode>> edge = new ArrayList<GenomeEdge<NeatNode>>();
		edge.add(new GenomeEdge<NeatNode>(1, nodes.get(0), nodes.get(1), 0.0, 0));
		return new NeatGenome(edge, nodes, -1, createParent(), createParent());
	}

	public void testIdenticalCalcDistance() {
		God<NeatGenome>.AgentFitness testing = god.new AgentFitness(createParent());
		God<NeatGenome>.AgentFitness testAgainst = god.new AgentFitness(createParent());
		assertEquals(0.0, god.calcDistance(testing, testAgainst));
	}

	public void testNonIdenticalCalcDistance() {
		God<NeatGenome>.AgentFitness testing = god.new AgentFitness(createParent());
		God<NeatGenome>.AgentFitness testAgainst = god.new AgentFitness(createLargerGenome());
		god.c1 = 1.0;
		god.c2 = 1.0;
		god.c3 = 1.0;
		assertEquals(1.0, god.calcDistance(testing, testAgainst));
	}

	public void testIdenticalCrossover() {
		NeatGenome mother = createParent();
		NeatGenome father = createParent();
		assertEquals(createChild(), god.crossover(mother, father));
	}

	public void testNonIndenticalCrossover() {
		NeatGenome dominant = createParent();
		NeatGenome recessive = createLargerGenome();
		GenomeEdge<NeatNode> matchedEdge = new GenomeEdge<NeatNode>(
				1, dominant.getNodes().get(0), dominant.getNodes().get(1), 0.0, 0);
		ArrayList<GenomeEdge<NeatNode>> edges = new ArrayList<GenomeEdge<NeatNode>>();
		edges.add(matchedEdge);
		NeatGenome child = new NeatGenome(edges, recessive.getNodes(), -1, createParent(), createLargerGenome());
		assertEquals(child, god.crossover(dominant, recessive));
	}

	public void testNonIdenticalUnmatchedGeneCrossover() {
		NeatGenome dominant = createLargerGenome();
		NeatGenome recessive = createParent();
		GenomeEdge<NeatNode> matchedEdge = new GenomeEdge<NeatNode>(
				1, dominant.getNodes().get(0), dominant.getNodes().get(1), 0.0, 0);
		GenomeEdge<NeatNode> extraEdge = new GenomeEdge<NeatNode>(
				2, dominant.getNodes().get(1), dominant.getNodes().get(2), 0.0, 0);
		ArrayList<GenomeEdge<NeatNode>> edges = new ArrayList<GenomeEdge<NeatNode>>();
		edges.add(matchedEdge);
		edges.add(extraEdge);
		NeatGenome child = new NeatGenome(edges, dominant.getNodes(), -1, createLargerGenome(), createParent());
		assertEquals(child, god.crossover(dominant, recessive));
	}

	public void testNoParamMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.parameterMutationChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		NeatGenome child = createChild();
		assertEquals(child, spy.parameterMutation(child));
	}

	public void testYesIncreaseParamMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.parameterMutationChance = 0.5;
		spy.parameterIncreaseChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		NeatGenome child = createChild();
		NeatGenome mutated = child.clone();
		mutated.getNodes().get(0).getParams().a += 0.01;
		assertEquals(mutated, spy.parameterMutation(child));
	}

	public void testYesDecreaseParamMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.parameterMutationChance = 0.5;
		spy.parameterIncreaseChance = 0.01;
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		NeatGenome child = createChild();
		NeatGenome mutated = child.clone();
		mutated.getNodes().get(0).getParams().a += -0.01;
		assertEquals(mutated, spy.parameterMutation(child));
	}

	public void testNoStructuralMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.structuralMutationChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		NeatGenome child = createChild();
		assertEquals(child, spy.structuralMutation(child));	
	}

	public void testAddConnStructuralMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.structuralMutationChance = 0.7;
		spy.addConnectionChance = 0.7;
		spy.addNodeChance = 0.5;
		spy.setNextEdgeMarker(2);
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		NeatGenome child = createChild();

		@SuppressWarnings("unchecked")
		ArrayList<GenomeEdge<NeatNode>> newEdges = (ArrayList<GenomeEdge<NeatNode>>) child.getGene().clone();
		GenomeEdge<NeatNode> newEdge = new GenomeEdge<NeatNode>(2, child.getXthOut(0), child.getXthOut(0), 30.0, 1);
		newEdges.add(newEdge);
		NeatGenome mutated = new NeatGenome(newEdges, child.getNodes(), -1, createParent(), createParent());
		assertEquals(mutated, spy.structuralMutation(child));
	}

	public void testAddNodeStructuralMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.structuralMutationChance = 0.7;
		spy.addConnectionChance = 0.5;
		spy.addNodeChance = 0.7;
		spy.setNextEdgeMarker(1);
		spy.setNextNodeMarker(2);
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		NeatGenome child = createChild();

		NeatNode newNode = NeatNode.createRSNeatNode(2);
		ArrayList<NeatNode> newNodes = (ArrayList<NeatNode>) child.copyNodes();
		newNodes.add(newNode);

		@SuppressWarnings("unchecked")
		ArrayList<GenomeEdge<NeatNode>> newEdges = (ArrayList<GenomeEdge<NeatNode>>) child.getGene().clone();
		GenomeEdge<NeatNode> toMutate = child.getXthGene(0);
		newEdges.remove(0);
		GenomeEdge<NeatNode> leftEdge = new GenomeEdge<NeatNode>(1, toMutate.getIn(), newNode, 30.0, 1);
		// Right edge should have weight 0 as it is copied from the mutated edge.
		GenomeEdge<NeatNode> rightEdge = new GenomeEdge<NeatNode>(2, newNode, toMutate.getOut(), 0.0, 1);
		newEdges.add(leftEdge);
		newEdges.add(rightEdge);

		NeatGenome mutated = new NeatGenome(newEdges, newNodes, -1, createParent(), createParent());
		assertEquals(mutated, spy.structuralMutation(child));
	}

	public void testNoWeightMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.weightMutationChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		NeatGenome child = createChild();
		assertEquals(child, spy.weightMutation(child));
	}

	public void testYesIncreaseWeightMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.weightMutationChance = 0.5;
		spy.weightIncreaseChance = 0.7;
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		NeatGenome child = createChild();
		NeatGenome mutated = child.clone();
		mutated.getXthGene(0).addWeight(0.6);
		assertEquals(mutated, spy.weightMutation(child));
	}

	public void testYesDecreaseWeightMutation() {
		NeatFishGod spy = Mockito.spy(god);
		spy.weightMutationChance = 0.5;
		spy.weightIncreaseChance = 0.01;
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		NeatGenome child = createChild();
		NeatGenome mutated = child.clone();
		mutated.getXthGene(0).addWeight(-0.6);
		assertEquals(mutated, spy.weightMutation(child));
	}
}
