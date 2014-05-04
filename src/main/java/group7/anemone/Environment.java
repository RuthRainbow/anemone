package group7.anemone;

import group7.anemone.Genetics.God;
import group7.anemone.CPPN.CPPNFunction;
import group7.anemone.Genetics.GeneticObject;
import group7.anemone.Genetics.GenomeEdge;
import group7.anemone.HyperNeatGenetics.Chromosome;
import group7.anemone.HyperNeatGenetics.HyperNeatFishGod;
import group7.anemone.HyperNeatGenetics.HyperNeatGenome;
import group7.anemone.HyperNeatGenetics.HyperNeatNode;
import group7.anemone.HyperNeatGenetics.HyperNeatSharkGod;
import group7.anemone.NeatGenetics.NeatFishGod;
import group7.anemone.NeatGenetics.NeatGenome;
import group7.anemone.NeatGenetics.NeatNode;
import group7.anemone.NeatGenetics.NeatSharkGod;
import group7.anemone.UI.Utilities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;
import java.util.ArrayList;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import processing.core.PApplet;

public class Environment implements Serializable{

	private static final long serialVersionUID = 2740658645450395424L;
	transient PApplet parent;
	// God & clock needed for breeding every n generations
	// God types given on instantiation in constructor.
	@SuppressWarnings("rawtypes")
	public God fishGod;
	@SuppressWarnings("rawtypes")
	private God sharkGod;
	private int tick = 0;
	private int fishGeneration = 0;
	private int sharkGeneration = 0;
	private int totalTick = 0;
	private int sharkStart = 20000;
	private ArrayList<Agent> fishes;
	private ArrayList<Agent> sharks;
	private static ArrayList<Food> food;
	private ArrayList<Wall> wall;
	private ArrayList<Seaweed> seaweed;
	private static ArrayList<Point2D.Double> foodPos;
	public ArrayList<Agent> scheduledRemove;

	private ArrayList<Collision> collisions;

	// Whether to not use health:
	protected final boolean fitnessOnly = true;
	//whether a fully connected network should be created.
	protected final boolean FLAG_CONNECT_ALL= true;
	// True -> use NEAT, false -> use HyperNEAT
	protected final boolean Neat = true;
	//True => sharks on, false => sharks off
	protected final boolean SharksOn = true;

	protected final int numHyperNeatLayers = 4;

	static int width = 2500;
	static int height = 2500;
	
	static float box2Dwidth = width/Simulation.meterToPixel;
	static float box2Dheight = height/Simulation.meterToPixel;

	//Save number of segments used by agent for analysis tool
	public int agentNumSegments;

	//JBox2D Variables
	transient World world;

	public Environment(PApplet p){
		this.parent = p;
		if (!Neat) {
			this.fishGod = new HyperNeatFishGod();
			this.sharkGod = new HyperNeatSharkGod();
		} else {
			this.fishGod = new NeatFishGod();
			this.sharkGod = new NeatSharkGod();
		}
		this.fishes = new ArrayList<Agent>();
		this.sharks = new ArrayList<Agent>();
		Environment.food = new ArrayList<Food>();
		this.wall = new ArrayList<Wall>();
		this.seaweed = new ArrayList<Seaweed>();
		this.foodPos = new ArrayList<Point2D.Double>();
		this.agentNumSegments = Agent.configNumSegments;
		this.foodPos = new ArrayList<Point2D.Double>(); 
		this.scheduledRemove = new ArrayList<Agent>();
		
		buildBox2DWorld();
	}
	
