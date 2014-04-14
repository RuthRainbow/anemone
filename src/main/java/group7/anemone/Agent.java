package group7.anemone;

//import group7.anemone.CPPN.CPPNEdge;
import group7.anemone.CPPN.CPPNNode;
import group7.anemone.CPPN.CPPNSimulation;
import group7.anemone.CPPN.CPPNFactory;
import group7.anemone.Genetics.GeneticObject;
import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.MNetwork.MFactory;
import group7.anemone.MNetwork.MNetwork;
import group7.anemone.MNetwork.MNeuron;
import group7.anemone.MNetwork.MNeuronParams;
import group7.anemone.MNetwork.MNeuronState;
import group7.anemone.MNetwork.MSimulation;
import group7.anemone.MNetwork.MSimulationConfig;
import group7.anemone.MNetwork.MSynapse;
import group7.anemone.MNetwork.MVec3f;
import group7.anemone.NeatGenetics.NeatGenome;
import group7.anemone.NeatGenetics.NeatNode;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import processing.core.PApplet;

public class Agent extends SimulationObject implements Serializable {

	private static final long serialVersionUID = -6755516656827008579L;
	transient PApplet parent;

	/* Anatomical parameters. */
	public static final int configNumSegments = 20;
	final double visionRange = 100;
	final double fov = 90;
	private final double maxSpeed = 15;

	/* Physics state. */
	private final Point2D.Double speed = new Point2D.Double(0, 0);
	private final Point2D.Double thrust = new Point2D.Double(0, 0);
	private final Point2D.Double drag = new Point2D.Double(0, 0);
	private double viewHeading = 0; // in degrees 0-360

	/* The agent's genome (from which the brain is generated). */
	//private final Genome genome;

	/*In hyper neat, this is the list of genomes to create the CPPN Networks to create the agents brain*/
	private final GeneticObject geneticObject;

	/* Brain state. */
	private MNetwork mnetwork;

	/* Brain simulation instance. */
	private MSimulation msimulation;

	/* Whether we are using Neat or HyperNEAT */
	private final boolean neat;

	/* Interface between the world and the brain. */
	private final NInterface ninterface = new NInterface(configNumSegments);

	/* Health state and stats. */
	private double fitness = 2;
	private double health = 1;
	private int age = 0; // Age of agent in number of updates.

	/* Objects  of interest within the agent's visual field. */
	private ArrayList<SightInformation> canSee
	= new ArrayList<SightInformation>();

	/**
	 * Instanciates an agent at a given coordinate in the simulation, with a
	 * given orientation and genome.
	 *
	 * The constructor uses genome to construct the neural network which is
	 * simulated in order to influence the physical state of the agent.
	 *
	 * @param coords	initial agent position within the world
	 * @param viewHeading	initial orientation of the agent
	 * @param p
	 * @param genome	the genome to be used to construct the brain
	 */
	public Agent(Point2D.Double coords,
			double viewHeading,
			PApplet p,
			GeneticObject geneticObject,
			boolean neat) {
		super(coords);
		this.parent = p;
		this.viewHeading = viewHeading;
		thrust(1);
		this.geneticObject = geneticObject;
		this.neat = neat;
		if (this.neat) { 
			createNeuralNet();
		} else {
			createBrain();
		}
		calculateNetworkPositions();
	}

