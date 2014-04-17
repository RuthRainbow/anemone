package group7.anemone.HyperNeatGenetics;

import group7.anemone.Genetics.Genome;
import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.CPPN.CPPN;
import group7.anemone.CPPN.CPPNNode;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;

/*
 * Genome class to hold the Gene array along with a species id.
 */
public class HyperNeatGenome extends Genome<HyperNeatNode> {
	private static final long serialVersionUID = -9023930914349095877L;

	private int historicalMarker;
	
	public enum Type {
		NEURON, SYNAPSE
	};
	
	private Type type;
	private int layer;
	
	public HyperNeatGenome(
			List<GenomeEdge<HyperNeatNode>> genome,
			Collection<HyperNeatNode> nodes,
			int historicalMarker,
			HyperNeatGenome.Type type,
			int layer) {
		super(genome, nodes);
		this.historicalMarker = historicalMarker;
		this.type = type;
		this.layer = layer;
	}
	
	public CPPN generateCPPN() {
		CPPN cppn;
		HashMap<Integer, CPPNNode> nodeMap =
			new HashMap<Integer, CPPNNode>();
		
		/* Create CPPN Nodes. */
		for (HyperNeatNode hnn : this.nodes) {
			Integer id = new Integer(hnn.getId());
			CPPNNode cn = new CPPNNode(false, true,
				hnn.cppnFunction);
			
			nodeMap.put(id, cn);
		}
		
		/* Create CPPN Edges. */
		for (GenomeEdge<HyperNeatNode> ge : this.genome) {
			HyperNeatNode preHnn = ge.getIn();
			HyperNeatNode postHnn = ge.getOut();
			
			CPPNNode preCnn = nodeMap.get(preHnn.getId());
			CPPNNode postCnn = nodeMap.get(postHnn.getId());
			
			/* Add the edge proper. */
			preCnn.getPostNodes().add(postCnn);
			postCnn.getPreNodes().add(preCnn);
		}
		
		/* Create the CPPN. */
		cppn = new CPPN(nodeMap.values());
		
		return cppn;
	}
	
	public int getHistoricalMarker() {
		return this.historicalMarker;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public int getLayerNum() {
		return layer;
	}

	public void incLayerNum() {
		this.layer++;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof HyperNeatGenome)) {
			return false;
		} else {
			HyperNeatGenome other = (HyperNeatGenome) o;
			if (other.historicalMarker != this.historicalMarker) return false;
			if (other.type != this.type) return false;
			if (other.layer != this.layer) return false;
			if (this.genome.size() != other.genome.size()) return false;
			for (int i = 0; i < this.genome.size(); i++) {
				if (!this.genome.get(i).equals(other.getXthGene(i))) return false;
			}
			if (this.nodes.size() != other.getNodesSize()) return false;
			for (int i = 0; i < this.getNodesSize(); i++) {
				if (!this.nodes.get(i).equals(other.nodes.get(i))) return false;
			}
			return true;
		}
	}
	
	public String toString() {
		return "ID: " + this.historicalMarker + " type: " + this.type + " layer: " + this.layer;
	}
}
