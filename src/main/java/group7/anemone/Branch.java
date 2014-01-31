package group7.anemone;

import java.awt.geom.Point2D;

public class Branch extends SimulationObject{
	private double[] directionVector;
	private Point2D.Double currentPoint;
	private Point2D.Double root;
	private double maxSize;
	Branch(Point2D.Double coords, double[] directionVector, Point2D.Double root){
		super(coords);
		this.directionVector = directionVector;
		this.currentPoint = coords;
		this.root = root;
		this.maxSize = 5;
	}
	public void grow() {
		if(Math.random() < 0.1) maxSize++;
		if(currentPoint.distance(root) > maxSize) currentPoint = root;
		if(Math.random() < 0.1){
			if(Environment.checkFood(currentPoint)){
				Point2D.Double nextPoint = new Point2D.Double(currentPoint.x + directionVector[0]*3,currentPoint.y + directionVector[1]*3);
				Environment.addFood(nextPoint);
				currentPoint = nextPoint;
			} else {
				Environment.addFood(currentPoint);
			}
			if(Math.random() < 0.1 && directionVector[0] < 1){
				directionVector[0]++;
			}
			if(Math.random() < 0.1 && directionVector[0] > -1){
				directionVector[0]--;
			}
			if(Math.random() < 0.1 && directionVector[1] < 1){
				directionVector[1]++;
			}
			if(Math.random() < 0.1 && directionVector[1] > -1){
				directionVector[1]--;
			}
			
		}

		
	}
	


}
