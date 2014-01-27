package group7.anemone.MNetwork;

import java.io.Serializable;

/**
 * Izhikevich neuron parameters. Setting these to various configurations allow
 * the neuron to take on the behaviour of different neuron types.
 */
public class MNeuronParams implements Serializable {
	private static final long serialVersionUID = 7253780657653372645L;

	/* Izhikevich neuron parameters. */
	public double a, b, c, d;

	/* Excitatory vs inhibitory neurotransmitter. */
	public boolean isExcitatory;

	/* 3D Spatial coordinates. */
	public MVec3f spatialCoords = new MVec3f(0, 0, 0);

	/**
	 * Copy constructor.
	 * 
	 * @param mNeuronParams	neuron parameters
	 */
	public MNeuronParams(MNeuronParams mNeuronParams) {
		this.a = mNeuronParams.a;
		this.b = mNeuronParams.b;
		this.c = mNeuronParams.c;
		this.d = mNeuronParams.d;
		this.isExcitatory = mNeuronParams.isExcitatory;
		this.spatialCoords = new MVec3f(mNeuronParams.spatialCoords);
	}

	/**
	 * Constructs a neuron parameter object.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param isExcitatory
	 * @param spatialCoords 
	 */
	public MNeuronParams(double a, double b, double c, double d,
		boolean isExcitatory, MVec3f spatialCoords) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.isExcitatory = isExcitatory;
		this.spatialCoords = new MVec3f(spatialCoords);
	}
}
