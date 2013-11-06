package group7.anemone;

public class BPOutputLink extends BasicLink{

	public BPOutputLink(double initialWeight, int initialInputNode)
	{
		//Output Nodes must be created before a link can be made between the two of them.
		super(initialWeight, initialInputNode, 0);
		//May want to add in feature so that a loop can't be made between nodes.
	}

	public void lightup(double nodeEnergy)
	{
		//System.out.println("An output link lighting up");
		this.setActive(nodeEnergy);
	}
}
