package group7.anemone.CPPN;

import java.util.Collection;
import java.util.ArrayList;

/**
 * An abstract class enforcing the basic functionality of a CPPN node.
 */
public class CPPNNode {
	ArrayList<CPPNNode> preNodes, postNodes;
	CPPNFunction function;
	boolean isInputNode;
	boolean isMutatable;
	
	private double totalInputs;
	
	double paraA, paraB, paraC;
	
	/**
	 * Constructs a CPPN node.
	 * 
	 * @param isInputNode does this node receive primary input?
	 * @param isMutatable do we allow this node to be mutated under NEAT?
	 * @param preNodes collection of presynaptic nodes for adjacency list
	 * @param postNodes collection of postsynaptic nodes for adjacency list
	 * @param function function object (note: the object is NOT copied)
	 */
	CPPNNode(boolean isInputNode, boolean isMutatable,
		Collection<CPPNNode> preNodes, Collection<CPPNNode> postNodes,
		CPPNFunction function)
	{
		this.isInputNode = isInputNode;
		this.isMutatable = isMutatable;
		this.preNodes = new ArrayList<CPPNNode>(preNodes);
		this.postNodes = new ArrayList<CPPNNode>(postNodes);
		this.function = function;
		totalInputs=0;
	}
	
	public CPPNNode(boolean isInputNode, boolean isMutatable,
		CPPNFunction function)
	{
		this(isInputNode, isMutatable, null, null, function);
	}
	
	/**
	 * Retrieves the presynaptic nodes in this node's adjacency list.
	 * 
	 * @return ArrayList of presynaptic nodes
	 */
	public ArrayList<CPPNNode> getPreNodes() {
		return preNodes;
	}
	
	/**
	 * Retrieves the postsynaptic nodes in this node's adjacency list.
	 * 
	 * @return ArrayList of postsynaptic nodes
	 */
	public ArrayList<CPPNNode> getPostNodes() {
		return postNodes;
	}
	
	/**
	 * Returns true iff this node receives primary input.
	 * 
	 * @return true if node is an input node, false otherwise
	 */
	public boolean isInputNode() {
		return isInputNode;
	}
	
	/**
	 * Returns true iff this node is allowed to be mutated under NEAT.
	 * 
	 * @return true if node is mutatable, false otherwise
	 */
	public boolean isMutatable() {
		return isMutatable;
	}
	
	/**
	 * Returns a reference to this node's function object.
	 * 
	 * @return function object
	 */
	public CPPNFunction getFunction() {
		return function;
	}
	
	public double calculate() {
		double result =0;
		
		result = function.calculate(totalInputs);
		
		return result;
	}
	
	public void addNodeInput(double input) {
		totalInputs = totalInputs + input;
	}
}
