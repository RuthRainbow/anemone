package group7.anemone;
import java.util.ArrayList;
import java.lang.Math;


public class BPNode {
	
	private ArrayList<Integer> inputLinks = new ArrayList<Integer>();	//List of input link DESIGNATIONS. (Not actual links)
	private ArrayList<Integer> outputLinks =  new ArrayList<Integer>();	//List of output link DESIGNATIONS. (Not actual links)
	private ArrayList<Integer> startLinks = new ArrayList<Integer>();
	private ArrayList<Integer> finalOutputLinks = new ArrayList<Integer>();
	private int type;
	private double errorValue;
	private double target;
	private double logisticNumber;
	
	public BPNode(int nodeType) {	//When initially constructed the Node only recieves an intial activation threshold. Links are made after nodes.
		type = nodeType;
		errorValue=0;
		logisticNumber=0;
		target=0;
	}
	
	public void setTarget(double initialTarget)
	{
		target = initialTarget;
	}
	
	public void setError(double newError)
	{
		errorValue = newError;
	}
	
	public void addInputLink(int inputLinkDesignation)	//When a link is made that outputs TO this node, it will update this node to reflect the change in the network
	{
		inputLinks.add(inputLinkDesignation);
	}
	
	public void addStartLink(int startLinkDesignation)	//When a link is made that outputs TO this node, it will update this node to reflect the change in the network
	{
		startLinks.add(startLinkDesignation);
	}
	
	public void addFinalOutputLink(int finalOutputDesignation)
	{
		finalOutputLinks.add(finalOutputDesignation);
	}
	
	public void addOutputLink(int outputLinkDesignation)	//When a link is made that outputs FROM this node, it will update this node to reflect the change in the network
	{
		outputLinks.add(outputLinkDesignation);
	}
	
	public ArrayList<Integer> getStartLinks()
	{
		return startLinks;
	}
	
	public ArrayList<Integer> getInputLinks()
	{
		return inputLinks;	//Returns the list of input link designations (Not actual links)
	}
	
	public ArrayList<Integer> getOutputLinks()
	{
		return outputLinks;
	}
	
	public ArrayList<Integer> getFinalOutputLinks()
	{
		return finalOutputLinks;
	}
	
	public int getType()
	{
		return type;
	}
	
	public double getError()
	{
		return errorValue;
	}
	
	public double getTarget()
	{
		return target;
	}
	
	public double getLogisticNumber()
	{
		return logisticNumber;
	}
	
	public double fire(double inputLinkResults)
	{
		if (type!=0)	
		{
			logisticNumber = 1 / (1 + Math.exp(-inputLinkResults*1));
			System.out.println("inputLinkResults: " + inputLinkResults);
			System.out.println("Logistic Number: " + logisticNumber);
			return logisticNumber;
		}
		else	//If the node is an input node it shouldn't do the calculation, it should just pass its input straight on
		{
			logisticNumber = inputLinkResults;
			System.out.println("inputLinkResults: " + inputLinkResults);
			System.out.println("Logistic Number: " + logisticNumber);
			return logisticNumber;
		}
	}

}
