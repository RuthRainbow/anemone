package group7.anemone;

import java.awt.geom.Point2D;

import processing.core.PApplet;

public class Agent {
	PApplet parent;
	private Point2D.Double coords;
	private Point2D.Double speed = new Point2D.Double(0, 0);
	private Point2D.Double thrust = new Point2D.Double(0, 0);
	private Point2D.Double drag = new Point2D.Double(0, 0);

	Agent(Point2D.Double coords, PApplet p){
		this.parent = p;
		this.coords = coords;
		this.speed = new Point2D.Double(1, 0);
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

		coords.x += speed.x;
		coords.y += speed.y;

		if(coords.x > parent.width + 10) coords.x = -10;
		if(coords.y > parent.height + 10) coords.y = -10;
	}

	void setThrust(double x, double y){
		thrust.x = x;
		thrust.y = y;
	}
	int getX(){return (int) coords.x;}
	int getY(){return (int) coords.y;}
}