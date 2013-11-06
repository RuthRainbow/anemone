package group7.anemone;

public class BPStartLink extends BasicLink{

	public BPStartLink(double initialWeight, int initialOutputNode)
	{
		//Ouput Nodes must be created before a link can be made between the two of them.
		super(initialWeight, 0, initialOutputNode);
		//May want to add in feature so that a loop can't be made between nodes.
	}

	public void lightup()
	{
		this.setActive(this.getWeight());
		//System.out.println("A start link lighting up to: " + this.getActive());
	}

}
