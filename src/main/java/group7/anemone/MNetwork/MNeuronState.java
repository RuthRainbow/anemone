package group7.anemone.MNetwork;

import java.io.Serializable;

/**
 * Izhikevich state parameters. These variables describe the current
 * voltage, ion-channel state and input current for a neuron.
 */
public class MNeuronState implements Serializable{
	private static final long serialVersionUID = -4964575820535511446L;
	
	public double v, u, I;
        public double s;
	
	/**
	 * Copy constructor.
	 * @param mNeuronState	a neuron state
	 */
	public MNeuronState(MNeuronState mNeuronState) {
            this.v = mNeuronState.v;
            this.u = mNeuronState.u;
            this.I = mNeuronState.I;
            this.s = mNeuronState.s;
        }
	
	/**
	 * Constructs an Izhikevich neuron state with the given parameters.
	 * 
	 * @param v
	 * @param u
	 * @param I 
	 */
        public MNeuronState(double v, double u, double I, double s) {
            this.v = v;
            this.u = u;
            this.I = I;
            this.s = s;
        }
}