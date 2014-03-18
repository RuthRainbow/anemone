package group7.anemone.CPPN;

import java.util.ArrayList;

import group7.anemone.MNetwork.MFactory;
import group7.anemone.MNetwork.MNetwork;
import group7.anemone.MNetwork.MNeuron;
import group7.anemone.MNetwork.MNeuronParams;
import group7.anemone.MNetwork.MNeuronState;
import group7.anemone.MNetwork.MSynapse;

/**
 * A factory that will take in the CPPN's generated by an agents genome,
 * create a brain out of them,
 * and then return the brain once all CPPN's have been input
 * @author seb
 *
 */

public class CPPNFactory {
	
	//These arrayLists will hold the nodes and synapse of an agents brain as it is gradually built up
	private ArrayList<MNeuron> neurons = new ArrayList<MNeuron>();
	private ArrayList<MSynapse> synapse = new ArrayList<MSynapse>();
	
	private MNetwork brain = new MNetwork(neurons, synapse);
	
	//The current layer that is built up and refreshed each layer before being appended to the master list
	private ArrayList<MNeuron> currentNeurons = new ArrayList<MNeuron>();
	private ArrayList<MSynapse> currentSynapse = new ArrayList<MSynapse>();
			
	//Previous layers items need to be kept below so that the apprpriate connections can be made between layers
	private ArrayList<MNeuron> preNeurons = new ArrayList<MNeuron>();
	private ArrayList<MSynapse> preSynapse = new ArrayList<MSynapse>();
	
	//A factory to create initial parameters for the neurons
	MFactory mFactory = new MFactory();
	
	public void inputCPPN(CPPNSimulation buildSynapse, int layerSize) {
		/** 
		 * This class works a little strangely, so please read:
		 * 
		 * Both the current layer and the previous layer must be worked on at the same time.
		 * At any iteration, the current layer will have its neuron instantiated with default parameters and state
		 * Once these current neurons are created, the synaptic links between these neurons and those in the pevious layer can be made
		 * While the synapses are being made, the neurons in the previous layer have their post neuron values updated
		 * at the same time the current neurons have their pre neuron values updated.
		 * 
		 * This means that at the end of querying the CPPN, the current layer of nerons will be unfinished, but the previous layer
		 * will have been completed. So at the end of all of this, the previous layer of neurons are appended to the arraylist
		 * of neurons, and the current neurons are copied over to now be the previous layers for the next CPPN that is input.
		 */
		
		//Reset the currentNeurons and currentSynapse list
		currentNeurons = new ArrayList<MNeuron>();
		currentSynapse = new ArrayList<MSynapse>();
		
		//WTF IS THE NEURON ID? HOW DO WE MAKE IT? AND OTHER QUESTIONS LIKE THAT
		//HOW SHOULD WE DECIDE DELAY? CPPN? CONSTANT?
		//TODO: SOLVE ABOVE QUESTIONS
		int tempID =0;
		int delay = 0;
		
		//For the number of neurons in this layer, create a standard neuron
		for (int x=0; x<layerSize; x++) {
			//Query the simulation passed in, and save the returned parameters
			MNeuronParams tempParam = mFactory.createRSNeuronParams();
			
			//Create a default state for the neuron to exist when created
			MNeuronState tempState = mFactory.createInitialRSNeuronState();
			
			//Create the node, and then pass it into the arraylist
			currentNeurons.add(new MNeuron(tempParam,tempState,tempID));
			
			//Create a synapse for the current neuron to all of the neurons in the previous layer
			for (int y=0; y<preNeurons.size();y++) {
				//TODO: I have a feeling this won't work out....
				MSynapse tempSynapse = new MSynapse(currentNeurons.get(x),preNeurons.get(y),buildSynapse.query(x, y), delay);
				
				//For the current neuron set the preNeuron to point to the neuron at y
				
				//For the previous neuron, set the postNeuron to point to the neuron at x
			}
		}
		//Append the finished neurons to the ararylist of neurons for the agents brain
		neurons.addAll(preNeurons);
		
		//Copy over the current neurons to the previous layer, so that work on the next layer can start
		preNeurons = currentNeurons;
	}
	
	public MNetwork getBrain() {
		MNetwork output = new MNetwork(neurons, synapse);
		
		return output;
	}

}