	/**
	 * Uses the agent's genome to construct a neural network.
	 */
	private void createNeuralNet() {
		MSimulationConfig simConfig;
		HashMap<Integer, MNeuron> neuronMap = new HashMap<Integer, MNeuron>();
		ArrayList<MNeuron> neurons = new ArrayList<MNeuron>();
		ArrayList<MSynapse> synapses = new ArrayList<MSynapse>();

		NeatGenome genome = (NeatGenome) geneticObject;
		/* Create neurons. */
		for (NeatNode nn : genome.getNodes()) {
			int id = nn.getId();
			MNeuronParams params = nn.getParams();
			MNeuronState state =
					MFactory.createInitialRSNeuronState();

			/* Create a neuron. */
			MNeuron neuron = new MNeuron(params, state, id);

			/* Add it to temporary NID->Neuron map. */
			neuronMap.put(id, neuron);

			/* Add neuron to the list. */
			neurons.add(neuron);
		}

		/* Create synapses. */
		for (GenomeEdge<NeatNode> g : genome.getGene()) {
			/* Get the synapse information. */
			NeatNode preNode = g.getIn();
			NeatNode postNode = g.getOut();
			double weight = g.getWeight();
			int delay = g.getDelay();
			Integer preNid = new Integer(preNode.getId());
			Integer postNid = new Integer(postNode.getId());

			/* Find the pre and post neurons. */
			MNeuron preNeuron = neuronMap.get(preNid);
			MNeuron postNeuron = neuronMap.get(postNid);

			/* Create the synapse. */
			MSynapse synapse = new MSynapse(preNeuron, postNeuron,
					weight, delay);
			/*
			Add the synapse to the pre and post neuron synapse list
			 */
			ArrayList<MSynapse> postSynapses
			= preNeuron.getPostSynapses();
			ArrayList<MSynapse> preSynapses
			= postNeuron.getPreSynapses();

			postSynapses.add(synapse);
			preSynapses.add(synapse);

			preNeuron.setPostSynapses(postSynapses);
			postNeuron.setPreSynapses(preSynapses);

			/* Add the synapse to the list. */
			synapses.add(synapse);
		}

		/* Create the network. */
		this.mnetwork = new MNetwork(neurons, synapses);

		/* Create and set the simulation configuration parameters. */
		simConfig = new MSimulationConfig(20);

		/* Create the simulation instance with our network. */
		this.msimulation = new MSimulation(this.mnetwork, simConfig);
	}

	private void createBrain() {
		/**
		 * This method will create the agents brain.
		 * 
		 * The actual brain creation is handled by the CPPNFactory class, which will take as input the CPPNs that are 
		 * made from the agents genomes, and generate parts of the brain as each CPPN is fed into it.
		 * 
		 * Once all CPPNs have been passed into the CPPNFactory, the method "linkFinalLayer" is run, which will link the 
		 * latest CPPN layer with the final layer of output motory neurons.
		 * 
		 * The final brain will then be pulled from cFactory by calling "getBrain" and the returned MNetwork can be saved
		 * into the mnetwork value already stored globally by this agent class.
		 */

		//How many input and output nodes to create in the agents brain
		int inputNodes = 30;
		int outputNodes = 3;

		//Create a CPPNFactory. Feed the factory a series of CPPN and after all chromosomes have been read in, it can return a fully formed brain.
		CPPNFactory cFactory = new CPPNFactory(inputNodes,outputNodes);

		//An arraylist of CPPNNodes for the CPPN which will be queried to link neurons in the current layer of the brain
		ArrayList<CPPNNode> synapseParameterNodes = new ArrayList<CPPNNode>();

		//An arraylist of CPPNNodes for the CPPN which will be queries to get neuron parameters
		ArrayList<CPPNNode> neuronParameterNodes = new ArrayList<CPPNNode>();

		//The number of neurons that are currently being created in this layer of the brain
		int layerSize=0;

		//First, loop through every chromosome that the agent contains
		for (int g=0; g < geneticObject.getSize(); g++) {
			//TODO: Dan, this is where each chromosome is read and a CPPN needs to get created so it can be passed off

			/**
			 * Each 'chromosome' the agent contains is an instance of the genome class.
			 * 
			 * Each chromosome contains multiple types of genes that can contain the following information to build the CPPN:
			 * 1: A sort of 'header' gene that says how many neurons will be in this layer of the brain
			 * 2: Add nodes to the buildSyanpse CPPN, so this will need to include a 'type' integer, to designate the kind of function
			 * 		that this node will use. 3 Parameter integers, which will be used to augment the function so that
			 * 		each node has a higher degree of possible diversity.
			 * 		There are 4 different 'types' of nodes, which correspond to 0: Parabola, 1: Sigmoid, 2: Gauss, 3: Sin.
			 * 3: Add nodes to the buildNeurons CPPN, this should be built just like the buildSynapseCPPN. There will probably need to
			 * 		be some field in the gene that lets this part of the code distinguish between genes for the buildNeuronCPPN and the
			 * 		buildSyanpse CPPNs.
			 * 
			 * Some additional notes:
			 * 1: The first two nodes in any CPPN arrayList of nodes will always be the input nodes for that network.
			 * 2: The last node in the CPPN arrayList of nodes will always be the output node for that network.
			 */

			/**
			 * ##############
			 * CREATE CPPN HERE ###############################
			 * ##############
			 */

			//Once all genes are read in, build the CPPNSimulation so that the current layer can be built for real by querying the CPPN
			CPPNSimulation buildSynapse = new CPPNSimulation(synapseParameterNodes);

			CPPNSimulation buildNeurons = new CPPNSimulation(neuronParameterNodes);

			//Call the factory to add the new CPPN's and generate more of the agents brain
			cFactory.neuronCPPN(buildNeurons,layerSize);
			cFactory.synapseCPPN(buildSynapse, layerSize);
		}

		//Once all the CPPN's have been input to the cFactory, the brain will be finished and it can be pulled out.
		mnetwork = cFactory.getBrain();
	}

