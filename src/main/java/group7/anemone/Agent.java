package group7.anemone;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import processing.core.PApplet;

public class Agent {
	PApplet parent;
	private Point2D.Double coords;
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

	Agent(Point2D.Double coords, PApplet p){
		this.parent = p;
		this.coords = coords;
		this.speed = new Point2D.Double(1, 0);
	}
	Agent(Point2D.Double coords, double viewHeading, PApplet p){
		this.parent = p;
		this.coords = coords;
		this.speed = new Point2D.Double(1, 0);
		this.viewHeading = viewHeading;
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
	
	
	void updateNetwork(){
		//TODO Actually implement this
		//This will run through this agents network
		//Input sensory data
		//Update all the neurons
		//Fire syanpse, etc
		//Should result in some new outputs from outputlinks
	}
	
	void updateSensors(){
		//TODO Actually implement this
		/*
		 * Look at what can be seen
		 * Shunt it into neural network for processing next step?
		 */
	}
	
	void update(){
		//Will update the parameters of the agent
		//Parameters include:
		//Speed
		//Neural Network State
		//Sensor data (sight)
		updateNetwork();
		updateSpeed();

		//TODO Move the change of coords to the update speed secion?? -Seb
		coords.x += speed.x;	//Changes the coordinates to display disatance travelled since last update
		coords.y += speed.y;

		if(coords.x > parent.width + 10) coords.x = -10;	//f the agent goes off the corner of the map, move it to the other side.
		if(coords.y > parent.height + 10) coords.y = -10;
		
		health -= 0.001;
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
	
	int getX(){return (int) coords.x;}
	int getY(){return (int) coords.y;}
	Point2D.Double getCoordinates(){return coords;}
	double getHealth(){return health;}
	double getViewHeading(){return viewHeading;}
	double getVisionRange(){return visionRange;}
	double getFOV(){return fov;}
}
