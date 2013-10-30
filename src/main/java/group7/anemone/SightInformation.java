package group7.anemone;

public class SightInformation extends Collision{
	private double distance;
	
	public SightInformation(Agent ag, Object ob, double dist) {
		super(ag, ob);
		
		distance = dist;
	}

	public double getDistance(){
		return distance;
	}
}
