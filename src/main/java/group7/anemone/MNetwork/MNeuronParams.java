package group7.anemone.MNetwork;

import java.io.Serializable;

/**
 * Izhikevich neuron parameters. Setting these to various configurations
 * allow the neuron to take on the behaviour of different neuron types.
 */
public class MNeuronParams implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7253780657653372645L;

	/* Izhikevich neuron parameters. */
	public double a, b, c, d;

	/* Excitatory vs inhibitory neurotransmitter. */
	public boolean isExcitatory;

	/* 3D Spatial coordinates. */
	public MVec3f spatialCoords = new MVec3f(0, 0, 0);
}