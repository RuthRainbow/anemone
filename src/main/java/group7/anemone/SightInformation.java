package group7.anemone;

public class SightInformation extends Collision{
	private double distance;
	private double distanceFromLower;
	
	public SightInformation(Agent ag, Object ob, double dist, double distlow) {
		super(ag, ob);
		
		distance = dist;
		distanceFromLower = distlow;
	}

	public double getDistance(){
		return distance;
	}
	public double getDistanceFromLower(){
		return distanceFromLower;
	}
}
