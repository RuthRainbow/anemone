package group7.anemone;

import java.util.ArrayList;

public class MSimulation {
	/* Network configuration. */
	MSimulationConfig config;

	/* Simulation state variables. */
	private int time;

	/* Neurons and Synapses. */
	private MNetwork network;

	private ArrayList<ArrayList<MSynapse>> events;

	MSimulation(MNetwork network, MSimulationConfig config) {
		/* Get a reference to the network. */
		this.network = network;

		/* Copy over the configuration. */
		this.config.eventHorizon = config.eventHorizon;

		initialise();
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

	/* Perform a 1ms integration of the network. */
	void step() {
		doCurrentEvents();
		updateNeurons();
		addFutureEvents();
		time++;
	}

	private void updateNeurons() {
		for (MNeuron n : network.getNeurons()) {
			n.update();
		}
	}

	private void doCurrentEvents() {
		MNeuron n;

		for (MSynapse s : events.get(time)) {
			n = s.getPostNeuron();
			n.addCurrent(s.getWeight());
		}
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
}