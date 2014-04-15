package group7.anemone.CPPN;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a set of CPPN nodes and edges to reveal operations on a CPPN.
 */
public class CPPN {
	ArrayList<CPPNNode> nodes;
	
	/**
	 * Constructs a CPPN object using the given nodes and edges.
	 * 
	 * @param nodes collection of CPPN nodes
	 * @param edges collection of CPPN edges
	 */
	public CPPN(Collection<CPPNNode> nodes) {
		this.nodes = new ArrayList<CPPNNode>(nodes);
	}
	
	/**
	 * Applies an input tuple to the network and returns an output tuple.
	 * 
	 * @param inputs ordered list (tuple) of inputs
	 * @return ordered list of output tuples
	 */
	public ArrayList<Double> apply(List<Double> inputs) {
		/* TODO */
		return new ArrayList<Double>();
	}
	
	/**
	 * Retrieves an ArrayList of the CPPN nodes for this object.
	 * @return 
	 */
	public ArrayList<CPPNNode> getNodes() {
		return new ArrayList<CPPNNode>(nodes);
	}
}
