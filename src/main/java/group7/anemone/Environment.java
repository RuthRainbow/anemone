package group7.anemone;

import group7.anemone.Genetics.FishGod;
import group7.anemone.Genetics.Genome;
import group7.anemone.Genetics.God;
import group7.anemone.Genetics.NeatEdge;
import group7.anemone.Genetics.NeatNode;
import group7.anemone.Genetics.SharkGod;
import group7.anemone.UI.Utilities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import processing.core.PApplet;

public class Environment implements Serializable{

	private static final long serialVersionUID = 2740658645450395424L;
	transient PApplet parent;
	// God & clock needed for breeding every n generations
	public God fishGod;
	private God sharkGod;
	private int tick = 0;
	private ArrayList<Agent> fishes;
	private ArrayList<Agent> sharks;
	private static ArrayList<Food> food;
	private ArrayList<Wall> wall;
	private ArrayList<Seaweed> seaweed;
	private static ArrayList<Point2D.Double> foodPos;

	private ArrayList<Collision> collisions;

	// Whether to not use health:
	protected final boolean fitnessOnly = true;
	//whether a fully connected network should be created.
	protected final boolean FLAG_CONNECT_ALL= true;

	static int width = 1000;
	static int height = 1000;

	//Save number of segments used by agent for analysis tool
	public int agentNumSegments;

	//JBox2D Variables
	World world;

