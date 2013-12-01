package group7.anemone;

import java.io.Serializable;

/**
 * The NIEffectors class encapsulates data that represent the state of
 * an agent's efferent organs ('actuators' or muscles).
 * <p>
 * This should be written to by a neural network and read by the
 * simulation to update agent bearing/speed etc.
 */
class NIEffectors implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2371546578849005327L;
	public double thrust;
	public double yawLeft, yawRight;

	public String toString() {
		return "Effectors: [thrust: "+thrust+", yawLeft: "+yawLeft+
			", yawRight: "+yawRight+"]";
	}
}