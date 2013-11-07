package group7.anemone;

import java.util.ArrayList;

/**
 * The MNeuron class represents one Izhikevich neuron and its current state.
 * The class provides methods for integrating its simulation as well as
 * applying voltaic input (perhaps via a preneuron action potential).
 * <p>
 * TODO:
 * - Implement methods for accessing state information (eg, voltage).
 */
public class MNeuron {
	/* Neuron parameters (shouldn't change). */
	private MNeuronParams params;

	/* Neuron state (changes). */
	private MNeuronState state;

	/* Neuron ID. */
	private int nid;

	/* Pre and post synapses. */
	private ArrayList<MSynapse> preSynapses, postSynapses;

	MNeuron(MNeuronParams params, MNeuronState state, int nid) {
		/* Copy parameters. */
		this.params.a = params.a;
		this.params.b = params.b;
		this.params.c = params.c;
		this.params.d = params.d;

		/* Copy state. */
		this.state.v = state.v;
		this.state.u = state.u;
		this.state.I = state.I;

		/* Copy neuron id. */
		
	}

	void update() {
		if (isFiring()) {
			state.v = params.c;
			state.u += params.d;
			state.I = 0;
		}

		/* Perform update of Izhikevich model. */
		state.v += 0.5*(0.04*state.v*state.v + 5*state.v + 140 - state.u + state.I);
		state.v += 0.5*(0.04*state.v*state.v + 5*state.v + 140 - state.u + state.I);
		state.u += params.a*(params.b*state.v - state.u);
	}

	void addCurrent(double I) {
		state.I += I;
	}

	boolean isFiring() {
		if (state.v >= 70.0) {
			return true;
		}
		else {
			return false;
		}
	}

	int getID() {
		return nid;
	}

	ArrayList<MSynapse> getPreSynapses() {
		return preSynapses;
	}

	ArrayList<MSynapse> getPostSynapses() {
		return postSynapses;
	}
}