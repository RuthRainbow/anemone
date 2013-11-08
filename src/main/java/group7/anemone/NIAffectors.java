package group7.anemone;

import java.util.Arrays;

/**
 * The NIAffectors class encapsulates data that represent an agent's
 * afferent organs: visual array, and motor propriorecetpors (directional
 * drag receptors).
 * <p>
 * The data encapsulated here should be updated by the simulation, to later
 * be read by a neural network.
 */
class NIAffectors {
	public double[] vFood, vAlly, vEnemy;
	public double mFront, mRight, mBack, mLeft;

	int visionDim;

	/**
	 * Consutructs an NIAffectors objects with a visual array of the given
	 * size.
	 *
	 * @param	visionDim	specifies the dimension of the visual array.
	 */
	NIAffectors(int visionDim) {
		this.visionDim = visionDim;

		vFood = new double[visionDim];
		vAlly = new double[visionDim];
		vEnemy = new double[visionDim];
	}

	/**
	 * @return	the dimension of the visual sensor array.
	 */
	public int getVisionDim() {
		return visionDim;
	}

	public String toString() {
		return "Affectors: [vFood: "+Arrays.toString(vFood)+", vAlly: "+
			Arrays.toString(vAlly)+", vEnemy: "+Arrays.toString(vEnemy)+
			", mFront: "+mFront+", mRight: "+mRight+", mBack: "+mBack+
			", mLeft: "+mLeft+"]";
	}
}