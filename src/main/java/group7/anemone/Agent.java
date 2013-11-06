package group7.anemone;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import processing.core.PApplet;

public class Agent extends SimulationObject{
	PApplet parent;
	private Point2D.Double speed = new Point2D.Double(0, 0);
	private Point2D.Double thrust = new Point2D.Double(0, 0);
	private Point2D.Double drag = new Point2D.Double(0, 0);
	private String stringRep = "";
	private double fitness = 0;
	private double health = 1;
	private double viewHeading = 0; // in degrees 0-360
	private double visionRange = 100; //how far they can see into the distance
	private double fov = 25; //field of view, +-
	private ArrayList<SightInformation> canSee;
	private BPNetwork network;
	
	private int configNumSegments = 10;

	Agent(Point2D.Double coords, PApplet p) {
		super(coords);
		this.parent = p;
		thrust(1);
	}
	
	Agent(Point2D.Double coords, double viewHeading, PApplet p) {
		super(coords);
		this.parent = p;
		this.viewHeading = viewHeading;
		thrust(1);
		
		createNetwork();
	}
	
	// TODO make this so we can create a new agent from a string rep.
	public Agent(String string) {
		super(new Point2D.Double(0, 0));
		this.stringRep = string;
	}
	
	protected String getStringRep() {
		return this.stringRep;
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
	
	void createNetwork(){
		network = new BPNetwork(31,3,1);
		network.generateOperationQ();
	}
	
	
	void update(){
		updateSpeed();

		//TODO Move the change of coords to the update speed section?? -Seb
		coords.x += speed.x;	//Changes the coordinates to display distance travelled since last update
		coords.y += speed.y;	
		
		health -= 0.0000001;
	}

	void updateCanSee(ArrayList<SightInformation> see){
		canSee = see;
	}
	ArrayList<SightInformation> getCanSee(){return canSee;}
	
	private void setThrust(double x, double y){
		//This will be called by the neural network to 
		thrust.x = x;
		thrust.y = y;
	}
	void thrust(double strength){
		double x = strength * Math.cos(viewHeading * Math.PI / 180);
		double y = strength * Math.sin(viewHeading * Math.PI / 180);
		setThrust(x, y);
	}
	void changeViewHeading(double h){//This will be called by the neural network to change the current view heading
		viewHeading += h;
	}
	void updateHealth(double h){
		health += h;
		health = Math.min(1, health);
	}
	
	//returns the distance of the closest object in a specified segment, -1 if none found.
	public double viewingObjectOfTypeInSegment(int segment, int type){ 
		ArrayList<SightInformation> filtered = new ArrayList<SightInformation>();
		
		for(SightInformation si : canSee){ //filter out those objects of type that are in the specified segment
			if(si.getType() == type && si.getDistanceFromLower() >= (segment / configNumSegments) && si.getDistanceFromLower() < ((segment+1.0) / configNumSegments)){
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
	
	double getHealth(){return health;}
	double getViewHeading(){return viewHeading;}
	double getVisionRange(){return visionRange;}
	double getFOV(){return fov;}
}