	/**
	 * Uses the sensory components of the interface to provide sensory input
	 * current to the brain.
	 */
	private void applySensoryInputToBrain() {
		ArrayList<MNeuron> neurons = mnetwork.getNeurons();

		/* Temporarily hardcode id's of neurons of interest. */
		int numVSegs = Agent.configNumSegments;
		int firstVFoodNid = 3;
		int lastVFoodNid = firstVFoodNid + numVSegs - 1;
		int firstVWallNid = lastVFoodNid + 1;
		int lastVWallNid = firstVWallNid + numVSegs - 1;
		int firstVEnemyNid = lastVWallNid + 1;
		int lastVEnemyNid = firstVEnemyNid + numVSegs - 1;

		/*
		 Provide input current the network using the sensory
		 components of the interface.
		 */
		for (MNeuron n : neurons) {
			double current;
			int index;
			int id = n.getID();

			/* If the neuron is a food visual neuron. */
			if (id >= firstVFoodNid && id <= lastVFoodNid) {
				index = id - 3;
				current = ninterface.affectors.vFood[index];
				n.addCurrent(60.0*current);
			} /* If the neuron is a wall visual neuron. */ else if (id >= firstVWallNid && id <= lastVWallNid) {
				index = id - numVSegs - 3;
				current = ninterface.affectors.vWall[index];
				n.addCurrent(60.0*current);
			} /* If the neuron is an enemy visual neuron. */ else if (id >= firstVEnemyNid && id <= lastVEnemyNid) {
				index = id - 2 * numVSegs - 3;
				current = ninterface.affectors.vEnemy[index];
				n.addCurrent(60.0*current);
			}
		}
	}

	/**
	 * Applies sensory input from the interface to the brain and steps the
	 * brain simulation.
	 */
	private void updateMNetwork() {
		/*
		 Having applied the network inputs, update the brain simulation.
		 */
		msimulation.step();
	}

	/**
	 * Inspects the brain's motor neurons for activity and performs motor
	 * actions accordingly (thrust and turning).
	 */
	private void applyMotorOutputs() {
		ArrayList<MNeuron> neurons = mnetwork.getNeurons();
		int thrustNeuronID = 0;
		int turnNegativeNeuronID = 1;
		int turnPositiveNeuronID = 2;

		for (MNeuron n : neurons) {
			/*
			 Perform physical actions if effector neurons are firing.
			 */
			if (n.getID() == thrustNeuronID) {
				if (n.isFiring()) {
					thrust(2);
				}
			}
			if (n.getID() == turnNegativeNeuronID) {
				if (n.isFiring()) {
					changeViewHeading(-20.0);
				}
			}
			if (n.getID() == turnPositiveNeuronID) {
				if (n.isFiring()) {
					changeViewHeading(20.0);
				}
			}
		}
	}

	public Object getGeneticRep() {
		return this.geneticObject.getGeneticRep();
	}

	public GeneticObject getGeneticObject() {
		return this.geneticObject;
	}

	public double getFitness() {
		return this.fitness;
	}

