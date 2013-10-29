package group7.anemone;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import processing.core.PApplet;

public class Environment {

	PApplet parent;
	private God god;
	private ArrayList<Agent> fishes;
	private ArrayList<Agent> sharks;
	private ArrayList<Food> food;
	private int tick = 0;
	private ArrayList<Collision> collisions;

	public Environment(PApplet p){
		this.parent = p;
		this.god = new God(this);
		this.fishes = new ArrayList<Agent>();
		this.sharks = new ArrayList<Agent>();
		this.food = new ArrayList<Food>();
	}

    // Method to get all collisions that occurred in the environment
    public ArrayList<Collision> updateCollisions() {
    	collisions = new ArrayList<Collision>();
    	
    	for (Agent ag: fishes) { //for each agent, check for any collision
    		
    		for (Agent aa: fishes) { // check if collides to any other agent
        		if(ag == aa) continue;
        		
        		if(ag.getCoordinates().distance(aa.getCoordinates()) <= 20){
        			collisions.add(new Collision(ag, aa));
        		}
    		}
    		
    		for (Food fd: food) { //check collisions to food
        		if(ag.getCoordinates().distance(fd.getCoordinates()) <= 12){
        			collisions.add(new Collision(ag, fd));
        		}
    		}
		}
    	
    	return collisions;
    }
    
    protected ArrayList<Collision> getCollisions(){
    	return collisions;
    }

    // Method to get collisions for a specific agent
    protected ArrayList<Collision> GetCollision(Agent agent) {
    	ArrayList<Collision> result = new ArrayList<Collision>();
    	
    	for (Collision cc: result) {
    		if(cc.getAgent() == agent){
    			result.add(cc);
    		}
		}
    	
    	return result;
    }

	protected void updateAllAgents(){
		for (Agent fish: fishes) { //drawing the ikkle fishes
			fish.update();
		}
		for (Agent shark:sharks) {
			shark.update();
		}
		tick++;
		/* Method not implemented yet:
		if (tick % 5 == 0) {
			god.BreedPopulation(fishes);
			god.BreedPopulation(sharks);
			// Reset tick (in case of overflow)
			tick = 0;
		}*/
	}

	protected void addFish(Point2D.Double coords){
		fishes.add(new Agent(coords, parent));
	}

	protected void addShark(Point2D.Double coords) {
		sharks.add(new Agent(coords, parent));
	}

	void addFood(Point2D.Double coords){
		food.add(new Food(coords));
	}
	
	protected void removeAgent(Agent ag){
		fishes.remove(ag);
	}
	protected void removeFood(Food fd){
		food.remove(fd);
	}

	protected ArrayList<Agent> getAllFishes(){
		return fishes;
	}

	protected ArrayList<Agent> getAllSharks() {
		return sharks;
	}

	protected ArrayList<Agent> getAllAgents() {
		// We know the clone of fishes will be of type ArrayList<Agent>, so this cast is safe.
	    @SuppressWarnings("unchecked")
		ArrayList<Agent> all_agents = (ArrayList<Agent>) fishes.clone();
	    all_agents.addAll(sharks);
	    return all_agents;
	}

	protected ArrayList<Food> getAllFood(){
		return food;
	}
}

// I made this class so nothing is abstract and so we can find stuff still to implement easily.
class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotImplementedException(){}
}