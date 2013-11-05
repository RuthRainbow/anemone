package group7.anemone;

import java.awt.geom.Point2D;

public class Food extends SimulationObject{
	double value = 0.1;

	Food(Point2D.Double coords){
		super(coords);
	}

	double getValue(){return value;}
	
}
