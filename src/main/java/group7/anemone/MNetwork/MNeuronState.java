package group7.anemone.MNetwork;

import java.io.Serializable;

/**
 * Izhikevich state parameters. These variables describe the current
 * voltage, ion-channel state and input current for a neuron.
 */
public class MNeuronState implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4964575820535511446L;
	public double v, u, I;
}