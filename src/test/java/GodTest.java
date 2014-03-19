import java.util.ArrayList;

import org.mockito.Mockito;

import group7.anemone.Genetics.FishGod;
import group7.anemone.Genetics.Genome;
import group7.anemone.Genetics.NeatEdge;
import group7.anemone.Genetics.NeatNode;
import junit.framework.TestCase;

public class GodTest extends TestCase {

	static FishGod god = new FishGod();

	public static void testSetup() {}
	
	// Create a very simple child - 2 nodes with 1 edge between them.
	private Genome createChild() {
		ArrayList<NeatNode> nodes = new ArrayList<NeatNode>();
		for (int i = 0; i < 2; i++) {
			nodes.add(NeatNode.createRSNeatNode(i));
		}
	    ArrayList<NeatEdge> edge = new ArrayList<NeatEdge>();
	    edge.add(new NeatEdge(1, nodes.get(0), nodes.get(1), 0.0, 0));
		return new Genome(edge, nodes, 1, null, null);
	}
	
	public void testNoParamMutation() {
		FishGod spy = Mockito.spy(god);
		spy.parameterMutationChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		Genome child = createChild();
		assertEquals(child, spy.parameterMutation(child));
	}
	
	public void testYesIncreaseParamMutation() {
		FishGod spy = Mockito.spy(god);
		spy.parameterMutationChance = 0.5;
		spy.parameterIncreaseChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		Genome child = createChild();
		Genome mutated = child.clone();
		mutated.getNodes().get(0).getParams().a += 0.01;
		assertEquals(mutated, spy.parameterMutation(child));
	}
	
	public void testYesDecreaseParamMutation() {
		FishGod spy = Mockito.spy(god);
		spy.parameterMutationChance = 0.5;
		spy.parameterIncreaseChance = 0.01;
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		Genome child = createChild();
		Genome mutated = child.clone();
		mutated.getNodes().get(0).getParams().a += -0.01;
		assertEquals(mutated, spy.parameterMutation(child));
	}
	
	public void testNoStructuralMutation() {
		FishGod spy = Mockito.spy(god);
		spy.structuralMutationChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		Genome child = createChild();
		assertEquals(child, spy.structuralMutation(child));	
	}
	
	public void testAddConnStructuralMutation() {
		FishGod spy = Mockito.spy(god);
		spy.structuralMutationChance = 0.7;
		spy.addConnectionChance = 0.7;
		spy.addNodeChance = 0.5;
		spy.setNextEdgeMarker(2);
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		Genome child = createChild();
		
		@SuppressWarnings("unchecked")
		ArrayList<NeatEdge> newEdges = (ArrayList<NeatEdge>) child.getGene().clone();
		NeatEdge newEdge = new NeatEdge(2, child.getXthOut(0), child.getXthOut(0), 30.0, 1);
		newEdges.add(newEdge);
		Genome mutated = new Genome(newEdges, child.getNodes(), 1, null, null);
		assertEquals(mutated, spy.structuralMutation(child));
	}
	
	public void testAddNodeStructuralMutation() {
		FishGod spy = Mockito.spy(god);
		spy.structuralMutationChance = 0.7;
		spy.addConnectionChance = 0.5;
		spy.addNodeChance = 0.7;
		spy.setNextEdgeMarker(1);
		spy.setNextNodeMarker(2);
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		Genome child = createChild();

		NeatNode newNode = NeatNode.createRSNeatNode(2);
		@SuppressWarnings("unchecked")
		ArrayList<NeatNode> newNodes = (ArrayList<NeatNode>) child.getNodes().clone();
		newNodes.add(newNode);
		
		@SuppressWarnings("unchecked")
		ArrayList<NeatEdge> newEdges = (ArrayList<NeatEdge>) child.getGene().clone();
		NeatEdge toMutate = child.getXthGene(0);
		newEdges.remove(0);
		NeatEdge leftEdge = new NeatEdge(1, toMutate.getIn(), newNode, 30.0, 1);
		// Right edge should have weight 0 as it is copied from the mutated edge.
		NeatEdge rightEdge = new NeatEdge(2, newNode, toMutate.getOut(), 0.0, 1);
		newEdges.add(leftEdge);
		newEdges.add(rightEdge);
		
		Genome mutated = new Genome(newEdges, newNodes, 1, null, null);
		assertEquals(mutated, spy.structuralMutation(child));
	}
	
	public void testNoWeightMutation() {
		FishGod spy = Mockito.spy(god);
		spy.weightMutationChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		Genome child = createChild();
		assertEquals(child, spy.weightMutation(child));
	}
	
	public void testYesIncreaseWeightMutation() {
		FishGod spy = Mockito.spy(god);
		spy.weightMutationChance = 0.5;
		spy.weightIncreaseChance = 0.7;
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		Genome child = createChild();
		Genome mutated = child.clone();
		mutated.getXthGene(0).addWeight(0.6);
		assertEquals(mutated, spy.weightMutation(child));
	}
	
	public void testYesDecreaseWeightMutation() {
		FishGod spy = Mockito.spy(god);
		spy.weightMutationChance = 0.5;
		spy.weightIncreaseChance = 0.01;
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		Genome child = createChild();
		Genome mutated = child.clone();
		mutated.getXthGene(0).addWeight(-0.6);
		assertEquals(mutated, spy.weightMutation(child));
	}
}
