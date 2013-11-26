package group7.anemone.MNetwork;

/**
 * Izhikevich neuron parameters. Setting these to various configurations
 * allow the neuron to take on the behaviour of different neuron types.
 */
public class MNeuronParams {
	/* Izhikevich neuron parameters. */
	public double a, b, c, d;

	/* Excitatory vs inhibitory neurotransmitter. */
	public boolean isExcitatory;

	/* 3D Spatial coordinates. */
	public MVec3f spatialCoords;
}