	void updatePhysics() {//update speed to be ...
		//calculate new drag value, average of speed x / y
		drag.x = Math.abs(speed.x / 100);
		drag.y = Math.abs(speed.y / 100);
		if (drag.x < 0.0001) {
			speed.x = 0;
		}
		if (drag.y < 0.0001) {
			speed.y = 0;
		}

		//implements thrusting
		speed.x += thrust.x;
		speed.y += thrust.y;
		thrust.x = 0;
		thrust.y = 0;

		//implements drag
		if (speed.x > 0) {
			speed.x -= drag.x;
		} else if (speed.x < 0) {
			speed.x += drag.x;
		}

		if (speed.y > 0) {
			speed.y -= drag.y;
		} else if (speed.y < 0) {
			speed.y += drag.y;
		}

		if (this.getMovingSpeed() > maxSpeed) {
			double ratio = maxSpeed / this.getMovingSpeed();
			speed.x = speed.x * ratio;
			speed.y = speed.y * ratio;
		}
	}

	void updatePosition() {
		coords.x += speed.x;
		coords.y += speed.y;
	}

	/**
	 * Updates the sensory components of neural interface.
	 */
	void updateSensors() {
		int visionDim = ninterface.affectors.getVisionDim();
		double distance;

		/* Update food vision variables. */
		for (int i = 0; i < visionDim; i++) {
			/* Food sensory neurons. */
			distance = viewingObjectOfTypeInSegment(i,
					Collision.TYPE_FOOD);
			ninterface.affectors.vFood[i]
					= distance < 0.0 ? 1.0 : 1.0 - distance;

			/* Ally sensory neurons. */
			distance = viewingObjectOfTypeInSegment(i,
					Collision.TYPE_AGENT);
			ninterface.affectors.vAlly[i]
					= distance < 0.0 ? 1.0 : 1.0 - distance;

			/* Enemy sensory neurons. */
			distance = viewingObjectOfTypeInSegment(i,
					Collision.TYPE_ENEMY);
			ninterface.affectors.vEnemy[i]
					= distance < 0.0 ? 1.0 : 1.0 - distance;

			/* Wall sensory neurons. */
			distance = viewingObjectOfTypeInSegment(i,
					Collision.TYPE_WALL);
			ninterface.affectors.vWall[i]
					= distance < 0.0 ? 1.0 : 1.0 - distance;
		}
	}

	void update() {
		updateSensors();
		applySensoryInputToBrain();
		for (int i = 0; i < 4; i++) {
			updateMNetwork();
		}
		applyMotorOutputs();
		updatePhysics();
		updatePosition();

		age++;
		health -= 0.001;
		fitness -= 0.001;
		if (age < 100) {
			updateFitness(0.001);
		} else if (age > 200) {
			updateFitness(-0.001);
		}
	}

	//Pre-calculates the coordinates of each neuron in the network
	//private ArrayList<MNeuron> placed;
	private HashMap<Integer, Integer> maxInLevel;
	//private int maxLevel = 0;
	//private int maxHeight = 0;

