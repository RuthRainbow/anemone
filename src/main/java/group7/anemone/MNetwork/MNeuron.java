package group7.anemone.MNetwork;

import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;

/**
 * The MNeuron class represents one Izhikevich neuron and its current state. The
 * class provides methods for integrating its simulation as well as applying
 * voltaic input (perhaps via a preneuron action potential).
 * <p>
 * TODO: - Implement methods for accessing state information (eg, voltage).
 */
public class MNeuron implements Serializable {

	private static final long serialVersionUID = 4920400729701021728L;

	/* Neuron parameters (shouldn't change). */
	public MNeuronParams params;

	/* Neuron state (changes). */
	private MNeuronState state;

	/* Neuron ID. */
	private int nid;

	/* Pre and post synapses. */
	private ArrayList<MSynapse> preSynapses, postSynapses;

	/* 3D spatial coordinates. */
	public MVec3f disp;

	/**
	 * Copy constructor.
	 *
	 * @param neuron	the neuron to be copied
	 */
	public MNeuron(MNeuron neuron) {
		this.params = new MNeuronParams(neuron.params);
		this.state = new MNeuronState(neuron.state);
		this.nid = neuron.nid;
		this.preSynapses = new ArrayList<MSynapse>(neuron.preSynapses);
		this.postSynapses = new ArrayList<MSynapse>(neuron.postSynapses);
		this.disp = new MVec3f(neuron.disp);
	}

	/**
	 * Constructs a neuron with the given parameters.
	 *
	 * @param params	the neuron parameters
	 * @param state	the initial neuron state
	 * @param nid	the neuron id
	 */
	public MNeuron(MNeuronParams params, MNeuronState state, int nid) {
		/* Initialise parameters and state. */
		this.params = new MNeuronParams(params);
		this.state = new MNeuronState(state);

		/* Initialise pre and post synapse list. */
		this.preSynapses = new ArrayList<MSynapse>();
		this.postSynapses = new ArrayList<MSynapse>();

		/* Copy neuron id. */
		this.nid = nid;
	}

	void update() {
		if (isFiring()) {
			state.v = params.c;
			state.u += params.d;
			state.s += 1.0;
			doSTDP();
		}

		/* Perform update of Izhikevich model. */
		state.v += (0.04 * state.v * state.v + 5 * state.v + 140
			- state.u + state.I);
		state.u += params.a * (params.b * state.v - state.u);

		state.I = 0.0;

		/* Perform update of STDP model. */
		state.s += params.tau * (-state.s);
	}

	private void doSTDP() {
		MNeuron pre, post;
		double w, s;

		for (MSynapse syn : getPreSynapses()) {
			pre = syn.getPreNeuron();
			w = syn.getWeight();
			s = pre.getState().s;
			w += s * pre.getParams().ap;

			syn.setWeight(w);
		}

		for (MSynapse syn : getPostSynapses()) {
			post = syn.getPostNeuron();
			w = syn.getWeight();
			s = post.getState().s;
			w += s * post.getParams().am;

			syn.setWeight(w);
		}
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
		MNeuronState tmpState = new MNeuronState(state);
		return tmpState;
	}

	public MNeuronParams getParams() {
		MNeuronParams tmpParams = new MNeuronParams(params);
		return tmpParams;
	}

	public MVec3f getCoords() {
		MVec3f tmpCoords = new MVec3f(this.params.spatialCoords);
		return tmpCoords;
	}

	public ArrayList<MSynapse> getPreSynapses() {
		ArrayList<MSynapse> tmpSynapses
			= new ArrayList<MSynapse>(preSynapses);

		return tmpSynapses;
	}

	public void setPreSynapses(Collection<MSynapse> synapses) {
		this.preSynapses = new ArrayList<MSynapse>(synapses);
	}

	public ArrayList<MSynapse> getPostSynapses() {
		ArrayList<MSynapse> tmpSynapses
			= new ArrayList<MSynapse>(postSynapses);

		return tmpSynapses;
	}

	public void setPostSynapses(Collection<MSynapse> synapses) {
		this.postSynapses = new ArrayList<MSynapse>(synapses);
	}
	
	public void addPreSynapse(MSynapse synapse) {
		this.preSynapses.add(synapse);
	}
	
	public void addPostSynapse(MSynapse synapse) {
		this.postSynapses.add(synapse);
	}

	@Override
	public String toString() {
		return "" + this.nid;
	}
}
