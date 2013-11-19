package group7.anemone;

import group7.anemone.MNetwork.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import processing.core.PApplet;

public class Agent extends SimulationObject{
	PApplet parent;
	private Point2D.Double speed = new Point2D.Double(0, 0);
	private Point2D.Double thrust = new Point2D.Double(0, 0);
	private Point2D.Double drag = new Point2D.Double(0, 0);
	private double fitness = 0;
	private double health = 1;
	private int age = 0; // Age of agent in number of updates.
	private double viewHeading = 0; // in degrees 0-360
	private double visionRange = 100; //how far they can see into the distance
	private double fov = 25; //field of view, +-
	private ArrayList<SightInformation> canSee;
	private MSimulation netSim;

	/*
	 * GENOME LAYOUT:
	 * 		* Each index is an individual gene
	 * 		* Each gene has 4 values.
	 * 		* VALUE 1: Historical Marker (Not very biological, maybe we can change how this part works later.
	 * 		* VALUE 2: Node where a link originates from
	 * 		* VALUE 3: Node that a link connects to
	 * 		* VALUE 4: Enabled/disabled gene
	 */
	private int[][] genome;

	private int configNumSegments = 10;

	private MNetwork mnetwork;
	private MSimulation msimulation;
	private NInterface ninterface;

	public Agent(Point2D.Double coords, double viewHeading, PApplet p, int[][] newGenome) {
		super(coords);
		ninterface = new NInterface(10);
		canSee = new ArrayList<SightInformation>();
		this.parent = p;
		this.viewHeading = viewHeading;
		thrust(1);
		this.genome=newGenome;
		constructNetwork();
		createSimpleNetwork();
	}

	public Agent(int[][] newGenome) {
		super(new Point2D.Double());
		this.genome = newGenome;
	}

	public void constructNetwork() {
		MSimulationConfig config = new MSimulationConfig();
		ArrayList<MNeuron> neurons = new ArrayList<MNeuron>();
		ArrayList<MSynapse> synapses = new ArrayList<MSynapse>();

		config.eventHorizon=100;

		for (int x=0; x<genome.length; x++) { //For every gene in the genome
			if (genome[x][3]==1) {	//If the gene is active...
				//Create node described at index 1
				//Create node described at index 2
				//Create link between pre and post nodes
			}
		}
		MNetwork network = new MNetwork(neurons,synapses);
		netSim = new MSimulation(network, config); //Create the simulator with this network class
	}

	public void stepNetwork() {
		netSim.step();
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

		/* Set the neurons to start in a resting state. */
		nstate.v = -65.0;
		nstate.u = 0.0;
		nstate.I = 0.0;

		/* Create the neurons. */
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

		/* Create the synapses. */
		MSynapse ss1 = new MSynapse(sn1, mn, 4.0, 1);
		MSynapse ss2 = new MSynapse(sn2, mn, 4.0, 1);
		MSynapse ssL = new MSynapse(snL, mnL, 4.0, 1);
		MSynapse ssR = new MSynapse(snR, mnR, 4.0, 1);

		synapses.add(ss1);
		synapses.add(ss2);
		synapses.add(ssL);
		synapses.add(ssR);

		/* This should probably be done by MNetwork. */
		sn1.getPostSynapses().add(ss1);
		sn2.getPostSynapses().add(ss2);
		snL.getPostSynapses().add(ssL);
		snR.getPostSynapses().add(ssR);
		mn.getPreSynapses().add(ss1);
		mn.getPreSynapses().add(ss2);
		mnL.getPreSynapses().add(ssL);
		mnR.getPreSynapses().add(ssR);

		/* Create the network. */
		this.mnetwork = new MNetwork(neurons, synapses);

		/* Set the simulation configuration parameters. */
		simConfig.eventHorizon = 20;

		/* Create the simulation. */
		this.msimulation = new MSimulation(this.mnetwork, simConfig);
	}

	private void updateMNetwork() {
		ArrayList<MNeuron> neurons = mnetwork.getNeurons();

		/* Apply inputs from sensors to network. */
		for (MNeuron n : neurons) {
			switch(n.getID()) {
				/* Sensory neurons. */
				case 0:
					//System.out.println("Adding current "+ninterface.affectors.vFood[4]);
					n.addCurrent(ninterface.affectors.vFood[4]);
					break;
				case 1:
					//System.out.println("Adding current "+ninterface.affectors.vFood[5]);
					n.addCurrent(ninterface.affectors.vFood[5]);
					break;
				case 2:
					System.out.println("Adding current "+ninterface.affectors.vFood[0]);
					n.addCurrent(ninterface.affectors.vFood[0]);
					break;
				case 3:
					System.out.println("Adding current "+ninterface.affectors.vFood[9]);
					n.addCurrent(ninterface.affectors.vFood[9]);
					break;
				/* Motor neuron. */
				case 4:
					if (n.isFiring()) {
						thrust(2);
					}
					break;
				case 5:
					if (n.isFiring()) {
						changeViewHeading(-20.0);
					}
					break;
				case 6:
					if (n.isFiring()) {
						changeViewHeading(20.0);
					}
					break;
				default:
					break;
			}
		}

		msimulation.step();
	}

	protected int[][] getStringRep() {
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
		}
	}

	void update(){
		updateSensors();
		updateMNetwork();
		updateSpeed();

		//TODO Move the change of coords to the update speed section?? -Seb
		coords.x += speed.x;	//Changes the coordinates to display distance travelled since last update
		coords.y += speed.y;

		age++;
		health -= 0.001;//0.0000001;
		if (age < 100) {
			fitness += 0.001;
		} else if (age > 200) {
			fitness -= 0.001;
		}
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
}