	private void calculateNetworkPositions() {
		//placed = new ArrayList<MNeuron>(); //store coordinates of placed neurons
		maxInLevel = new HashMap<Integer, Integer>(); //store max y coordinate at each level of tree
		maxInLevel.put(0, 0);
		//maxLevel = 0;
		//maxHeight = 0;

		ArrayList<MNeuron> neurons = mnetwork.getNeurons();
		ArrayList<MSynapse> synapses = mnetwork.getSynapses();

		//TODO: place nodes in set position then spread out using algorithm below
		//TODO: normalise to -125 to 125
		/*for(MSynapse s : mnetwork.getSynapses()){ //determine the x, y coordinates for each node based on links
		 MNeuron pre = s.getPreNeuron();
		 MNeuron post = s.getPostNeuron();
		 int level = 0;

		 if(!placed.contains(pre)){
		 if(placed.contains(post)){ //pre node not placed but post is, place at level - 1
		 level = (int) (post.getCoordinates().x / 20) - 1;
		 }

		 int max = maxValue(level);
		 addNode(level, max, pre);
		 }

		 if(!placed.contains(post)){
		 if(placed.contains(pre)){ //post node not placed but pre is, place at level + 1
		 level = (int) (pre.getCoordinates().x / 20);
		 }
		 level++;

		 int max = maxValue(level);
		 addNode(level, max, post);
		 }

		 if(pre.getCoordinates().x == post.getCoordinates().x) pre.getCoordinates().z += 20;
		 }
		 //offset nodes centrally
		 int offsetX = -maxLevel * 10;
		 int offsetY = -maxHeight * 10;
		 for(MNeuron n : mnetwork.getNeurons()){
		 n.getCoordinates().x += offsetX;
		 n.getCoordinates().y += offsetY;
		 }*/
		//Force directed graph drawing
		float area = 250 * 250 * 250; //The size of the area 
		//float C = 2;
		float k = (float) Math.sqrt(area / mnetwork.getVertexNumber());
		int setIterations = 250;
		double temp = 25;
		float kPow = (float) Math.pow(k, 2);

		for (int x = 0; x < neurons.size(); x++) {
			Random generator = new Random();
			int xAx = 125 - generator.nextInt(250);
			int yAx = 125 - generator.nextInt(250);
			int zAx = 125 - generator.nextInt(250);
			neurons.get(x).params.spatialCoords = new MVec3f(xAx, yAx, zAx);
		}

		//For a pre set number of iterations
		for (int i = 0; i < setIterations; i++) {
			//Calculate the repulsive force between neurons
			for (MNeuron v : neurons) {
				v.disp = MVec3f.zero();
				for (MNeuron u : neurons) {
					if (v != u) {
						//Calculate delta, the difference in position between the two neurons
						MVec3f delta = v.getCoords().subtract(u.getCoords());
						MVec3f absDelta = delta.abs();

						if (delta.x != 0) {
							v.disp.x += delta.x / absDelta.x * kPow / absDelta.x;
						}
						if (delta.y != 0) {
							v.disp.y += delta.y / absDelta.y * kPow / absDelta.y;
						}
						if (delta.z != 0) {
							v.disp.z += delta.z / absDelta.z * kPow / absDelta.z;
						}
					}

				}
			}

			//Calculate the attractive forces
			for (MSynapse e : synapses) {
				MNeuron v = e.getPreNeuron();
				MNeuron u = e.getPostNeuron();
				//Calculate delta, the difference in position between the two neurons
				MVec3f delta = v.getCoords().subtract(u.getCoords());
				MVec3f deltaPow = delta.pow(2);
				MVec3f absDelta = delta.abs();

				if (delta.x != 0) {
					v.disp.x -= delta.x / absDelta.x * deltaPow.x / k;
				}
				if (delta.y != 0) {
					v.disp.y -= delta.y / absDelta.y * deltaPow.y / k;
				}
				if (delta.z != 0) {
					v.disp.z -= delta.z / absDelta.z * deltaPow.z / k;
				}

				if (delta.x != 0) {
					u.disp.x += delta.x / absDelta.x * deltaPow.x / k;
				}
				if (delta.y != 0) {
					u.disp.y += delta.y / absDelta.y * deltaPow.y / k;
				}
				if (delta.z != 0) {
					u.disp.z += delta.z / absDelta.z * deltaPow.z / k;
				}

			}

			//Limit maximum displacement by the temperature
			//Also prevent the thing from being displaced outside the frame
			for (MNeuron v : neurons) {
				if (v.disp.x != 0) {
					v.getCoords().x += v.disp.x / Math.abs(v.disp.x) * Math.min(Math.abs(v.disp.x), temp);
				}
				if (v.disp.y != 0) {
					v.getCoords().y += v.disp.y / Math.abs(v.disp.y) * Math.min(Math.abs(v.disp.y), temp);
				}
				if (v.disp.z != 0) {
					v.getCoords().z += v.disp.z / Math.abs(v.disp.z) * Math.min(Math.abs(v.disp.z), temp);
				}

				v.getCoords().x = Math.min(250 / 2, Math.max(-250 / 2, v.getCoords().x));
				v.getCoords().y = Math.min(250 / 2, Math.max(-250 / 2, v.getCoords().y));
				v.getCoords().z = Math.min(250 / 2, Math.max(-250 / 2, v.getCoords().z));
			}

			//Reduce temperature
			temp = temp - 0.5;
			if (temp == 0) {
				temp = 1;
			}
		}

		mnetwork.setNeurons(neurons);

	}

