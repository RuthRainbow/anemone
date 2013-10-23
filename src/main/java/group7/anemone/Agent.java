package group7.anemone;

import java.awt.geom.Point2D;

import processing.core.PApplet;

public class Agent {
	PApplet parent;
	private double x = 100;
	private double y = 100;
	private Point2D.Double speed = new Point2D.Double(0, 0);
	private Point2D.Double thrust = new Point2D.Double(0, 0);
	private double acceleration;
	private Point2D.Double drag = new Point2D.Double(0, 0);;

	Agent(int a, int b, PApplet p){
		parent = p;
		x = a; y = b;
		speed = new Point2D.Double(1, 0);

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
	void update(){
		updateSpeed();

		x += speed.x;
		y += speed.y;

		if(x > parent.width + 10) x = -10;
		if(y > parent.height + 10) y = -10;
	}

	void setThrust(double x, double y){
		thrust.x = x; 
		thrust.y = y;
	}
	int getX(){return (int) x;}
	int getY(){return (int) y;}
}