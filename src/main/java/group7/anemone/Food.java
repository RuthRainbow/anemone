package group7.anemone;

import java.awt.geom.Point2D;

public class Food {
	private Point2D.Double coords = new Point2D.Double(100, 100);
	double value = 0.1;

	Food(Point2D.Double coords){
		this.coords = coords;
	}

	int getX(){return (int) coords.x;}
	int getY(){return (int) coords.y;}
	double getValue(){return value;}
	Point2D.Double getCoordinates(){return coords;}
}