	/*private void addNode(int level, int max, MNeuron node) {
		//if(node.getCoordinates() == null)
		node.getCoords().x = level * 20;
		node.getCoords().y = max;
		node.getCoords().z = 0;
		maxInLevel.put(level, max + 20);
		placed.add(node);
	}*/

	/*private int maxValue(int level) {
		int max = 0;
		if (!maxInLevel.containsKey(level)) {
			maxInLevel.put(level, 0);
			maxLevel++;
		} else {
			max = maxInLevel.get(level);
		}
		maxHeight = Math.max(maxHeight, (max / 20));
		return max;
	}*/

	protected void updateCanSee(ArrayList<SightInformation> see) {
		canSee = see;
	}

	ArrayList<SightInformation> getCanSee() {
		return canSee;
	}

	private void setThrust(double x, double y) {
		thrust.x = x;
		thrust.y = y;
	}

	protected void thrust(double strength) {
		double x = strength * Math.cos(viewHeading * Math.PI / 180);
		double y = strength * Math.sin(viewHeading * Math.PI / 180);
		setThrust(x, y);
	}

	protected void changeViewHeading(double h) {
		viewHeading += h;
	}

	protected void updateHealth(double h) {
		health += h;
		health = Math.min(1, health);
	}

	protected void updateFitness(double value) {
		fitness += value;
	}

	//returns the distance of the closest object in a specified segment, -1 if none found.
	public double viewingObjectOfTypeInSegment(int segment, int type) {
		ArrayList<SightInformation> filtered = new ArrayList<SightInformation>();

		for (SightInformation si : canSee) { //filter out those objects of type that are in the specified segment
			if (si.getType() == type && si.getDistanceFromLower() >= ((double) segment / configNumSegments) && si.getDistanceFromLower() < ((segment + 1.0) / configNumSegments)) {
				filtered.add(si);
			}
		}
		if (filtered.size() == 0) {
			return -1;
		}

		double dist = Double.MAX_VALUE;
		for (SightInformation si : filtered) {
			dist = Math.min(dist, si.getDistance());
		}

		return dist / visionRange;
	}

	public void stop() {
		speed.x = 0;
		speed.y = 0;
	}

	public void setX(int x) {
		coords.x = x;
	}

	public void setY(int y) {
		coords.y = y;
	}

	public double getHealth() {
		return health;
	}

	public double getViewHeading() {
		return viewHeading;
	}

	public double getVisionRange() {
		return visionRange;
	}

	public ArrayList<SightInformation> getAllViewingObjects() {
		return canSee;
	}

	int getNumSegments() {
		return configNumSegments;
	}

	public double getFOV() {
		return fov;
	}

	public double getChangeX() {
		return speed.x;
	}

	public double getChangeY() {
		return speed.y;
	}

	public double getMovingAngle() {
		double angle = 0;
		if (getChangeX() == 0) {
			if (getChangeY() < 0) {
				angle = -90;
			} else {
				angle = 90;
			}
		} else {
			angle = Math.atan((getChangeY()) / (getChangeX())) * 180.0 / Math.PI;
		}

		if (getChangeX() > 0) {
			if (getChangeY() < 0) {
				angle = 360 + angle;
			}
		} else {
			if (getChangeY() >= 0) {
				angle = 180 + angle;
			} else {
				angle += 180;
			}
		}
		return angle;
	}

	public double getMovingSpeed() {
		return Math.sqrt(Math.pow((float) (getChangeX()), 2) + Math.pow((float) (getChangeY()), 2));
	}

	MNetwork getNetwork() {
		return mnetwork;
	}

	NInterface getInterface() {
		return ninterface;
	}

	public int getSpeciesId() {
		return geneticObject.getSpeciesId();
	}

	public int getAge() {
		return this.age;
	}

	public int getType(){
		return Collision.TYPE_AGENT;
	}
}
