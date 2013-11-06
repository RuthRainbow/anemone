package group7.anemone;

public class BPLink extends BasicLink{

	public BPLink(double initialWeight, int initialInputNode, int initialOutputNode)
	{
		//Nodes must be created before a link can be made between the two of them.
		super(initialWeight, initialInputNode, initialOutputNode);
		//May want to add in feature so that a loop can't be made between nodes.
	}
	
	public void lightup(double nodeEnergy)
	{
		this.setActive(nodeEnergy*this.getWeight());
		//System.out.println("A normal link lighting up, with energy of: " + this.getActive());
	}
	
}
