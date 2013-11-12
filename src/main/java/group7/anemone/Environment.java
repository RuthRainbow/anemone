package group7.anemone;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import processing.core.PApplet;

public class Environment {

	PApplet parent;
	// God & clock needed for breeding every n generations
	private God god;
	private int tick = 0;
	private ArrayList<Agent> fishes;
	private ArrayList<Agent> sharks;
	private ArrayList<Food> food;
	private ArrayList<Wall> wall;

	private ArrayList<Collision> collisions;

	public Environment(PApplet p){
		this.parent = p;
		this.god = new God();
		this.fishes = new ArrayList<Agent>();
		this.sharks = new ArrayList<Agent>();
		this.food = new ArrayList<Food>();
		this.wall = new ArrayList<Wall>();
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
    		
    		for (Wall wl: wall) {
    			if (wl.getLine().ptLineDist(ag.getCoordinates()) < 10){
    				collisions.add(new Collision(ag, wl));
    			}
    		}
		}
    	
    	return collisions;
    }
    
    public void updateAgentsSight() {
    	//update what each agent can see
    	for (Agent ag: fishes) { 
    		ArrayList<SightInformation> result = new ArrayList<SightInformation>();
    		//angle of the top and bottom of the agent's field of view
    		double headBelow = ag.getViewHeading() - ag.getFOV();
    		double headAbove = ag.getViewHeading() + ag.getFOV();
    		
    		//TODO: move this into method and perform for food, other agents and enemies
    		//check for food within FOV
    		for (Food fd : food) { 
        		//check if the food is within viewable distance
        		double distance = ag.getCoordinates().distance(fd.getCoordinates());
        		if(distance <= ag.getVisionRange()){
        			//get angle of food in relation to agent
        			double angleBetween = Math.atan((fd.getCoordinates().y - ag.getCoordinates().y) / (fd.getCoordinates().x - ag.getCoordinates().x));
        			angleBetween = angleBetween * 180 / Math.PI;
        			//adjust angles depending on quadrant to be represented in 0-360 rather than -180-180
        			if(fd.getX() > ag.getX()) {
        				if (fd.getY() < ag.getY()) angleBetween = 360 + angleBetween;
        			}else{
        				if (fd.getY() >= ag.getY()) angleBetween = 180 + angleBetween;
        				else angleBetween += 180;
        			}    
        			//check if the food falls within field of view
        			if(angleBetween >= headBelow && angleBetween <= headAbove){
        				result.add(new SightInformation(ag, fd, distance, (angleBetween - headBelow) / (ag.getFOV() * 2)));
        			//special cases where field of view crosses 0/360 divide	
        			}else if(headBelow < 0){
        				if(angleBetween >= 360 + headBelow) result.add(new SightInformation(ag, fd, distance, ((angleBetween <= headAbove ? angleBetween + 360 : angleBetween ) - (360 + headBelow)) / (ag.getFOV() * 2)));
        			}else if(headAbove > 360){
        				if(angleBetween <= headAbove - 360) result.add(new SightInformation(ag, fd, distance, ((angleBetween <= headAbove-360 ? angleBetween + 360 : angleBetween ) - headBelow) / (ag.getFOV() * 2)));
        			}
        			
        		}
    		}
    		/*
    		//check for other agents in FOV
    		for (Agent fi : fishes) { 
        		
        		double distance = ag.getCoordinates().distance(fi.getCoordinates());
        		if(distance <= ag.getVisionRange()){
        			double angleBetween = Math.atan((fi.getCoordinates().y - ag.getCoordinates().y) / (fi.getCoordinates().x - ag.getCoordinates().x));
        			angleBetween = angleBetween * 180 / Math.PI;
        			
        			if(fi.getX() > ag.getX()) {
        				if (fi.getY() < ag.getY()) angleBetween = 360 + angleBetween;
        			}else{
        				if (fi.getY() >= ag.getY()) angleBetween = 180 + angleBetween;
        				else angleBetween += 180;
        			}        			
        			
        			if(angleBetween >= headBelow && angleBetween <= headAbove){
        				result.add(new SightInformation(ag, fi, distance));
        			}else if(headBelow < 0){
        				if(angleBetween >= 360 + headBelow) result.add(new SightInformation(ag, fi, distance));
        			}else if(headAbove > 360){
        				if(angleBetween <= headAbove - 360) result.add(new SightInformation(ag, fi, distance));
        			}
        			
        		}
    		}
    		
    		//check for other sharks in FOV
    		for (Agent sh : sharks) { 
        		
        		double distance = ag.getCoordinates().distance(sh.getCoordinates());
        		if(distance <= ag.getVisionRange()){
        			double angleBetween = Math.atan((sh.getCoordinates().y - ag.getCoordinates().y) / (sh.getCoordinates().x - ag.getCoordinates().x));
        			angleBetween = angleBetween * 180 / Math.PI;
        			
        			if(sh.getX() > ag.getX()) {
        				if (sh.getY() < ag.getY()) angleBetween = 360 + angleBetween;
        			}else{
        				if (sh.getY() >= ag.getY()) angleBetween = 180 + angleBetween;
        				else angleBetween += 180;
        			}        			
        			
        			if(angleBetween >= headBelow && angleBetween <= headAbove){
        				result.add(new SightInformation(ag, sh, distance));
        			}else if(headBelow < 0){
        				if(angleBetween >= 360 + headBelow) result.add(new SightInformation(ag, sh, distance));
        			}else if(headAbove > 360){
        				if(angleBetween <= headAbove - 360) result.add(new SightInformation(ag, sh, distance));
        			}
        			
        		}
    		}  		
    		*/
    		//return updated list
    		ag.updateCanSee(result);
		}
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

	protected void addFish(Point2D.Double coords, int heading){
		int[][] genome = new int[32][4];
		for (int first=0; first<32; first++) {
			genome[first][0] = first;
			genome[first][1] = first;
			genome[first][2] = 31;
			genome[first][3] = 1;
		}
		
		//Creates an agent with a generic genome for a network that has no hidden nodes
		fishes.add(new Agent(coords, heading, parent, genome));
	}

	void addFood(Point2D.Double coords){
		food.add(new Food(coords));
	}
	
	void addWall(Point2D.Double start, Point2D.Double end){
		wall.add(new Wall(start, end));
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