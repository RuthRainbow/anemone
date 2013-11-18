package group7.anemone.BPNetwork;

public class BasicLink {

	private double weight;
	private double active;
	private int inputNode, outputNode;	//Designation of the input and output nodes, not the nodes themselves.

	public BasicLink(double initialWeight, int initialInputNode, int initialOutputNode)
	{
		//Nodes must be created before a link can be made between the two of them.
		weight = initialWeight;
		inputNode = initialInputNode;
		outputNode = initialOutputNode;
		active=0;
		//May want to add in feature so that a loop can't be made between nodes.
	}
	
	public int getInputNode()
	{
		return inputNode;
	}
	
	public int getOutputNode()
	{
		return outputNode;
	}
	
	public double getWeight()
	{
		return weight;
	}
	
	public double getActive()
	{
		return active;
	}
	
	public void setActive(double newActive)
	{
		active=newActive;
	}
	
	public void adjustWeight(double newWeight)
	{
		weight = newWeight;
	}
}