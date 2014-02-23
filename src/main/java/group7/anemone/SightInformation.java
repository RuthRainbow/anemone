package group7.anemone;

import java.io.Serializable;

public class SightInformation extends Collision implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -944300220441096787L;
	private double distance;
	private double distanceFromLower;
	
	public SightInformation(Agent ag, SimulationObject ob, double dist, double distlow) {
		super(ag, ob);
		
		distance = dist;
		distanceFromLower = distlow;
	}

	public double getDistance(){
		return distance;
	}
	public double getNormalisedDistance(){
		return distance / getAgent().getVisionRange();
	}
	public double getDistanceFromLower(){
		return distanceFromLower;
	}
}
