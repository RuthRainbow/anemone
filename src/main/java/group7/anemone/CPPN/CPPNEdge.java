package group7.anemone.CPPN;

/**
 * A class representing a basic CPPN edge.
 */
public class CPPNEdge {
	double weight;
	
	/**
	 * Constructs a CPPN edge object with the given weight.
	 * @param weight edge weight
	 */
	CPPNEdge(double weight) {
		this.weight = weight;
	}
	
	/**
	 * Retrieves the weight for this edge object.
	 * @return current edge weight
	 */
	public double getWeight() {
		return weight;
	}
	
	/**
	 * Sets the weight for this edge object.
	 * @param weight new edge weight
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