	public void buildBox2DWorld(){
		//JBox2D
		Vec2 gravity = new Vec2(0.0f, 0.0f);
		this.world = new World(gravity);
		world.setContactListener(new ContactListener() {


			//@Override
			public void beginContact(Contact contact) {


			}

			//@Override
			public void endContact(Contact arg0) {
				
				
			}

			//@Override
			public void postSolve(Contact contact, ContactImpulse arg1) {
				Fixture ob1 = contact.getFixtureA();
				Fixture ob2 = contact.getFixtureB();
				SimulationObject simOb1 = (SimulationObject) ob1.getUserData();
				SimulationObject simOb2 = (SimulationObject) ob2.getUserData();
				if(simOb1 instanceof Wall || simOb2 instanceof Wall){
					if(simOb1 instanceof Agent){
						((Agent) simOb1).hitWall();
					} else if(simOb2 instanceof Agent){
						((Agent) simOb2).hitWall();
					}
				}
				boolean obisAgent1 = (simOb1 instanceof Agent) && !(simOb1 instanceof Enemy);
				boolean obisAgent2 = (simOb2 instanceof Agent) && !(simOb2 instanceof Enemy);
				boolean obisEnemy1 = (simOb1 instanceof Agent) && (simOb1 instanceof Enemy);
				boolean obisEnemy2 = (simOb2 instanceof Agent) && (simOb2 instanceof Enemy);
				if(obisEnemy1 && obisAgent2){
					Collision cc = new Collision((Agent) simOb1,simOb2);
					eatFood(cc);
					//world.destroyBody(ob2.getBody());
					scheduledRemove.add((Agent) simOb2);
				} 
				if(obisAgent1 && obisEnemy2){
					Collision cc = new Collision((Agent) simOb2,simOb1);
					eatFood(cc);
					//world.destroyBody(ob1.getBody());
					scheduledRemove.add((Agent) simOb1);
				}
				
			}

			//@Override
			public void preSolve(Contact arg0, Manifold arg1) {
				
				
			}


        });
	}

    // Method to get all collisions that occurred in the environment
    public ArrayList<Collision> updateCollisions() {
    	collisions = new ArrayList<Collision>();


    	for (Agent ag: getAllAgents()) { //for each agent, check for any collision

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
    	world.step(1.0f / (30.0f*Simulation.meterToPixel*Simulation.meterToPixel), 6, 3);
    	for(int i=0;i<scheduledRemove.size();i++){
    		removeAgent(scheduledRemove.get(i));
    	}
    	scheduledRemove.clear();
    	
    	for (Agent fish: fishes) { //drawing the ikkle fishes
    		fish.update();
    	}
    	for (Agent shark:sharks) {
    		shark.update();
    	}
    	tick++;
    	
    	if (SharksOn && (totalTick == sharkStart)) {
			for (int i = 0; i < Simulation.numStartingSharks; i++) {
				int x = (int) Math.floor(Environment.width*0.8 + Math.random() * Environment.width*0.2);
				int y = (int) Math.floor(Environment.height*0.8 + Math.random() * Environment.height*0.2);
				int heading = (int) Math.floor(Math.random() * 360);
				addShark(new Point2D.Double(x, y), heading);
			}
			totalTick++;
    	} else {
    		totalTick++;
    	}
    	
    	if (tick % 1000 == 0) {
    		if (SharksOn && tick % 2000 == 0 && totalTick > sharkStart) {
    			sharkGeneration++;
    			@SuppressWarnings("unchecked")
				ArrayList<GeneticObject> nextSharks = sharkGod.BreedWithSpecies(sharks, fitnessOnly);
   
        		if (fitnessOnly) {
        			ArrayList<Agent> nextAgents = new ArrayList<Agent>();
        			for (int i = 0; i < sharks.size(); i++) {
        				GeneticObject genObj = sharks.get(i).getGeneticObject();
    	    			if (nextSharks.contains(genObj)) {
    	    				nextSharks.remove(genObj);
    	    				nextAgents.add(sharks.get(i));
    	    			}
    	    		}
        			sharks.clear();
        			sharks.addAll(nextAgents);
        		}
    			for (GeneticObject genObj : nextSharks) {
    				int x = (int) Math.floor(width*0.8 + Math.random() * width*0.2);
    				int y = (int) Math.floor(height*0.8 + Math.random() * height*0.2);
    				int heading = (int) Math.floor(Math.random() * 360);
    				spawnShark(new Point2D.Double(x,y), heading, genObj);
    			}
    			// Reset tick until next generation
    			tick = 0;
    		}
    		fishGeneration++;
    		@SuppressWarnings("unchecked")
			ArrayList<GeneticObject> nextFish = fishGod.BreedWithSpecies(fishes, fitnessOnly);

    		if (fitnessOnly) {
    			ArrayList<Agent> nextAgents = new ArrayList<Agent>();

    			for (int i = 0; i < fishes.size(); i++) {
    				GeneticObject genObj = fishes.get(i).getGeneticObject();
	    			if (nextFish.contains(genObj)) {
	    				nextFish.remove(genObj);
	    				nextAgents.add(fishes.get(i));
	    			}
	    		}
	    		fishes.clear();
	    		fishes.addAll(nextAgents);
    		}
    		for (GeneticObject genObj : nextFish) {
    			int x = (int) Math.floor(Math.random() * width*0.2);
    			int y = (int) Math.floor(Math.random() * height*0.2);
    			int heading = (int) Math.floor(Math.random() * 360);
    			spawnFish(new Point2D.Double(x,y), heading, genObj);
    		}

    	}
    	if(tick % 50 == 0 && food.size() > 10){
    		food.remove(0);
    	}

    	while(seaweed.size() > Simulation.maxSeaweed){
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
    			GeneticObject genObj = Neat ? getDefaultGenome() : getDefaultChromosome();
    			
    			int x = (int) Math.floor(Math.random() * width*0.2);
    			int y = (int) Math.floor(Math.random() * height*0.2);
    			int heading = (int) Math.floor(Math.random() * 360);
    			spawnFish(new Point2D.Double(x,y), heading, genObj);
    		}
    	}

    }

