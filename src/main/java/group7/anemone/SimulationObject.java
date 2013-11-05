package group7.anemone;

import java.awt.geom.Point2D;


public class SimulationObject {

	protected Point2D.Double coords;
	
	SimulationObject(Point2D.Double coords) {
		this.coords = coords;
	}
	
	int getX(){return (int) coords.x;}
	int getY(){return (int) coords.y;}
	Point2D.Double getCoordinates(){return coords;}
}
