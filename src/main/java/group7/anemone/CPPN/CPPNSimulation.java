package group7.anemone.CPPN;

import group7.anemone.MNetwork.MNetwork;
import group7.anemone.MNetwork.MNeuron;
import group7.anemone.MNetwork.MSimulationConfig;
import group7.anemone.MNetwork.MSynapse;

import java.util.ArrayList;

/**
 * The CPPNSimulation class represents the state of one CPPN
 * and provides methods for integrating (stepping) its simulation.
 */

public class CPPNSimulation {
	private ArrayList<Integer> functionQueue;
	
	//The network that will be getting run by this simulation
	private CPPN network;

	public CPPNSimulation(CPPN network) {
		/* Get a reference to the network. */
		this.network = network;

		initialise();
	}

	/**
	 * Performs a 1ms integration of the network.
	 * <p>
	 * TODO:
	 * - Verify that the integration step is in fact 1 ms.
	 */
	public double step(int input) {
		double output = 0.0;
		//For the first node in the CPPN, give the input, and run it.
		
		//For each node in the queue,
		for (int x=0; x<functionQueue.size(); x++) {
			//Run the top node in the queue, then pop it out
			
			//Temprarily save the output for the previous run node, so that when the entire queue has been run we have the final output.
		}
		return output;
	}

	private void initialise() {
		/* Create a list that holds the id of any neurons currently firing
		 * These will get worked on in order, FIFO
		 * Each time a node finishes its calculations, it will add any output neurons to the queue
		 * When the queue is empty, the last node to have been evaluateds output will be the output for this CPPN
		 *  */
		functionQueue = new ArrayList<Integer>();
	}
	
	public CPPN getNetwork(){
		return network;
	}
}