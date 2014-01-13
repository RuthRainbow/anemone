package group7.anemone;

import group7.anemone.MNetwork.MNetwork;
import group7.anemone.MNetwork.MNeuron;
import group7.anemone.MNetwork.MNeuronParams;
import group7.anemone.MNetwork.MNeuronState;
import group7.anemone.MNetwork.MSimulation;
import group7.anemone.MNetwork.MSimulationConfig;
import group7.anemone.MNetwork.MSynapse;
import group7.anemone.MNetwork.MVec3f;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import processing.core.PApplet;

public class Agent extends SimulationObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6755516656827008579L;
	transient PApplet parent;
	private Point2D.Double speed = new Point2D.Double(0, 0);
	private Point2D.Double thrust = new Point2D.Double(0, 0);
	private Point2D.Double drag = new Point2D.Double(0, 0);
	private double fitness = 2;
	private double health = 1;
	private int age = 0; // Age of agent in number of updates.
	private double viewHeading = 0; // in degrees 0-360
	private double visionRange = 100; //how far they can see into the distance
	private double fov = 45; //field of view, +-
	private ArrayList<SightInformation> canSee;
	private double maxSpeed = 15;
	public double size = 10;
	

	/*
	 * GENOME LAYOUT:
	 * 		* Each index is an individual gene
	 * 		* Each gene has 4 values.
	 * 		* VALUE 1: Historical Marker (Not very biological, maybe we can change how this part works later.
	 * 		* VALUE 2: Node where a link originates from
	 * 		* VALUE 3: Node that a link connects to
	 * 		* VALUE 4: Enabled/disabled gene
	 */
	private Genome genome;

	public static final int configNumSegments = 10;

	private MNetwork mnetwork;
	private MSimulation msimulation;
	private NInterface ninterface;

	public Agent(
			Point2D.Double coords, double viewHeading, PApplet p, Genome genome) {
		super(coords);
		ninterface = new NInterface(configNumSegments);
		canSee = new ArrayList<SightInformation>();
		this.parent = p;
		this.viewHeading = viewHeading;
		thrust(1);
		this.genome = genome;
		createSimpleNetwork();
		calculateNetworkPositions();
	}

	public Agent(Gene[] newGenome) {
		super(new Point2D.Double());
		this.genome = new Genome(newGenome);
	}

	private void createSimpleNetwork() {
		MSimulationConfig simConfig = new MSimulationConfig();
		ArrayList<MNeuron> neurons = new ArrayList<MNeuron>();
		ArrayList<MSynapse> synapses = new ArrayList<MSynapse>();
		MNeuronParams nparams = new MNeuronParams();
		MNeuronState nstate = new MNeuronState();

		/* Set the neurons to RS (regular spiking) neurons. */
		nparams.a = 0.1;
		nparams.b = 0.2;
		nparams.c = -65.0;
		nparams.d = 8.0;
		
		//Set default neuron coordinates
		nparams.spatialCoords = new MVec3f(0, 0, 0);

		/* Set the neurons to start in a resting state. */
		nstate.v = -65.0;
		nstate.u = 0.0;
		nstate.I = 0.0;

		/* Create the neurons. */

		/*
		MNeuron sn1 = new MNeuron(nparams, nstate, 0);
		MNeuron sn2 = new MNeuron(nparams, nstate, 1);
		MNeuron snL = new MNeuron(nparams, nstate, 2);
		MNeuron snR = new MNeuron(nparams, nstate, 3);
		MNeuron mn = new MNeuron(nparams, nstate, 4);
		MNeuron mnL = new MNeuron(nparams, nstate, 5);
		MNeuron mnR = new MNeuron(nparams, nstate, 6);

		neurons.add(sn1);
		neurons.add(sn2);
		neurons.add(snL);
		neurons.add(snR);
		neurons.add(mn);
		neurons.add(mnL);
		neurons.add(mnR);

		// Create the synapses.
		MSynapse ss1 = new MSynapse(sn1, mn, 4.0, 1);
		MSynapse ss2 = new MSynapse(sn2, mn, 4.0, 1);
		MSynapse ssL = new MSynapse(snL, mnL, 4.0, 1);
		MSynapse ssR = new MSynapse(snR, mnR, 4.0, 1);

		synapses.add(ss1);
		synapses.add(ss2);
		synapses.add(ssL);
		synapses.add(ssR);

		// This should probably be done by MNetwork.
		sn1.getPostSynapses().add(ss1);
		sn2.getPostSynapses().add(ss2);
		snL.getPostSynapses().add(ssL);
		snR.getPostSynapses().add(ssR);
		mn.getPreSynapses().add(ss1);
		mn.getPreSynapses().add(ss2);
		mnL.getPreSynapses().add(ssL);
		mnR.getPreSynapses().add(ssR);
		*/

		for (int x=0; x<genome.getLength(); x++) {
			int preNodeID = genome.getXthIn(x);
			int postNodeID = genome.getXthOut(x);


			int maxNeuron = Math.max(preNodeID, postNodeID); //Finds the max neuron to make sure that there are enough neurons in the arraylist
			maxNeuron++;
			if (maxNeuron>neurons.size()) {
				int initialSize = neurons.size();
				//If the neuron linked to is beyond the current scope of the network, you know you'll need at least that many, so we had better make them now
				int difference = maxNeuron-initialSize;
				for(int y=0; y<difference; y++) {
					int newNeuronID = initialSize+y;
					//System.out.println("Adding neuron ID: " + newNeuronID);
					neurons.add(new MNeuron(nparams, nstate, newNeuronID));
				}
			}

			//Now that we are sure there are enough neurons in the arraylist for links to be made for this gene, we can make the synapse.
			MSynapse newSyn = new MSynapse(
					neurons.get(preNodeID), neurons.get(postNodeID), genome.getXthWeight(x), genome.getXthDelay(x));
			synapses.add(newSyn);

			neurons.get(preNodeID).getPostSynapses().add(newSyn);
			neurons.get(postNodeID).getPreSynapses().add(newSyn);
		}

		/* Create the network. */
		this.mnetwork = new MNetwork(neurons, synapses, 0.0f, 0.0f, 0.0f);

		/* Set the simulation configuration parameters. */
		simConfig.eventHorizon = 20;

		/* Create the simulation. */
		this.msimulation = new MSimulation(this.mnetwork, simConfig);
	}

	private void updateMNetwork() {
		ArrayList<MNeuron> neurons = mnetwork.getNeurons();
		int thrustNeuronID = 0;
		int turnNegativeNeuronID = 1;
		int turnPositiveNeuronID = 2;

		/* Apply inputs from sensors to network. */
		for (MNeuron n : neurons) {
			int id = n.getID();
			
			if(id >= 3 && id < 3 + Agent.configNumSegments){
				n.addCurrent(ninterface.affectors.vFood[id - 3]);
			}else if(id >= 3 + Agent.configNumSegments && id < 3 + Agent.configNumSegments * 2){
				n.addCurrent(ninterface.affectors.vWall[id - 3 - Agent.configNumSegments]);
			}else if(id >= 3 + Agent.configNumSegments * 2 && id < 3 + Agent.configNumSegments * 3){
				n.addCurrent(ninterface.affectors.vEnemy[id - 3 - Agent.configNumSegments * 2]);
			}
			
			if (n.getID()==thrustNeuronID) {
				if (n.isFiring()) {
					thrust(2);
				}
			}
			if (n.getID()==turnNegativeNeuronID) {
				if (n.isFiring()) {
					changeViewHeading(-5.0);
				}
			}
			if (n.getID()==turnPositiveNeuronID) {
				if (n.isFiring()) {
					changeViewHeading(5.0);
				}
			}
		}

		msimulation.step();
	}

	protected Genome getStringRep() {
		return this.genome;
	}

	protected double getFitness() {
		return this.fitness;
	}

	void updateSpeed(){//update speed to be ...
		//calculate new drag value, average of speed x / y
		drag.x = Math.abs(speed.x / 100);
		drag.y = Math.abs(speed.y / 100);
		if(drag.x < 0.0001) speed.x = 0;
		if(drag.y < 0.0001) speed.y = 0;

		//implements thrusting
		speed.x += thrust.x;
		speed.y += thrust.y;
		thrust.x = 0;
		thrust.y = 0;

		//implements drag
		if(speed.x > 0) speed.x -= drag.x;
		else if(speed.x < 0) speed.x += drag.x;

		if(speed.y > 0) speed.y -= drag.y;
		else if(speed.y < 0) speed.y += drag.y;
		
		if(this.getMovingSpeed() > maxSpeed){
			//System.out.println("Exceeded max speed. Speed: "+this.getMovingSpeed()+", Max: "+maxSpeed);
			double ratio = maxSpeed / this.getMovingSpeed(); 
			speed.x = speed.x * ratio;
			speed.y = speed.y * ratio;
			//System.out.println("New speed: "+this.getMovingSpeed());
		}
	}

	/*
	 * TODO:
	 * - Update the motor proprioreceptors (ninterface.affectors.m*)
	 * - Normalisation.
	 */
	void updateSensors(){
		int visionDim = ninterface.affectors.getVisionDim();
		double distance;

		/* Update food vision variables. */
		for (int i=0; i<visionDim; i++) {
			/* Food sensory neurons. */
			distance = viewingObjectOfTypeInSegment(i, Collision.TYPE_FOOD);
			ninterface.affectors.vFood[i] = distance < 0.0 ? 0.0 : distance;

			/* Ally sensory neurons. */
			distance = viewingObjectOfTypeInSegment(i, Collision.TYPE_AGENT);
			ninterface.affectors.vAlly[i] = distance < 0.0 ? 0.0 : distance;

			/* Enemy sensory neurons. */
			distance = viewingObjectOfTypeInSegment(i, Collision.TYPE_ENEMY);
			ninterface.affectors.vEnemy[i] = distance < 0.0 ? 0.0 : distance;

			/* Wall sensory neurons. */
			distance = viewingObjectOfTypeInSegment(i, Collision.TYPE_WALL);
			ninterface.affectors.vWall[i] = distance < 0.0 ? 0.0 : distance;
		}
	}

	void update(){
		updateSensors();
		for(int i = 0; i < 64; i++){
			updateMNetwork();
		}
		updateSpeed();

		//TODO Move the change of coords to the update speed section?? -Seb
		coords.x += speed.x;	//Changes the coordinates to display distance travelled since last update
		coords.y += speed.y;
		for(int i=(int) (coords.x-this.size)/5;i<((int) coords.x+this.size)/5;i++){
			for(int j=(int) (coords.y-this.size)/5;j<((int) coords.y+this.size)/5;j++){
				if(i < 0) Environment.collisions.add(new Collision(this, Environment.wall.get(3)));
				if(j < 0) Environment.collisions.add(new Collision(this, Environment.wall.get(0)));
				if(i >= Environment.width/5) Environment.collisions.add(new Collision(this, Environment.wall.get(1)));
				if(j >= Environment.height/5) Environment.collisions.add(new Collision(this, Environment.wall.get(2)));
				
				if(i < 0 || j < 0 || i >= Environment.width/5 || j >= Environment.height/5){
					
				}else if(Environment.map[i][j] != null && Environment.map[i][j].positionupdated == true){
					//Collision has happened!
					Environment.collisions.add(new Collision(this, Environment.map[i][j]));
				}else{
					Environment.map[i][j] = this;
					
				}
			}
		}
		this.positionupdated = true;
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
	private ArrayList<MNeuron> placed;
    private HashMap<Integer, Integer> maxInLevel;
    private int maxLevel = 0;
    private int maxHeight = 0;
	private void calculateNetworkPositions(){
		placed = new ArrayList<MNeuron>(); //store coordinates of placed neurons
	    maxInLevel = new HashMap<Integer, Integer>(); //store max y coordinate at each level of tree
	    maxInLevel.put(0, 0);
	    maxLevel = 0;
	    maxHeight = 0;
	    
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
	    float area = 250*250*250; //The size of the area 
	    //float C = 2;
	    float k = (float) Math.sqrt(area/mnetwork.getVertexNumber());
	    int setIterations = 250;
	    double temp=25;
	    float kPow = (float) Math.pow(k, 2);
	    
	    for(int x=0; x<neurons.size(); x++) {
	    	Random generator = new Random();
			int xAx = 125 - generator.nextInt(250);
			int yAx = 125 - generator.nextInt(250);
			int zAx = 125 - generator.nextInt(250);
	    	neurons.get(x).params.spatialCoords = new MVec3f(xAx, yAx, zAx);
	    }
	    
	    //For a pre set number of iterations
	    for (int i=0; i<setIterations; i++) {
	    	//Calculate the repulsive force between neurons
		    for (MNeuron v : neurons) {
		    	v.disp = MVec3f.zero();
		    	for(MNeuron u : neurons) {
		    		if (v!=u) {
		    			//Calculate delta, the difference in position between the two neurons
		    			MVec3f delta = v.getCoordinates().subtract(u.getCoordinates());
		    			MVec3f absDelta = delta.abs();
		    			
		    			if(delta.x != 0) v.disp.x += delta.x / absDelta.x * kPow / absDelta.x;
		    			if(delta.y != 0) v.disp.y += delta.y / absDelta.y * kPow / absDelta.y;
		    			if(delta.z != 0) v.disp.z += delta.z / absDelta.z * kPow / absDelta.z;
		    		}
		    		
		    	}
		    }
		    
		    //Calculate the attractive forces
	    	for(MSynapse e : synapses) {
	    		MNeuron v = e.getPreNeuron();
	    		MNeuron u = e.getPostNeuron();
    			//Calculate delta, the difference in position between the two neurons
    			MVec3f delta = v.getCoordinates().subtract(u.getCoordinates());
    			MVec3f deltaPow = delta.pow(2);
    			MVec3f absDelta = delta.abs();
    			
    			if(delta.x != 0) v.disp.x -= delta.x / absDelta.x * deltaPow.x / k;
    			if(delta.y != 0) v.disp.y -= delta.y / absDelta.y * deltaPow.y / k;
    			if(delta.z != 0) v.disp.z -= delta.z / absDelta.z * deltaPow.z / k;
    			
    			if(delta.x != 0) u.disp.x += delta.x / absDelta.x * deltaPow.x / k;
    			if(delta.y != 0) u.disp.y += delta.y / absDelta.y * deltaPow.y / k;
    			if(delta.z != 0) u.disp.z += delta.z / absDelta.z * deltaPow.z / k;
    			
    			
	    	}
	    	
		    //Limit maximum displacement by the temperature
		    //Also prevent the thing from being displaced outside the frame
		    for (MNeuron v : neurons) {
		    	if(v.disp.x != 0) v.getCoordinates().x += v.disp.x / Math.abs(v.disp.x) * Math.min(Math.abs(v.disp.x), temp);
    			if(v.disp.y != 0) v.getCoordinates().y += v.disp.y / Math.abs(v.disp.y) * Math.min(Math.abs(v.disp.y), temp);
    			if(v.disp.z != 0) v.getCoordinates().z += v.disp.z / Math.abs(v.disp.z) * Math.min(Math.abs(v.disp.z), temp);
		    	
		    	v.getCoordinates().x = Math.min(250/2, Math.max(-250/2, v.getCoordinates().x));
		    	v.getCoordinates().y = Math.min(250/2, Math.max(-250/2, v.getCoordinates().y));
		    	v.getCoordinates().z = Math.min(250/2, Math.max(-250/2, v.getCoordinates().z));
		    }
		    
		    //Reduce temperature
		    temp = temp-0.5;
		    if (temp==0) {
		    	temp=1;
		    }
	    }
	    
	    mnetwork.setNeurons(neurons);

	}
	
	private void addNode(int level, int max, MNeuron node){
		//if(node.getCoordinates() == null)
		node.getCoordinates().x = level * 20;
		node.getCoordinates().y = max;
		node.getCoordinates().z = 0;
		maxInLevel.put(level, max + 20);
		placed.add(node);
	}
	private int maxValue(int level){
		int max = 0;
		if(!maxInLevel.containsKey(level)) {
			maxInLevel.put(level, 0);
			maxLevel++;
		}else max = maxInLevel.get(level);
		maxHeight = Math.max(maxHeight, (max/20));
		return max;
	}

	protected void updateCanSee(ArrayList<SightInformation> see){
		canSee = see;
	}
	ArrayList<SightInformation> getCanSee(){return canSee;}

	private void setThrust(double x, double y){
		//This will be called by the neural network to
		thrust.x = x;
		thrust.y = y;
	}
	protected void thrust(double strength){
		double x = strength * Math.cos(viewHeading * Math.PI / 180);
		double y = strength * Math.sin(viewHeading * Math.PI / 180);
		setThrust(x, y);
	}
	protected void changeViewHeading(double h){//This will be called by the neural network to change the current view heading
		viewHeading += h;
	}
	protected void updateHealth(double h){
		health += h;
		health = Math.min(1, health);
	}

	protected void updateFitness(double value) {
		fitness += value;
	}

	//returns the distance of the closest object in a specified segment, -1 if none found.
	public double viewingObjectOfTypeInSegment(int segment, int type){
		ArrayList<SightInformation> filtered = new ArrayList<SightInformation>();

		for(SightInformation si : canSee){ //filter out those objects of type that are in the specified segment
			if(si.getType() == type && si.getDistanceFromLower() >= ((double)segment / configNumSegments) && si.getDistanceFromLower() < ((segment+1.0) / configNumSegments)){
				filtered.add(si);
			}
		}
		if(filtered.size() == 0) return -1;

		double dist = Double.MAX_VALUE;
		for(SightInformation si : filtered){
			dist = Math.min(dist, si.getDistance());
		}

		return dist / visionRange;
	}

	public void stop(){
		speed.x = 0;
		speed.y = 0;
	}
	public void setX(int x){
		coords.x = x;
	}
	public void setY(int y){
		coords.y = y;
	}

	public double getHealth(){return health;}
	public double getViewHeading(){return viewHeading;}
	public double getVisionRange(){return visionRange;}
	public ArrayList<SightInformation> getAllViewingObjects(){
		return canSee;
	}
	int getNumSegments(){return configNumSegments;}
	public double getFOV(){return fov;}
	public double getChangeX(){return speed.x;}
	public double getChangeY(){return speed.y;}
	public double getMovingAngle(){
		double angle = 0;
		if(getChangeX() == 0){
			if(getChangeY() < 0) angle = -90;
			else angle = 90;
		}else angle = Math.atan((getChangeY()) / (getChangeX())) * 180.0 / Math.PI;

		if(getChangeX() > 0) {
			if (getChangeY() < 0) angle = 360 + angle;
		}else{
			if (getChangeY() >= 0) angle = 180 + angle;
			else angle += 180;
		}
		return angle;
	}
	public double getMovingSpeed(){
		return Math.sqrt(Math.pow((float) (getChangeX()), 2) + Math.pow((float) (getChangeY()), 2));
	}
	MNetwork getNetwork(){
		return mnetwork;
	}
	NInterface getInterface(){
		return ninterface;
	}
	
	public int getSpeciesId() {
		return genome.getSpeciesId();
	}

	public int getAge() {
		return this.age;
	}
}
