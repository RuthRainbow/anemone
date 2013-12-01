package group7.anemone.MNetwork;

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

	public MNeuron(MNeuronParams params, MNeuronState state, int nid) {
		/* Initialise parameters and state. */
		this.params = new MNeuronParams();
		this.state = new MNeuronState();

		/* Initialise pre and post synapse list. */
		this.preSynapses = new ArrayList<MSynapse>();
		this.postSynapses = new ArrayList<MSynapse>();

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
		this.nid = nid;
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

	public void addCurrent(double I) {
		state.I += I;
	}

	public boolean isFiring() {
		return state.v >= 30.0;
	}

	public int getID() {
		return nid;
	}

	public MNeuronState getState() {
		return state;
	}

	public MNeuronParams getParams() {
		return params;
	}
	
	public MVec3f getCoordinates() {
		return params.spatialCoords;
	}

	public ArrayList<MSynapse> getPreSynapses() {
		return preSynapses;
	}

	public ArrayList<MSynapse> getPostSynapses() {
		return postSynapses;
	}
}