package group7.anemone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MSimulation {
	/* Network configuration. */
	MSimulationConfig config;

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
		events = new ArrayList<MSynapse>()[h];
	}

	/* Perform a 1ms integration of the network. */
	void step() {
		updateNeurons();
	}

	private void updateNeurons() {
		for (MNeuron n : network.getNeurons()) {
			n.update();
		}
	}
}