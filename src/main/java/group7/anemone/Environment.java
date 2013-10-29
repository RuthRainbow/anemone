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

	public Environment(PApplet p){
		this.parent = p;
		this.god = new God(this);
		this.fishes = new ArrayList<Agent>();
		this.sharks = new ArrayList<Agent>();
		this.food = new ArrayList<Food>();
	}

    // Method to get all collisions that occurred in the environment
    protected ArrayList<Collision<Agent, Object>> GetCollisions() {
    	throw new NotImplementedException();
    }

    // Method to get collisions for a specific agent
    protected Collision<Agent, Object> GetCollision(Agent agent) {
    	throw new NotImplementedException();
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

// Class to hold a collision between an agent and an object, which could be another agent or a wall.
// (If we have breeding on collision, another agent vs wall matters).
abstract class Collision<Agent, Object> {}

// I made this class so nothing is abstract and so we can find stuff still to implement easily.
class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotImplementedException(){}
}