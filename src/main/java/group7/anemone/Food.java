package group7.anemone;

import java.awt.geom.Point2D;

public class Food {
	private Point2D.Double coords = new Point2D.Double(100, 100);
	int value = 10;

	Food(Point2D.Double coords){
		this.coords = coords;
	}

	int getX(){return (int) coords.x;}
	int getY(){return (int) coords.y;}
	int getValue(){return value;}
}