	public Environment(PApplet p){
		this.parent = p;
		this.fishGod = new FishGod();
		this.sharkGod = new SharkGod();
		this.fishes = new ArrayList<Agent>();
		this.sharks = new ArrayList<Agent>();
		Environment.food = new ArrayList<Food>();
		this.wall = new ArrayList<Wall>();
		this.seaweed = new ArrayList<Seaweed>();
		this.foodPos = new ArrayList<Point2D.Double>();
		this.agentNumSegments = Agent.configNumSegments;
		
		//JBox2D
		Vec2 gravity = new Vec2(0.0f, 0.0f);
		this.world = new World(gravity);
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

	private Wall wallChecking;
    private ArrayList<SightInformation> checkFOVWalls( ArrayList<Wall> walls, Agent ag) {
    	ArrayList<SightInformation> result = new ArrayList<SightInformation>();
		for(Wall wl : walls){
			if(ag.getType() == wl.getWallType()) continue;

			wallChecking = wl;

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
						SightInformation temp = checkObject(new Wall(midPoint, midPoint, null), ag);
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

	private boolean lineIntersectsWall(Line2D.Double line, SimulationObject ignore, Agent ag){
		for(Wall wl : wall){
			if(ignore instanceof Wall && wallChecking == wl) continue;
			if(ag.getType() == wl.getWallType()) continue;

			if(wl.getLine().intersectsLine(line)) return true;
		}

		return false;
	}

	private SightInformation checkObject(SimulationObject ob, Agent ag) {

    	//angle of the top and bottom of the agent's field of view
		double headBelow = ag.getViewHeading() - ag.getFOV();
		double headAbove = ag.getViewHeading() + ag.getFOV();
		//check if the object is within viewable distance
		double distance = ag.getCoordinates().distance(ob.getCoordinates());

		Line2D.Double lineToObject = new Line2D.Double(ag.getCoordinates(), ob.getCoordinates());
		boolean intersectsWall = lineIntersectsWall(lineToObject, ob, ag);

		if(!intersectsWall && distance <= ag.getVisionRange()){
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
    	world.step(1.0f / 30.0f, 6, 3);
    	
    	for (Agent fish: fishes) { //drawing the ikkle fishes
    		fish.update();
    	}
    	for (Agent shark:sharks) {
    		shark.update();
    	}
    	tick++;
    	
    	if (tick % 1000 == 0) {
    		if (tick % 2000 == 0) {
    			ArrayList<Genome> nextSharks = sharkGod.BreedPopulation(sharks, fitnessOnly);
        		if (fitnessOnly) {
        			ArrayList<Agent> nextAgents = new ArrayList<Agent>();
        			for (int i = 0; i < sharks.size(); i++) {
        				Genome stringRep = sharks.get(i).getStringRep();
    	    			if (nextSharks.contains(stringRep)) {
    	    				nextSharks.remove(stringRep);
    	    				nextAgents.add(sharks.get(i));
    	    			}
    	    		}
        			sharks.clear();
        			sharks.addAll(nextAgents);
        		}
    			for (Genome genome : nextSharks) {
    				int x = (int) Math.floor(width*0.8 + Math.random() * width*0.2);
    				int y = (int) Math.floor(height*0.8 + Math.random() * height*0.2);
    				int heading = (int) Math.floor(Math.random() * 360);
    				spawnShark(new Point2D.Double(x,y), heading, genome);

    			}
    			// Reset tick until next generation
    			tick = 0;
    		}
    		ArrayList<Genome> nextFish = fishGod.BreedPopulation(fishes, fitnessOnly);

    		if (fitnessOnly) {
    			ArrayList<Agent> nextAgents = new ArrayList<Agent>();

    			for (int i = 0; i < fishes.size(); i++) {
    				Genome stringRep = fishes.get(i).getStringRep();
	    			if (nextFish.contains(stringRep)) {
	    				nextFish.remove(stringRep);
	    				nextAgents.add(fishes.get(i));
	    			}
	    		}
	    		fishes.clear();
	    		fishes.addAll(nextAgents);
    		}
    		for (Genome genome : nextFish) {
    			int x = (int) Math.floor(Math.random() * width*0.2);
    			int y = (int) Math.floor(Math.random() * height*0.2);
    			int heading = (int) Math.floor(Math.random() * 360);
    			spawnFish(new Point2D.Double(x,y), heading, genome);
    		}

    	}
    	if(tick % 50 == 0 && food.size() > 10){
    		food.remove(0);
    	}

    	while(seaweed.size() > 10){
    		seaweed.remove(0);
    	}
    	if(tick % 1000 == 0){
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(height*0.2 + Math.random() * height*0.6);
			addSeaweed(new Point2D.Double(x, y));
    	}

		for(int i = 0; i < seaweed.size(); i++){
			Seaweed sw = seaweed.get(i);
			if(Math.random() < 0.02) sw.update();
		}

    	if(fishes.size() <= 7) {
    		for (int i = fishes.size(); i < 5; i++) {
    			Genome genome = getGenome();
    			int x = (int) Math.floor(Math.random() * width*0.2);
    			int y = (int) Math.floor(Math.random() * height*0.2);
    			int heading = (int) Math.floor(Math.random() * 360);
    			spawnFish(new Point2D.Double(x,y), heading, genome);
    		}
    	}

    }

	protected void spawnFish(
			Point2D.Double coords, int heading, Genome genome) {
		fishes.add(new Agent(coords, heading, parent, genome, world));
	}

	protected void spawnShark(
			Point2D.Double coords, int heading, Genome genome) {
		sharks.add(new Enemy(coords, heading, parent, genome, world));
	}

	protected void addFish(Point2D.Double coords, int heading){
		Genome genome = getGenome();

		//Creates an agent with a generic genome for a network that has no hidden nodes
		fishes.add(new Agent(coords, heading, parent, genome, world));
	}

	private Genome getGenome() {
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
		int total = 0;
		int numMotorNeurons = 3;
		int visualFieldSize = Agent.configNumSegments;
		int numVisualNeurons = visualFieldSize*3;
		ArrayList<NeatEdge> edges = new ArrayList<NeatEdge>();
		ArrayList<NeatNode> nodes = new ArrayList<NeatNode>();
		ArrayList<NeatNode> motorNodes = new ArrayList<NeatNode>();
		ArrayList<NeatNode> visualNodes = new ArrayList<NeatNode>();

		/* Motor neurons. */
		for (int i=0; i<3; i++) {
			NeatNode node = NeatNode.createRSNeatNode(total++);
			motorNodes.add(node);
		}
                /* Food sensory neurons. */
		for (int i = 0; i < Agent.configNumSegments; i++){//Food
			NeatNode node = NeatNode.createRSNeatNode(total++);
			visualNodes.add(node);
		}
                /* Wall sensory neurons. */
		for (int i = 0; i < Agent.configNumSegments; i++){//Wall
			NeatNode node = NeatNode.createRSNeatNode(total++);
			visualNodes.add(node);
		}
                /* Enemy sensory neurons. */
		for (int i = 0; i < Agent.configNumSegments; i++){//Enemy
			NeatNode node = NeatNode.createRSNeatNode(total++);
			visualNodes.add(node);
		}

		nodes.addAll(motorNodes);
		nodes.addAll(visualNodes);

		/*
		Full connectivity - for every sensory neuron, connect it to each
		motor neuron.
		*/
		if(FLAG_CONNECT_ALL){
			for (NeatNode vn : visualNodes) {
				for (NeatNode mn : motorNodes) {
					int preID = vn.getId();
					int postID = mn.getId();
					NeatEdge g = new NeatEdge(total++, vn, mn, 30.0, 1);
					edges.add(g);
				}
			}
		}

		return new Genome(edges, nodes, 0, null, null);
	}

	protected void addShark(Point2D.Double coords, int heading){
		Genome genome = getGenome();
		//Creates an agent with a generic genome for a network that has no hidden nodes
		sharks.add(new Enemy(coords, heading, parent, genome, world));
	}

	void addFood(Point2D.Double coords) {
		if (!food.contains(coords) && !coordsOutside(coords)) {
			food.add(new Food(coords));
			foodPos.add(coords);
		}
	}

	private static boolean coordsOutside(Point2D.Double coords) {
		if(coords.x < 0 || coords.x > width || coords.y < 0 || coords.y > height){
			return true;
		}
		return false;
	}

	void addSeaweed(Point2D.Double coords){
		seaweed.add(new Seaweed(coords, this));

	}
	void addWall(Point2D.Double start, Point2D.Double end){
		wall.add(new Wall(start, end, world));
	}
	void addWall(Point2D.Double start, Point2D.Double end, int type){
		wall.add(new Wall(start, end, world, type));
	}

	public int foodsize() { return food.size(); }



	protected void removeAgent(Agent ag){
		if(ag instanceof Enemy) sharks.remove(ag);
		else fishes.remove(ag);
		world.destroyBody(ag.body);
	}

	protected void removeFood(Food fd) {
		food.remove(fd);
		foodPos.remove(fd.coords);
	}

	protected ArrayList<Agent> getAllFishes(){
		return fishes;
	}

	protected ArrayList<Agent> getAllSharks() {
		return sharks;
	}

	public ArrayList<Agent> getAllAgents() {
		// We know the clone of fishes will be of type ArrayList<Agent>, so this cast is safe.
	    @SuppressWarnings("unchecked")
		ArrayList<Agent> all_agents = (ArrayList<Agent>) fishes.clone();
	    all_agents.addAll(sharks);
	    return all_agents;
	}

	protected ArrayList<Food> getAllFood(){
		return food;
	}
	protected ArrayList<Seaweed> getAllSeaweed(){
		return seaweed;
	}
	protected ArrayList<Wall> getAllWalls(){
		return wall;
	}
	protected God[] getAllGods(){
		return new God[]{
				fishGod, sharkGod
		};
	}

	public boolean checkFood(Point2D.Double pt) {
		for(int i=-1;i<2;i++){
			for(int j = -1;j<2;j++){
				if (foodPos.contains(adjustPt(pt, i, j)))
					return true;
			}
		}
		return false;
	}

	private static Point2D.Double adjustPt(Double pt, int i, int j) {
		return new Point2D.Double(pt.x + i, pt.y + j);
	}

	public int getSeaweedSize() {
		return seaweed.size();
	}

}