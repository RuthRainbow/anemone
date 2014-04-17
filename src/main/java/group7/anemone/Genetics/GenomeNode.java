package group7.anemone.Genetics;

public abstract class GenomeNode {
	public final int id;
	
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
