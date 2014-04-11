package group7.anemone.HyperNeatGenetics;


import group7.anemone.Genetics.Genome;
import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.CPPN.CPPN;
import group7.anemone.CPPN.CPPNNode;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

/*
 * Genome class to hold the Gene array along with a species id.
 */
public class HyperNeatGenome extends Genome<HyperNeatNode> {
	private static final long serialVersionUID = -9023930914349095877L;

	private int historicalMarker;
	
	public HyperNeatGenome(
			List<GenomeEdge<HyperNeatNode>> genome,
			Collection<HyperNeatNode> nodes,
			int historicalMarker) {
		super(genome, nodes);
		this.historicalMarker = historicalMarker;
	}
	
	public CPPN generateCPPN() {
		CPPN cppn;
		ArrayList<CPPNNode> cppnNodes = new ArrayList<CPPNNode>();
		HashMap<Integer, CPPNNode> nodeMap =
			new HashMap<Integer, CPPNNode>();
		
		/* Create CPPN Nodes. */
		for (HyperNeatNode hnn : super.nodes) {
			Integer id = new Integer(hnn.getId());
			CPPNNode cn = new CPPNNode(false, true,
				hnn.cppnFunction);
			
			nodeMap.put(id, cn);
		}
		
		/* Create CPPN Edges. */
		for (GenomeEdge<HyperNeatNode> ge : super.genome) {
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
}
