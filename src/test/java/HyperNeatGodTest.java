
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.Genetics.God;
import group7.anemone.HyperNeatGenetics.Chromosome;
import group7.anemone.HyperNeatGenetics.HyperNeatFishGod;
import group7.anemone.HyperNeatGenetics.HyperNeatGenome;
import group7.anemone.HyperNeatGenetics.HyperNeatGenome.Type;
import group7.anemone.HyperNeatGenetics.HyperNeatNode;
import junit.framework.TestCase;

/**
 * Tests for functionality specific to the HyperNeatGod class.
 */
public class HyperNeatGodTest extends TestCase {
	static HyperNeatFishGod god = new HyperNeatFishGod();

	public static void testSetup() {}
	
	private HyperNeatGenome createSimpleGenome(int marker, Type type, int layer) {
		ArrayList<HyperNeatNode> nodes = new ArrayList<HyperNeatNode>();
		for (int i = 0; i < 2; i++) {
			nodes.add(HyperNeatNode.createNonRandomNode(i));
		}
		ArrayList<GenomeEdge<HyperNeatNode>> edge = new ArrayList<GenomeEdge<HyperNeatNode>>();
		edge.add(new GenomeEdge<HyperNeatNode>(1, nodes.get(0), nodes.get(1), 0.0, 0));
		return new HyperNeatGenome(edge, nodes, marker, type, layer);
	}

	// Create a very simple child - 1 layer, each with 2 nodes with 1 edge between them.
	private Chromosome createParent() {
		ArrayList<HyperNeatGenome> genome = new ArrayList<HyperNeatGenome>();
		genome.add(createSimpleGenome(0, Type.NEURON, 1));
		genome.add(createSimpleGenome(1, Type.SYNAPSE, 1));
		return new Chromosome(genome, -1, null, null);
	}

	// Create a chromosome with 3 layers.
	private Chromosome createLargerChromo() {
		ArrayList<HyperNeatGenome> genome = new ArrayList<HyperNeatGenome>();
		genome.add(createSimpleGenome(0, Type.NEURON, 1));
		genome.add(createSimpleGenome(1, Type.SYNAPSE, 1));
		genome.add(createSimpleGenome(2, Type.NEURON, 2));
		genome.add(createSimpleGenome(3, Type.SYNAPSE, 2));
		genome.add(createSimpleGenome(4, Type.NEURON, 3));
		genome.add(createSimpleGenome(5, Type.SYNAPSE, 3));
		return new Chromosome(genome, -1, null, null);
	}

	private Chromosome createChild() {
		ArrayList<HyperNeatGenome> genome = new ArrayList<HyperNeatGenome>();
		genome.add(createSimpleGenome(0, Type.NEURON, 1));
		genome.add(createSimpleGenome(1, Type.SYNAPSE, 1));
		return new Chromosome(genome, -1, createParent(), createParent());
	}

	public void testIdenticalCalcDistance() {
		God<Chromosome>.AgentFitness testing = god.new AgentFitness(createParent());
		God<Chromosome>.AgentFitness testAgainst = god.new AgentFitness(createParent());
		assertEquals(0.0, god.calcDistance(testing, testAgainst));
	}

	public void testNonIdenticalCalcDistance() {
		God<Chromosome>.AgentFitness testing = god.new AgentFitness(createParent());
		God<Chromosome>.AgentFitness testAgainst = god.new AgentFitness(createLargerChromo());
		god.c1 = 1.0;
		god.c2 = 1.0;
		god.c3 = 1.0;
		god.c4 = 1.0;
		god.c5 = 1.0;
		assertEquals(1.0, god.calcDistance(testing, testAgainst));
	}

	public void testIdenticalCrossover() {
		Chromosome mother = createParent();
		Chromosome father = createParent();
		assertEquals(createChild(), god.crossover(mother, father));
	}

	public void testNonIndenticalCrossover() {
		Chromosome dominant = createParent();
		Chromosome recessive = createLargerChromo();
		ArrayList<HyperNeatGenome> genome = new ArrayList<HyperNeatGenome>();
		genome.add(createSimpleGenome(0, Type.NEURON, 1));
		genome.add(createSimpleGenome(1, Type.SYNAPSE, 1));
		Chromosome child = new Chromosome(genome, -1, createParent(), createLargerChromo());
		assertEquals(child, god.crossover(dominant, recessive));
	}

	public void testNonIdenticalUnmatchedGeneCrossover() {
		Chromosome dominant = createLargerChromo();
		Chromosome recessive = createParent();
		ArrayList<HyperNeatGenome> genome = new ArrayList<HyperNeatGenome>();
		genome.add(createSimpleGenome(0, Type.NEURON, 1));
		genome.add(createSimpleGenome(1, Type.SYNAPSE, 1));
		genome.add(createSimpleGenome(2, Type.NEURON, 2));
		genome.add(createSimpleGenome(3, Type.SYNAPSE, 2));
		genome.add(createSimpleGenome(4, Type.NEURON, 3));
		genome.add(createSimpleGenome(5, Type.SYNAPSE, 3));
		Chromosome child = new Chromosome(genome, -1, createLargerChromo(), createParent());
		assertEquals(child, god.crossover(dominant, recessive));
	}

	public void testNoParamMutation() {
		HyperNeatFishGod spy = Mockito.spy(god);
		spy.parameterMutationChance = 0.5;
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		Chromosome child = createChild();
		ArrayList<HyperNeatGenome> newGenome = new ArrayList<HyperNeatGenome>();
		for (HyperNeatGenome genome : child.getGeneticRep()) {
			newGenome.add(spy.parameterMutation(genome));
		}
		Chromosome mutated = new Chromosome(newGenome, -1, createParent(), createParent());
		assertEquals(child, mutated);
	}

	public void testAddGenomeMutation() {
		HyperNeatFishGod spy = Mockito.spy(god);
		spy.addGenomeChance = 1.0;
		spy.nextGenomeMarker = 2;
		Mockito.when(spy.getRandom()).thenReturn(0.6);
		Mockito.when(spy.getRandom(2)).thenReturn(0);
		Chromosome child = createChild();
		ArrayList<HyperNeatGenome> genomes = createChild().getGeneticRep();

		List<HyperNeatNode> nodes = genomes.get(0).copyNodes();
		HyperNeatGenome newSynapseGenome = new HyperNeatGenome(
				genomes.get(0).getGene(), nodes, 2, Type.NEURON, 1);
		nodes = genomes.get(1).copyNodes();
		HyperNeatGenome newNeuronGenome = new HyperNeatGenome(
				genomes.get(1).getGene(), nodes, 3, Type.SYNAPSE, 1);	
		genomes.add(0, newSynapseGenome);
		genomes.add(1, newNeuronGenome);
		genomes.get(2).incLayerNum();
		genomes.get(3).incLayerNum();

		assertEquals(genomes, spy.addGenome(child, child.getGeneticRep()));
	}
}
