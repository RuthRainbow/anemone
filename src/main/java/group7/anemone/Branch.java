package group7.anemone;

import java.awt.geom.Point2D;

public class Branch extends SimulationObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[] directionVector;
	private Point2D.Double currentPoint;
	private Point2D.Double root;
	private double maxSize;
	private Environment env;
	
	Branch(Point2D.Double coords, double[] directionVector, Point2D.Double root, Environment env){
		super(coords);
		this.directionVector = directionVector;
		this.currentPoint = coords;
		this.root = root;
		this.maxSize = 5;
		this.env = env;
	}
	public void grow() {
		if(Math.random() < 0.4) maxSize++;
		if(Math.random() < 0.001) maxSize = 0;
		double dist = currentPoint.distance(root);
		if(dist  > maxSize) currentPoint = root;
		if(env.foodsize() == 0 || Math.random() < (0.5/env.foodsize())){
			if(env.checkFood(currentPoint)){
				Point2D.Double nextPoint = new Point2D.Double(currentPoint.x + directionVector[0]*5,currentPoint.y + directionVector[1]*5);
				env.addFood(nextPoint);
				currentPoint = nextPoint;
			} else {
				env.addFood(currentPoint);
			}
			if(Math.random() < 0.05 && directionVector[0] < 1){
				directionVector[0]+= Math.random();
			}
			if(Math.random() < 0.05 && directionVector[0] > -1){
				directionVector[0]-= Math.random();
			}
			if(Math.random() < 0.05 && directionVector[1] < 1){
				directionVector[1]+= Math.random();
			}
			if(Math.random() < 0.05 && directionVector[1] > -1){
				directionVector[1]-= Math.random();
			}
		}

		
	}
}