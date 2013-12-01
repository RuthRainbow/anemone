package group7.anemone.MNetwork;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The MSimulation class represents the state of one neural network
 * and provides methods for integrating (stepping) its simulation.
 */
public class MSimulation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6811313613723305104L;

	/* Network configuration. */
	MSimulationConfig config;

	/* Simulation state variables. */
	private int time;

	/* Neurons and Synapses. */
	private MNetwork network;

	private ArrayList<ArrayList<MSynapse>> events;

	/**
	 * The constructor takes a network and simulation parameters to
	 * initialise data structures that are used for simulation.
	 *
	 * @param	network 	the neural network to be simulated
	 * @oaram 	config 		the parameters for the simulation
	 */
	public MSimulation(MNetwork network, MSimulationConfig config) {
		/* Initialise the simulation configuration. */
		this.config = new MSimulationConfig();

		/* Get a reference to the network. */
		this.network = network;

		/* Copy over the configuration. */
		this.config= config;

		initialise();
	}

	/**
	 * Performs a 1ms integration of the network.
	 * <p>
	 * TODO:
	 * - Verify that the integration step is in fact 1 ms.
	 */
	public void step() {
		doCurrentEvents();
		updateNeurons();
		clearCurrentEvents();
		addFutureEvents();
		time++;
	}

	private void initialise() {
		int h = this.config.eventHorizon;

		/* Create event table. */
		events = new ArrayList<ArrayList<MSynapse>>();
		for (int i=0; i<h; i++) {
			events.add(new ArrayList<MSynapse>());
		}

		/* Initialise state variables. */
		time = 0;
	}

	private void updateNeurons() {
		for (MNeuron n : network.getNeurons()) {
			n.update();
		}
	}

	private void doCurrentEvents() {
		int h = config.eventHorizon;
		MNeuron n;

		for (MSynapse s : events.get(time % h)) {
			n = s.getPostNeuron();
			n.addCurrent(s.getWeight());
		}
	}

	private void clearCurrentEvents() {
		int h = config.eventHorizon;
		
		events.get(time % h).clear();
	}

	private void addFutureEvents() {
		for (MNeuron n : network.getNeurons()) {
			if (n.isFiring()) {
				/* Add this neuron's postsynapses to the event table. */
				queueNeuronSynapses(n);
			}
		}
	}

	private void queueNeuronSynapses(MNeuron n) {
		int h = config.eventHorizon;
		int delay;

		for (MSynapse s : n.getPostSynapses()) {
			delay = s.getDelay();
			events.get((time + delay) % h).add(s);
		}
	}
	
	public MNetwork getNetwork(){
		return network;
	}
}