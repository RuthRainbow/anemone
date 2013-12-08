package group7.anemone;

import group7.anemone.UI.Utilities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import processing.core.PApplet;

public class Environment implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 2740658645450395424L;
	transient PApplet parent;
	// God & clock needed for breeding every n generations
	private God fishGod;
	private God sharkGod;
	private int tick = 0;
	private ArrayList<Agent> fishes;
	private ArrayList<Agent> sharks;
	private ArrayList<Food> food;
	private ArrayList<Wall> wall;

	private ArrayList<Collision> collisions;
	
	int width = 1500;
	int height = 1500;

	public Environment(PApplet p){
		this.parent = p;
		this.fishGod = new God();
		this.sharkGod = new God();
		this.fishes = new ArrayList<Agent>();
		this.sharks = new ArrayList<Agent>();
		this.food = new ArrayList<Food>();
		this.wall = new ArrayList<Wall>();
	}

    // Method to get all collisions that occurred in the environment
    public ArrayList<Collision> updateCollisions() {
    	collisions = new ArrayList<Collision>();

    	for (Agent ag: getAllAgents()) { //for each agent, check for any collision

    		for (Agent aa: getAllAgents()) { // check if collides to any other agent
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
    	for (Agent ag: getAllAgents()) {

    		ArrayList<SightInformation> result = new ArrayList<SightInformation>();

    		//check for objects within FOV
    		result.addAll(checkFOV(new ArrayList<SimulationObject>(food),ag));
    		result.addAll(checkFOV(new ArrayList<SimulationObject>(fishes),ag));
    		result.addAll(checkFOV(new ArrayList<SimulationObject>(sharks),ag));

    		result.addAll(checkFOVWalls(wall,ag));

    		//return updated list
    		ag.updateCanSee(result);
		}
    }


    private ArrayList<SightInformation> checkFOVWalls( ArrayList<Wall> walls, Agent ag) {

    	ArrayList<SightInformation> result = new ArrayList<SightInformation>();
		for(Wall wl : walls){
			//check if the wall is within the agent's viewable distance
			if (wl.getLine().ptLineDist(ag.getCoordinates()) < ag.getVisionRange()){

				double increment = (ag.getFOV()*2)/ag.getNumSegments();
				double headBelow = ag.getViewHeading() - ag.getFOV();
				//get the lines which make up the first segment
				Line2D.Double currentSegmentLine1 = Utilities.generateLine(ag.getCoordinates(),ag.getVisionRange(),headBelow);
				Line2D.Double currentSegmentLine2 = Utilities.generateLine(ag.getCoordinates(),ag.getVisionRange(),(headBelow + increment));

				for(int i=1;i<=ag.getNumSegments();i++){
					//check if the wall intersects both lines indicating it passes through
					boolean intersectsLine1 = currentSegmentLine1.intersectsLine(wl.getLine());
					boolean intersectsLine2 = currentSegmentLine2.intersectsLine(wl.getLine());

					if(intersectsLine1 && intersectsLine2){
						//find the intersection points for each line with the wall
						Point2D.Double lineIntersection1 = Utilities.findIntersection(currentSegmentLine1, wl.getLine());
						Point2D.Double lineIntersection2 = Utilities.findIntersection(currentSegmentLine2, wl.getLine());
						//get the midpoint
						Point2D.Double midPoint = new Point2D.Double((lineIntersection1.getX()+lineIntersection2.getX())/2,(lineIntersection1.y+lineIntersection2.y)/2);
						//now it is simply a point pass to checkObject method
						SightInformation temp = checkObject(new Wall(midPoint,midPoint),ag);
						if(temp != null) result.add(temp);
						//NB creating a wall object here so that the object type is stored

					}
					//generate lines for next segment
					currentSegmentLine1 = currentSegmentLine2;
					currentSegmentLine2 = Utilities.generateLine(ag.getCoordinates(), ag.getVisionRange(), headBelow + increment * (i+1));
				}

			}

		}

		return result;
	}

	private ArrayList<SightInformation> checkFOV(ArrayList<SimulationObject> objects, Agent ag) {
    	ArrayList<SightInformation> result = new ArrayList<SightInformation>();

		for (SimulationObject ob : objects) {
			SightInformation temp = checkObject(ob,ag);
			if(temp!= null)
			result.add(temp);
		}
		return result;
	}

	private SightInformation checkObject(SimulationObject ob, Agent ag) {

    	//angle of the top and bottom of the agent's field of view
		double headBelow = ag.getViewHeading() - ag.getFOV();
		double headAbove = ag.getViewHeading() + ag.getFOV();
		//check if the object is within viewable distance
		double distance = ag.getCoordinates().distance(ob.getCoordinates());
		if(distance <= ag.getVisionRange()){
			//get angle of object in relation to agent
			double angleBetween = Math.atan((ob.getCoordinates().y - ag.getCoordinates().y) / (ob.getCoordinates().x - ag.getCoordinates().x));
			angleBetween = angleBetween * 180 / Math.PI;
			//adjust angles depending on quadrant to be represented in 0-360 rather than -180-180
			if(ob.getX() > ag.getX()) {
				if (ob.getY() < ag.getY()) angleBetween = 360 + angleBetween;
			}else{
				if (ob.getY() >= ag.getY()) angleBetween = 180 + angleBetween;
				else angleBetween += 180;
			}
			//check if the object falls within field of view
			if(angleBetween >= headBelow && angleBetween <= headAbove){
				return(new SightInformation(ag, ob, distance, (angleBetween - headBelow) / (ag.getFOV() * 2)));

			//special cases where field of view crosses 0/360 divide
			}else if(headBelow < 0){
				if(angleBetween >= 360 + headBelow) {
					if(ob instanceof Wall) {
    				}
					return(new SightInformation(ag, ob, distance, ((angleBetween <= headAbove ? angleBetween + 360 : angleBetween ) - (360 + headBelow)) / (ag.getFOV() * 2)));
				}
			}else if(headAbove > 360){
				if(angleBetween <= headAbove - 360) {
					if(ob instanceof Wall) {
    				}
					return(new SightInformation(ag, ob, distance, ((angleBetween <= headAbove-360 ? angleBetween + 360 : angleBetween ) - headBelow) / (ag.getFOV() * 2)));
				}
			}

		}
		return null;
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

    	if (tick % 200 == 0 ) {
    		if (tick % 800 == 0) {
    			ArrayList<Gene[]> nextSharks = sharkGod.BreedWithSpecies(sharks);
    			for (Gene[] gene : nextSharks) {
    				// TODO unhardcode these
    				int x = (int) Math.floor(Math.random() * width);
    				int y = (int) Math.floor(Math.random() * height);
    				int heading = (int) Math.floor(Math.random() * 360);
    				spawnShark(new Point2D.Double(x,y), heading, gene);
    				
    			}
    			// Reset tick until next generation
    			tick = 0;
    		}
    		ArrayList<Gene[]> nextFish = fishGod.BreedWithSpecies(fishes);
    		for (Gene[] gene : nextFish) {
    			// TODO unhardcode these
    			int x = (int) Math.floor(Math.random() * width);
    			int y = (int) Math.floor(Math.random() * height);
    			int heading = (int) Math.floor(Math.random() * 360);
    			spawnFish(new Point2D.Double(x,y), heading, gene);
    		}

    		for(int i = 0; i < fishes.size()/2; i++){
    			int x = (int) Math.floor(Math.random() * width);
    			int y = (int) Math.floor(Math.random() * height);
    			addFood(new Point2D.Double(x, y));
    		}

    	}
    	
    	if(fishes.size() <= 5) {
       		ArrayList<Gene[]> nextFish = fishGod.BreedWithSpecies(fishes);
    		for (Gene[] gene : nextFish) {
    			// TODO unhardcode these
    			int x = (int) Math.floor(Math.random() * width);
    			int y = (int) Math.floor(Math.random() * height);
    			int heading = (int) Math.floor(Math.random() * 360);
    			spawnFish(new Point2D.Double(x,y), heading, gene);
    			tick = 0;
    		}    	
    		System.out.println("Fish population got to small, breeding.");
    	}
    	if(sharks.size() <= 5) {
  			ArrayList<Gene[]> nextSharks = sharkGod.BreedWithSpecies(sharks);
			for (Gene[] gene : nextSharks) {
				// TODO unhardcode these
				int x = (int) Math.floor(Math.random() * width);
				int y = (int) Math.floor(Math.random() * height);
				int heading = (int) Math.floor(Math.random() * 360);
				spawnShark(new Point2D.Double(x,y), heading, gene);
				// Reset tick until next generation
				tick = 0;
			}
			System.out.println("Shark population got to small, breeding.");
    	}
    }

	protected void spawnFish(Point2D.Double coords, int heading, Gene[] newGenome) {
		fishes.add(new Agent(coords, heading, parent, newGenome));
	}

	protected void spawnShark(Point2D.Double coords, int heading, Gene[] newGenome) {
		sharks.add(new Enemy(coords, heading, parent, newGenome));
	}

	protected void addFish(Point2D.Double coords, int heading){
		Gene[] genome = getGenome();

		//Creates an agent with a generic genome for a network that has no hidden nodes
		fishes.add(new Agent(coords, heading, parent, genome));
	}

	private Gene[] getGenome() {
		/**
		 * FULL GENOME FOR INITIAL AGENT
		 * GENE PARAMETERS:
		 * Historical Marker
		 * Pre Node
		 * Post Node
		 * Weight
		 * Delay
		 *
		 * */
		Gene[] genome = new Gene[3 * 3 * Agent.configNumSegments];
		int total = 0;
		
		for(int i = 0; i < Agent.configNumSegments; i++){//Food
			for(int j = 0; j < 3; j++){
				genome[total] = new Gene(total, 3 + i, j, 4.0, 1);
				total++;
			}
		}
		for(int i = 0; i < Agent.configNumSegments; i++){//Wall
			for(int j = 0; j < 3; j++){
				genome[total] = new Gene(total, 3 + i + Agent.configNumSegments, j, 4.0, 1);
				total++;
			}
		}
		for(int i = 0; i < Agent.configNumSegments; i++){//Enemy
			for(int j = 0; j < 3; j++){
				genome[total] = new Gene(total, 3 + i + Agent.configNumSegments * 2, j, 4.0, 1);
				total++;
			}
		}

		return genome;
	}

	protected void addShark(Point2D.Double coords, int heading){
		Gene[] genome = getGenome();
		//Creates an agent with a generic genome for a network that has no hidden nodes
		sharks.add(new Enemy(coords, heading, parent, genome));
	}

	void addFood(Point2D.Double coords){
		food.add(new Food(coords));
	}

	void addWall(Point2D.Double start, Point2D.Double end){
		wall.add(new Wall(start, end));
	}

	protected void removeAgent(Agent ag){
		if(ag instanceof Enemy) sharks.remove(ag);
		else fishes.remove(ag);
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
	protected ArrayList<Wall> getAllWalls(){
		return wall;
	}

	protected void Breed(Agent mother, Agent father) {
		/*ArrayList<Gene[]> children = god.CreateOffspring(mother, father);
		for (Gene[] child : children) {
			//TODO add new agent to env.
		}*/
	}

	public void killOutsideAgents(double width, double height) {
		for(Agent fish: getAllAgents()){
			if(fish.coords.x < 0 || fish.coords.x > width || fish.coords.y < 0 || fish.coords.y > height){
				fish.updateHealth(-1);
			}
		}

	}

}