package group7.anemone;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class Food extends SimulationObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6501032484260342122L;
	double value = 0.1;
	public double size = 2;
	public boolean positionupdated = true;
	
	Food(Point2D.Double coords){
		super(coords);
	}

	double getValue(){return value;}
	
}
