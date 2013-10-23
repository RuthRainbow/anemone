package group7.anemone;

import java.util.ArrayList;

import processing.core.PApplet;

public class Environment {

	/*// The list of all fish, sharks and food in the environment
	ArrayList<Agent> fishes;
	ArrayList<Agent> sharks;
	ArrayList<Coordinates> food;

    // Method to get all collisions that occurred in the environment
    abstract ArrayList<Collision<Agent, Object>> GetCollisions();

    // Method to get collisions for a specific agent
    abstract Collision<Agent, Object> GetCollision(Agent agent);

    // Method to get the move for a particular agent
    abstract void AgentTurn(Agent agent);

    // Method to add an agent to the environment
    abstract void AddAgent(Agent agent);

    // Move all agents (called by the God)
    void MoveAll() {
    	for (Agent fish: fishes) {
    		AgentTurn(fish);
    	}
    	for (Agent shark: sharks) {
    		AgentTurn(shark);
    	}
    }*/

	PApplet parent;
	private ArrayList<Agent> agents = new ArrayList();
	private ArrayList<Food> food = new ArrayList();

	Environment(PApplet p){
		parent = p;
	}

	void updateAllAgents(){
		for(int i = 0; i < agents.size(); i++){ //drawing the ikkle agents
			Agent ag = agents.get(i);
			ag.update();
		}
	}

	void addAgent(int x, int y){
		agents.add(new Agent(x, y, parent));
	}
	void addFood(int x, int y){
		food.add(new Food(x, y));
	}

	ArrayList<Agent> getAllAgents(){
		return agents;
	}
	ArrayList<Food> getAllFood(){
		return food; 
	}
}

// Class to hold a collision between an agent and an object, which could be another agent or a wall.
// (If we have breeding on collision, another agent vs wall matters).
abstract class Collision<Agent, Object> {}

// A class to hold coordinates (e.g. for food).
class Coordinates {
	protected float x, y;

	public Coordinates(float x, float y) {
		this.x = x;
		this.y = y;
	}
}