package group7.anemone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MSimulation {
	/* Network configuration. */
	MSimulationConfig config;

	/* Neurons and Synapses. */
	private MNetwork network;

	/* Synapse startlists and counts. */
	ArrayList<int[]> DS, DC;

	MSimulation(MNetwork network, MSimulationConfig config) {
		/* Get a reference to the network. */
		this.network = network;

		/* Copy over the configuration. */
		this.config.eventHorizon = config.eventHorizon;

		/* Prepare the network for simulation. */
		prepare();
	}

	private void prepare() {
		/* Sort the synapses. */
		Collections.sort(network.getSynapses(), new Comparator<MSynapse>() {
			public int compare(MSynapse s1, MSynapse s2) {
				if (s1.getPreNeuron().getID() < s2.getPreNeuron().getID()) return -1;
				if (s1.getPreNeuron().getID() > s2.getPreNeuron().getID()) return 1;

				if (s1.getDelay() < s2.getDelay()) return -1;
				if (s1.getDelay() < s2.getDelay()) return 1;

				return 0;
			}
		});

		/* Set up DS and DC. */
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