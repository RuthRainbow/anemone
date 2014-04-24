package group7.anemone.Genetics;

/**
 * Class to represent a node in the neural network.
 */
public abstract class GenomeNode {
	public final int id;
	
	public GenomeNode() {
		this.id = -1;
	}
	
	public GenomeNode(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "ID: "+this.id;
	}
}
