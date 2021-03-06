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

	/* STDP model parameters. */
	public double tau, ap, am;

	/* Excitatory vs inhibitory neurotransmitter. */
	public boolean isExcitatory;

	/* 3D Spatial coordinates. */
	public MVec3f spatialCoords = new MVec3f(0, 0, 0);
	
	@Override
	public String toString() {
		return "a: " + this.a + " b: " + this.b + " c: " + this.c + " d: "
			+ this.d + " tau: " + this.tau + " ap: " + this.ap + " am: " + this.am;
	}

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
		this.tau = mNeuronParams.tau;
		this.ap = mNeuronParams.ap;
		this.am = mNeuronParams.am;
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
	 * @param tau
	 * @param ap
	 * @param am
	 * @param isExcitatory
	 * @param spatialCoords
	 */
	public MNeuronParams(double a, double b, double c, double d, double tau,
			double ap, double am, boolean isExcitatory,
			MVec3f spatialCoords)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.tau = tau;
		this.ap = ap;
		this.am = am;
		this.isExcitatory = isExcitatory;
		this.spatialCoords = new MVec3f(spatialCoords);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MNeuronParams)) {
			return false;
		} else {
			MNeuronParams otherparams = (MNeuronParams) other;
			if (!this.spatialCoords.equals(otherparams.spatialCoords)) return false;
			if (this.a != otherparams.a) return false;
			if (this.b != otherparams.b) return false;
			if (this.am != otherparams.am) return false;
			if (this.ap != otherparams.ap) return false;
			if (this.c != otherparams.c) return false;
			if (this.d != otherparams.d) return false;
			if (this.tau != otherparams.tau) return false;
			return true;
		}
	}
}
