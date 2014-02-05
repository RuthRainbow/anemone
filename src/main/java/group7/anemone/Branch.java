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
		this.maxSize = 50;
		this.env = env;
	}
	public void grow() {
		if(Math.random() < 0.5) maxSize = maxSize + Math.random();
		if(Math.random() < 0.01) maxSize = 50;
		double dist = currentPoint.distance(root);
		if(dist  > maxSize) currentPoint = root;
		if(env.foodsize() == 0 || Math.random() < (0.5/(env.foodsize()/100))){
			if(env.checkFood(currentPoint)){
				Point2D.Double nextPoint = new Point2D.Double(currentPoint.x + directionVector[0]*5,currentPoint.y + directionVector[1]*5);
				env.addFood(nextPoint);
				currentPoint = nextPoint;
			} else {
				env.addFood(currentPoint);
			}
			if(Math.random() < 0.2 && directionVector[0] < 1){
				directionVector[0]+= Math.random();
			}
			if(Math.random() < 0.2 && directionVector[0] > -1){
				directionVector[0]-= Math.random();
			}
			if(Math.random() < 0.2 && directionVector[1] < 1){
				directionVector[1]+= Math.random();
			}
			if(Math.random() < 0.2 && directionVector[1] > -1){
				directionVector[1]-= Math.random();
			}
		}

		if(currentPoint.distance(root) > 60 && Math.random() < (0.5/env.getSeaweedSize())){
			env.addSeaweed(currentPoint);
		}
		
	}
	
	/*public double getMaxSize(){
		return maxSize;
	}*/
}