	protected void spawnFish(
			Point2D.Double coords, int heading, GeneticObject genObj) {
		fishes.add(new Agent(coords, heading, parent, genObj, Neat, world));
	}

	protected void spawnShark(
			Point2D.Double coords, int heading, GeneticObject genObj) {
		sharks.add(new Enemy(coords, heading, parent, genObj, Neat, world));
	}

	protected void addFish(Point2D.Double coords, int heading){
		GeneticObject genObj = Neat ? getDefaultGenome() : getDefaultChromosome();
		fishes.add(new Agent(coords, heading, parent, genObj, Neat, world));
	}

	protected void addShark(Point2D.Double coords, int heading){
		GeneticObject genObj = Neat ? getDefaultGenome() : getDefaultChromosome();
		//Creates an agent with a generic genome for a network that has no hidden nodes
		sharks.add(new Enemy(coords, heading, parent, genObj, Neat, world));
	}

	private Chromosome getDefaultChromosome() {
		if (this.Neat == false) {
			Chromosome chromosome;
			ArrayList<HyperNeatGenome> genomes;
			HyperNeatNode inputNode1, inputNode2, outputNode;
			GenomeEdge<HyperNeatNode> edge1, edge2;
			CPPNFunction basicFunc;
			int layer, nodeId, genomeId;
			
			genomes = new ArrayList<HyperNeatGenome>();
			
			/*
			Create neuron layer CPPNs.
			*/
			basicFunc = new CPPNFunction(0, 0, 0, 3);
			layer = 0;
			genomeId = 0;
			for (int i=0; i<numHyperNeatLayers; i++) {
				nodeId = 0;
				ArrayList<HyperNeatNode> nodes =
					new ArrayList<HyperNeatNode>();
				
				ArrayList<GenomeEdge<HyperNeatNode>> edges =
					new ArrayList<GenomeEdge<HyperNeatNode>>();
				
				/* Create CPPN nodes for use with genetic package. */
				inputNode1 = new HyperNeatNode(nodeId++, basicFunc, HyperNeatNode.Type.INPUT);
				inputNode2 = new HyperNeatNode(nodeId++, basicFunc, HyperNeatNode.Type.INPUT);
				outputNode = new HyperNeatNode(nodeId++, basicFunc, HyperNeatNode.Type.OUTPUT);
				
				/* Create accompanying CPPN edges. */
				edge1 = new GenomeEdge<HyperNeatNode>(0,
					inputNode1, outputNode, 30.0, 1);
				edge2 = new GenomeEdge<HyperNeatNode>(0,
					inputNode2, outputNode, 30.0, 1);

				nodes.add(inputNode1);
				nodes.add(inputNode2);
				nodes.add(outputNode);
				
				edges.add(edge1);
				edges.add(edge2);

				HyperNeatGenome g = new HyperNeatGenome(
						edges, nodes, genomeId, HyperNeatGenome.Type.NEURON, layer++);
				genomeId++;
				
				genomes.add(g);
			}
			
			/* Create synapse layer CPPNs. */
			layer = 0;
			for (int i=0; i<numHyperNeatLayers; i++) {
				nodeId = 0;
				ArrayList<HyperNeatNode> nodes =
					new ArrayList<HyperNeatNode>();
				
				ArrayList<GenomeEdge<HyperNeatNode>> edges =
					new ArrayList<GenomeEdge<HyperNeatNode>>();
				
				/* Create CPPN nodes for use with genetic package. */
				inputNode1 = new HyperNeatNode(nodeId++, basicFunc, HyperNeatNode.Type.INPUT);
				inputNode2 = new HyperNeatNode(nodeId++, basicFunc, HyperNeatNode.Type.INPUT);
				outputNode = new HyperNeatNode(nodeId++, basicFunc, HyperNeatNode.Type.OUTPUT);
				
				/* Create accompanying CPPN edges. */
				edge1 = new GenomeEdge<HyperNeatNode>(0,
					inputNode1, outputNode, 30.0, 1);
				edge2 = new GenomeEdge<HyperNeatNode>(0,
					inputNode2, outputNode, 30.0, 1);

				nodes.add(inputNode1);
				nodes.add(inputNode2);
				nodes.add(outputNode);
				
				edges.add(edge1);
				edges.add(edge2);
				
				HyperNeatGenome g = new HyperNeatGenome(
						edges, nodes, genomeId, HyperNeatGenome.Type.SYNAPSE, layer++);
				genomeId++;
				
				genomes.add(g);
			}
			
			chromosome = new Chromosome(genomes, 0, null, null);
			return chromosome;
		} else {
			return null;
		}
	}

	private NeatGenome getDefaultGenome() {
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
		ArrayList<GenomeEdge<NeatNode>> edges = new ArrayList<GenomeEdge<NeatNode>>();
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
					GenomeEdge<NeatNode> g = new GenomeEdge<NeatNode>(total++, vn, mn, 30.0, 1);
					edges.add(g);
				}
			}
		}

		return new NeatGenome(edges, nodes, 0, null, null);
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
	
	public int getFishGeneration() {
		return fishGeneration;
	}
	
	public int getSharkGeneration() {
		return sharkGeneration;
	}
	
	
	public int getTick() {
		return tick;
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
	@SuppressWarnings("rawtypes")
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

	public void eatFood(Collision cc) {
		Object obj = cc.getCollidedObject();

		
		if(obj instanceof Food){
			Food fd = (Food) obj;
			removeFood(fd);
			cc.getAgent().ateFood();
		} else {
			Agent ag = (Agent) cc.getCollidedObject();
			killAgent(ag);
			cc.getAgent().ateFood();
		}
	}
	
	private void killAgent(Agent ag){
		//removeAgent(ag);
		scheduledRemove.add(ag);
		if(Simulation.selectedAgent == ag){
			Simulation.selectedAgent = null;
		}
	}
}

