package group7.anemone.CPPN;

import group7.anemone.MNetwork.MNeuronParams;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;

/**
 * The CPPNSimulation class represents the state of one CPPN
 * and provides methods for integrating (stepping) its simulation.
 */

public class CPPNSimulation {
	private Queue<CPPNNode> functionQueue = new LinkedList<CPPNNode>();
	
	//The network that will be getting run by this simulation
	private CPPN network;

	public CPPNSimulation(ArrayList<CPPNNode> nodes) {
		/* Get a reference to the network. */
		
		this.network = new CPPN(nodes);
	}
	
	public double query(int one, int two) {
		/**
		 * Query will take two inputs, one and two. These numbers represent the post and pre 
		 * nodes to work out the synaptic connection for. This isn't important to know for this method though.
		 * 
		 * The query class will take these inputs, then pass them into the CPPN and at the end 
		 * will get the weight value for the synapse between two neurons.
		 * 
		 * A node will be added to the functionQueue when one of its pre nodes fires. This will only happen the once.
		 * Each time one of its possibly multiple pre nodes fires however, the result from that will be added to the nodes
		 * current totalInput. This totalInput is what will aggreate its inputs and be used to calculate the final result
		 * from this node, to pass to its own post nodes.
		 */
		//The output value will be the weight for this synapse
		double output = 0;
		
		//The first two nodes in the array are always the inputs, so we can run those first, to get the functionQueue populated to start
		ArrayList<CPPNNode> nodes = network.getNodes();
		
		//For the first input Node
//		for (int x=0; x<nodes.get(0).getPostNodes().size(); x++) {
//			//Add the input one to the first network node
//			nodes.get(0).addNodeInput(one);
//			
//			//Add this total to all of the post nodes
//			nodes.get(0).getPostNodes().get(x).addNodeInput(nodes.get(0).calculate());
//			
//			//Add all of these post nodes to the functionsQueue
//			functionQueue.add(nodes.get(0).getPostNodes().get(x));
//		}
//		
//		//For the second input node
//		for (int x=0; x<nodes.get(1).getPostNodes().size(); x++) {
//			//Add the input two to the second network node
//			nodes.get(1).addNodeInput(two);
//			
//			//Add this total to all of the post nodes
//			nodes.get(1).getPostNodes().get(x).addNodeInput(nodes.get(1).calculate());
//			
//			//Add all of these post nodes to the functionsQueue
//			functionQueue.add(nodes.get(1).getPostNodes().get(x));
//		}
//		
//		//TODO: This could be more efficient, I think. Still trying to come up with exactly how the better method could work.
//		//Run through the network, have to step it at least n-3 times. Where n is the total number of nodes in the network.
//		for (int n=0; n<nodes.size();n++) {
//			//For the current node, get its calculation result:
//			double result = nodes.get(n).calculate();
//			
//			//For each of the post nodes
//			for (int y=0; y<nodes.get(n).getPostNodes().size(); y++) {
//				//Add the result to its total input
//				nodes.get(n).getPostNodes().get(y).addNodeInput(result);
//			}
//		}
		
		nodes.get(0).addNodeInput(one);
		nodes.get(1).addNodeInput(two);
		
		/* Get the input nodes. */
		ArrayList<CPPNNode> inputNodes = new ArrayList<CPPNNode>();
		inputNodes.add(nodes.get(0));
		inputNodes.add(nodes.get(1));
		
		/* Set up BFS node sets, we start with just the input nodes. */
		HashSet<CPPNNode> nSet = new HashSet<CPPNNode>(inputNodes);
		HashSet<CPPNNode> tmpNSet = new HashSet<CPPNNode>();
		HashSet<CPPNNode> expanded;
		
		/* BFS - assumes no cycles in CPPN. */
		while (nSet.size() > 0) {
			tmpNSet.clear();
			
			/* Expand each node we are considering this time. */
			for (CPPNNode n : nSet) {
				double result = n.calculate();
				
				/* Expand the current node and add its input. */
				expanded = new HashSet<CPPNNode>(n.getPostNodes());
				for (CPPNNode nn : expanded) {
					nn.addNodeInput(result);
				}
				
				/* These node willl be expanded next time. */
				tmpNSet.addAll(expanded);
			}
			
			nSet = tmpNSet;
		}
		
		//After this, the output for the network will be the total calulation result for the final node in the arraylist,
		//which is always the output node.
		output = nodes.get(nodes.size()-1).calculate();
		
		return output;
	}
	
	public CPPN getNetwork(){
		return network;
	}
}