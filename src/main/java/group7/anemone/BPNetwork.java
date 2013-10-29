package group7.anemone;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class BPNetwork {
	
	public BPNode[] nodes;
	public BPStartLink[] startLinks;
	public BPOutputLink[] outputLinks;
	public BPLink[] links;
	public double learningRate;
	
	public Queue<Integer> operationQ = new LinkedList<Integer>();
	public Queue<Integer> backQ = new LinkedList<Integer>();

	public BPNetwork() 
	{
		//On creation, create these arraylists to manage all the neurons and links in this particular network
		nodes = new BPNode[0];
		startLinks = new BPStartLink[0];
		outputLinks = new BPOutputLink[0];
		links = new BPLink[0];
		learningRate=1;
	}
	
	// TODO set weight of startlinks to the input that is currently being fed to the network.
	
	public void runNetwork() {
		System.out.println();
		System.out.println();
		System.out.println("Starting Neural Net...");
		System.out.println();
		/*
		 * Create the nodes and links for the Neural Net
		 * Edit this method to change the net or the outcome of the net
		 */
		generateOperationQ();	//Creates and sets up the Q which controls what order the neural net works through nodes during general operation
		
		/*
		 * This loop will run through the operations of a neural network.
		 * Firing nodes, lighting up the links etc.
		 * It will stop once all Nodes that have fired and their successive nodes have done their operations.
		 */
		while (operationQ.size()>0)
		{
			 nodeOperation(operationQ.poll() );
		}
	}
	
	public void backPropagation(int incre)
	{
		//Increment is used for checking how many times to run the backpropagation algorithm
		do
		{
			//Create the intial queue state for the backpropagation algorithm, consisting of all the output nodes
			System.out.println("########################");
			System.out.println("");
			System.out.println("Doing backprop increment: " + incre);
			System.out.println("");
			System.out.println("########################");
			System.out.println();


			generateBackQ();
			
			while (backQ.size()>0)
			{
				backprop(backQ.poll());
			}
			
			runNetwork();	//Run through the network again to get results of changes to system
			incre--;
			
		} while(incre>0);	//Set to 0 to never run backpropagation. 1 or higher adds that many repititions.
		System.out.println();
	}
	
	public void nodeOperation(int node) 
	{
		System.out.println();
		System.out.println("------------");
		System.out.println("Doing a node operation on: " + node);
		
		BPNode currentNode = this.nodes[node];	//Copy the appropriate node from the global arraylist (Nothing is changed in the node so we don't need to keep reffering to the real one, unlike links)
		
		System.out.println("Node has: " + this.nodes[node].getStartLinks().size() + " start links");
		
		System.out.println("Node has: " + this.nodes[node].getInputLinks().size() + " input links");
		
		System.out.println("Node has: " + this.nodes[node].getOutputLinks().size() + " output links");
		
		ArrayList<Integer> nodesForQueue = new ArrayList<Integer>();	//List of nodes that the output links from currentNode go to, which must be added to the queue in the case that the output links are activated
		
		double nodeInputEnergy=0;
		double fireCheck=0; //0 means node does not fire, 1 means node does fire
		
		System.out.println("Adding up the total input energy of node: " + node);
		for(int lsize=0; lsize<this.nodes[node].getInputLinks().size(); lsize++)	//Run through all the links that input into the current node
		{
			nodeInputEnergy = nodeInputEnergy+this.links[this.nodes[node].getInputLinks().get(lsize)].getActive(); //Get the activation energy of the link that is currently being look at from the list of links that feed into the current node.
			System.out.println("Energy: " + nodeInputEnergy);
		}
		for(int ssize=0; ssize<this.nodes[node].getStartLinks().size(); ssize++)	//Run through all the links that input into the current node
		{
			nodeInputEnergy = nodeInputEnergy+this.startLinks[this.nodes[node].getStartLinks().get(ssize)].getActive(); //Get the activation energy of the link that is currently being look at from the list of links that feed into the current node.
			System.out.println("Energy: " + nodeInputEnergy);
		}
		//nodeInputEnergy will now have the total activation energy of all links feeding into the currentNode
		
		//Do fire
		fireCheck = currentNode.fire(nodeInputEnergy);
		
		//If fire true then light up output links from current nodes
		if (fireCheck!=0)
		{
			System.out.println("Node firing");
			for(int nlink=0; nlink<this.nodes[node].getOutputLinks().size(); nlink++)	//For each link that feeds out of currentNode
			{
				nodesForQueue.add(this.links[this.nodes[node].getOutputLinks().get(nlink)].getOutputNode()); //Adds the output node for this link to the list
				this.links[this.nodes[node].getOutputLinks().get(nlink)].lightup(this.nodes[node].getLogisticNumber());	//Tells the link to lightup so that its output nodes will know to count its energy
			}
			for(int olink=0; olink<this.nodes[node].getFinalOutputLinks().size(); olink++)	//For each link that feeds out of currentNode
			{
				this.outputLinks[this.nodes[node].getFinalOutputLinks().get(olink)].lightup(this.nodes[node].getLogisticNumber());	//Tells the link to lightup so that its output nodes will know to count its energy
			}
		}
		
		//Add nodes from outputlinks to the queue
		for (int n=0; n<nodesForQueue.size(); n++)
		{
			if(!operationQ.contains(nodesForQueue.get(n)))	//If the current node in question isn't currently inside the queue, then continue
			{	
				operationQ.add( nodesForQueue.get(n) );	//Adds the designation of a node that must eventually be checked to the queue
			}
		}
		System.out.println("--------------");
	}
	
	//Called for each node as it is brought up for processing. Node is the number of the node in the arrayList 'nodes'
	public void backprop(int node)
	{
		System.out.println();
		System.out.println("------------");
		System.out.println("Doing a error calculation on node: " + node);
		
		//This list will keep track of all nodes that should now be added to the queue to run through.
		ArrayList<Integer> nodesForQueue = new ArrayList<Integer>();
		
		//List of all inputs into the node
		ArrayList<Integer> currentInputs = new ArrayList<Integer>();
		
		//The error of the current node, to be calculated later
		double nodeError;	
		
		//The output energy of the current node
		double currentOutput = this.nodes[node].getLogisticNumber();
		
		System.out.println("Current Nodes output: " + this.nodes[node].getLogisticNumber());
		
		if (this.nodes[node].getType() == 1)	//If the current node is a hidden layer node, then...
		{
			System.out.println("Current node is: Hidden Layer Node");
			//List of links that the node provides output to
			ArrayList<Integer> outputLinks = new ArrayList<Integer>();
			
			//Will be used to total the error of all output nodes this node is connected to
			double outputNodeErrorSum=0;
			
			//Gets the list of outputLinks this node is connected to
			outputLinks = this.nodes[node].getOutputLinks();
			
			//Go through all the output nodes to calculate their combined error values with link weights
			for(int x=0; x<outputLinks.size(); x++)
			{
				System.out.println("Adding link: " + x);
				
				//The index designation of the link connecting to the output we want to add the error sum of now
				int currentLinkDesignation = outputLinks.get(x);
				
				//The node which is connected to the link X which handles output from the current backProp node
				int outputNodeX = this.links[outputLinks.get(x)].getOutputNode();
				
				//Calculates part of the error value for this node based on the error values of nodes after this one and their link weights
				outputNodeErrorSum = outputNodeErrorSum + ( this.nodes[outputNodeX].getError() * this.links[currentLinkDesignation].getWeight() );
			}
		
			nodeError = currentOutput * (1-currentOutput) * outputNodeErrorSum;	//Calculate the error value for this node
			System.out.println("Current Node Error: " + nodeError);
			this.nodes[node].setError(nodeError);	//Set the nodes current error value
			
			//
			currentInputs = this.nodes[node].getInputLinks();
			
			//Go through all links to adjust weights
			for (int x=0; x<currentInputs.size(); x++)	
			{
				System.out.println("");
				System.out.println("Link: " + x);
				
				//The index designation of the link the loop has reached
				int currentLinkDesignation = currentInputs.get(x);
				System.out.println("Link Designation: " + currentLinkDesignation);
				
				double previousWeight = this.links[currentLinkDesignation].getWeight();
				
				//For the current inputLink, get the output of the node that feeds into it
				double inputNodesOutput = this.nodes[this.links[currentLinkDesignation].getInputNode()].getLogisticNumber();
				
				//Calculate the new weight for the current Link
				double newWeight = this.links[currentLinkDesignation].getWeight()+ ( learningRate*(nodeError* inputNodesOutput ));
				
				//Adjust the weight of the nodes leading into this node to minimize error
				this.links[currentLinkDesignation].adjustWeight(newWeight);
				
				//Add input nodes to the queue to be processed later
				nodesForQueue.add(this.links[currentLinkDesignation].getInputNode());	
				
				System.out.println("Previous Weight: " + previousWeight);
				System.out.println("New Weight: " + this.links[currentInputs.get(x)].getWeight());
				System.out.println("");
			}
		}
		else if(this.nodes[node].getType() == 2)	//If the current node is an output node
		{
			System.out.println("Current node is: Output Node");
			double currentTarget = (double) this.nodes[node].getTarget();	//The target is only known for the output nodes
			System.out.println("Current Target: " + currentTarget);
			nodeError = (currentTarget-currentOutput)*(1-currentOutput)*currentOutput;	//Work out the error value for this node
			System.out.println("Calculating Error: " + "(" + currentTarget + "-" + currentOutput + ")*(1+" + currentOutput + ")*" + currentOutput);
			System.out.println("Current Node Error: " + nodeError);
			this.nodes[node].setError(nodeError);	//Set the current nodes errorValue
			
			currentInputs = this.nodes[node].getInputLinks();
			for (int x=0; x<currentInputs.size(); x++)	//Go through all links to adjust weights
			{
				System.out.println("");
				System.out.println("Link: " + currentInputs.get(x));
				double previousWeight = this.links[currentInputs.get(x)].getWeight();	//Current weight of one of the links between the current node and a previous one
				this.links[currentInputs.get(x)].adjustWeight( previousWeight + ( learningRate*(nodeError*currentOutput)));	//Adjust the weight of the nodes leading into this node to minimize error
				System.out.println("Correcting Weight: " + previousWeight + " + " + learningRate + "(" + nodeError + "+" + currentOutput + ") = " + this.links[currentInputs.get(x)].getWeight());
				nodesForQueue.add(this.links[currentInputs.get(x)].getInputNode());	//Add input nodes to the queue to be processed later
				System.out.println("Adding '" + this.links[currentInputs.get(x)].getInputNode() + "' to the queue");
				System.out.println("");
			}
		}
		else //The only remaining possibility is that the node is an input node
		{
			System.out.println("Current Node is: Input node");
			System.out.println("No more calculations are needed");
			//Node is a start node so nothing really needs to be done.
		}
			
		for (int n=0; n<nodesForQueue.size(); n++)
		{
			if(!backQ.contains(nodesForQueue.get(n)))	//If the current node in question isn't currently inside the queue, then continue
			{	
				backQ.add( nodesForQueue.get(n) );	//Adds the designation of a node that must eventually be checked to the queue
			}
		}
		
		System.out.println("--------------");
	}
	
	
	public void input(int startLink, int inputValue)
	{
		startLinks[startLink].adjustWeight(inputValue);
	}
	
	public void setNodeTarget(int outputNode, int outputValue)
	{
		nodes[outputNode].setTarget(outputValue);
	}
	
	public void adjustLearningRate(double newLearningRate)
	{
		learningRate = newLearningRate;
	}
	
	public void generateOperationQ()
	{
		/*
		 * This Method will generate the queue of nodes for the neural net to run through.
		 * When created it lights up startLinks, to begin normal operation of the neural net.
		 * The method then adds nodes which recieve the output of these startlinks to the operation Q.
		 * This way only nodes which are being used will ever be checked.
		 * Nodes are in an inactive state by default.
		 */
		for (int x=0; x<this.startLinks.length; x++)
		{
			//lightup start links
			this.startLinks[x].lightup();
			
			operationQ.add(this.startLinks[x].getOutputNode());	//Get the designation of the node startlink goes to and store in operationQ, finds nodes they connect to, add those nodes to the queue
		}
	}
	
	public void generateBackQ()
	{
		/*
		 * Almost identical to operationQ
		 * This queue is instead used for the backpropagation part of this system
		 * It will take outputlinks, and add their nodes to the list, beause they are what needs to be considered first.
		 */
		for (int x=0; x<this.outputLinks.length; x++)
		{
			System.out.println();
			System.out.println("Current output links input node: " + this.outputLinks[x].getInputNode());
			//Add output nodes for the backpropagation algorithm
			backQ.add(this.outputLinks[x].getInputNode());
			
			System.out.println();
			System.out.println("Back queue has:" + backQ.peek() + " as top");
			System.out.println("With the logistic number of: " + this.nodes[backQ.peek()].getLogisticNumber());
		}
	}
	
	public void printResults()
	{
		System.out.println("----------------------");
		System.out.println("");
		System.out.println("Results:");
		for(int x=0; x<outputLinks.length; x++)
		{
			System.out.println("outputLink: " + x + "   |   result: " + outputLinks[x].getActive() + "   |   Error: " + nodes[outputLinks[x].getInputNode()].getError());
			System.out.println();
		}
		System.out.println("----------------------");
	}
}
