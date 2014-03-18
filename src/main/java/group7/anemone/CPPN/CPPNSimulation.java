package group7.anemone.CPPN;

import group7.anemone.MNetwork.MNeuronParams;

import java.util.ArrayList;

/**
 * The CPPNSimulation class represents the state of one CPPN
 * and provides methods for integrating (stepping) its simulation.
 */

public class CPPNSimulation {
	private ArrayList<Integer> functionQueue;
	
	//The network that will be getting run by this simulation
	private CPPN network;

	public CPPNSimulation(ArrayList<CPPNNode> nodes, ArrayList<CPPNEdge> edges) {
		/* Get a reference to the network. */
		
		this.network = new CPPN(nodes, edges);

		initialise();
	}
	
	public double query(int one, int two) {
		/**
		 * Query will take two inputs, one and two. These numbers represent the post and pre 
		 * nodes to work out the synaptic connection for. This isn't important to know for this method though.
		 * 
		 * The query class will take these inputs, then pass them into the CPPN and at the end 
		 * will get the weight value for the synapse between two neurons
		 */
		//The output value will be the weight for this synapse
		double output;
		
		//For each node in the queue,
		for (int x=0; x<functionQueue.size(); x++) {
			//TODO: This loop for actually querying
			//Run the top node in the queue, then pop it out
			
			//Temprarily save the output for the previous run node, so that when the entire queue has been run we have the final output.
		}
		return output;
	}

	private void initialise() {
		/* Create a list that holds the id of any neurons currently firing
		 * These will get worked on in order, FIFO
		 * Each time a node finishes its calculations, it will add any output neurons to the queue
		 * When the queue is empty, the last node to have been evaluates output will be the output for this CPPN
		 *  */
		functionQueue = new ArrayList<Integer>();
	}
	
	public CPPN getNetwork(){
		return network;
	}
}