package group7.anemone;

import java.awt.geom.Point2D;
import java.io.Serializable;


public class SimulationObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1887973287289968272L;
	protected Point2D.Double coords;
	
	SimulationObject(Point2D.Double coords) {
		this.coords = coords;
	}
	
	int getX(){return (int) coords.x;}
	int getY(){return (int) coords.y;}
	Point2D.Double getCoordinates(){return coords;}
}
