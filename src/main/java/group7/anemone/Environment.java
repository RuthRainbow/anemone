package group7.anemone;

import group7.anemone.UI.Utilities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

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
		if (tick % 50 == 0) {
			ArrayList<Gene[]> nextGen = god.BreedPopulation(fishes);
			System.out.println("BREEDING!");
			for (Gene[] gene : nextGen) {
				System.out.println(Arrays.toString(gene));
			}
			//god.BreedPopulation(sharks);
			// Reset tick until next generation
			tick = 0;
		}
	}
	
	protected void spawnAgent(Point2D.Double coords, int heading, Gene[] newGenome) {
		fishes.add(new Agent(coords, heading, parent, newGenome));
	}

	protected void addFish(Point2D.Double coords, int heading){

		/*
		Gene[] genome = new Gene[4];
		genome[0] = new Gene(1, 0,4,4.0,1);
		genome[1] = new Gene(2, 1,4,4.0,1);
		genome[2] = new Gene(3, 2,5,4.0,1);
		genome[3] = new Gene(4, 3,6,4.0,1);
		*/
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
		Gene[] genome = new Gene[90];

		genome[0] = new Gene(0,0,30,4.0,1);
		genome[1] = new Gene(1,1,30,4.0,1);
		genome[2] = new Gene(2,2,30,4.0,1);
		genome[3] = new Gene(3,3,30,4.0,1);
		genome[4] = new Gene(4,4,30,4.0,1);
		genome[5] = new Gene(5,5,30,4.0,1);
		genome[6] = new Gene(6,6,30,4.0,1);
		genome[7] = new Gene(7,7,30,4.0,1);
		genome[8] = new Gene(8,8,30,4.0,1);
		genome[9] = new Gene(9,9,30,4.0,1);
		genome[10] = new Gene(10,10,30,4.0,1);
		genome[11] = new Gene(11,11,30,4.0,1);
		genome[12] = new Gene(12,12,30,4.0,1);
		genome[13] = new Gene(13,13,30,4.0,1);
		genome[14] = new Gene(14,14,30,4.0,1);
		genome[15] = new Gene(15,15,30,4.0,1);
		genome[16] = new Gene(16,16,30,4.0,1);
		genome[17] = new Gene(17,17,30,4.0,1);
		genome[18] = new Gene(18,18,30,4.0,1);
		genome[19] = new Gene(19,19,30,4.0,1);
		genome[20] = new Gene(20,20,30,4.0,1);
		genome[21] = new Gene(21,21,30,4.0,1);
		genome[22] = new Gene(22,22,30,4.0,1);
		genome[23] = new Gene(23,23,30,4.0,1);
		genome[24] = new Gene(24,24,30,4.0,1);
		genome[25] = new Gene(25,25,30,4.0,1);
		genome[26] = new Gene(26,26,30,4.0,1);
		genome[27] = new Gene(27,27,30,4.0,1);
		genome[28] = new Gene(28,28,30,4.0,1);
		genome[29] = new Gene(29,29,30,4.0,1);
		genome[30] = new Gene(30,0,31,4.0,1);
		genome[31] = new Gene(31,1,31,4.0,1);
		genome[32] = new Gene(32,2,31,4.0,1);
		genome[33] = new Gene(33,3,31,4.0,1);
		genome[34] = new Gene(34,4,31,4.0,1);
		genome[35] = new Gene(35,5,31,4.0,1);
		genome[36] = new Gene(36,6,31,4.0,1);
		genome[37] = new Gene(37,7,31,4.0,1);
		genome[38] = new Gene(38,8,31,4.0,1);
		genome[39] = new Gene(39,9,31,4.0,1);
		genome[40] = new Gene(40,10,31,4.0,1);
		genome[41] = new Gene(41,11,31,4.0,1);
		genome[42] = new Gene(42,12,31,4.0,1);
		genome[43] = new Gene(43,13,31,4.0,1);
		genome[44] = new Gene(44,14,31,4.0,1);
		genome[45] = new Gene(45,15,31,4.0,1);
		genome[46] = new Gene(46,16,31,4.0,1);
		genome[47] = new Gene(47,17,31,4.0,1);
		genome[48] = new Gene(48,18,31,4.0,1);
		genome[49] = new Gene(49,19,31,4.0,1);
		genome[50] = new Gene(50,20,31,4.0,1);
		genome[51] = new Gene(51,21,31,4.0,1);
		genome[52] = new Gene(52,22,31,4.0,1);
		genome[53] = new Gene(53,23,31,4.0,1);
		genome[54] = new Gene(54,24,31,4.0,1);
		genome[55] = new Gene(55,25,31,4.0,1);
		genome[56] = new Gene(56,26,31,4.0,1);
		genome[57] = new Gene(57,27,31,4.0,1);
		genome[58] = new Gene(58,28,31,4.0,1);
		genome[59] = new Gene(59,29,31,4.0,1);
		genome[60] = new Gene(60,0,32,4.0,1);
		genome[61] = new Gene(61,1,32,4.0,1);
		genome[62] = new Gene(62,2,32,4.0,1);
		genome[63] = new Gene(63,3,32,4.0,1);
		genome[64] = new Gene(64,4,32,4.0,1);
		genome[65] = new Gene(65,5,32,4.0,1);
		genome[66] = new Gene(66,6,32,4.0,1);
		genome[67] = new Gene(67,7,32,4.0,1);
		genome[68] = new Gene(68,8,32,4.0,1);
		genome[69] = new Gene(69,9,32,4.0,1);
		genome[70] = new Gene(70,10,32,4.0,1);
		genome[71] = new Gene(71,11,32,4.0,1);
		genome[72] = new Gene(72,12,32,4.0,1);
		genome[73] = new Gene(73,13,32,4.0,1);
		genome[74] = new Gene(74,14,32,4.0,1);
		genome[75] = new Gene(75,15,32,4.0,1);
		genome[76] = new Gene(76,16,32,4.0,1);
		genome[77] = new Gene(77,17,32,4.0,1);
		genome[78] = new Gene(78,18,32,4.0,1);
		genome[79] = new Gene(79,19,32,4.0,1);
		genome[80] = new Gene(80,20,32,4.0,1);
		genome[81] = new Gene(81,21,32,4.0,1);
		genome[82] = new Gene(82,22,32,4.0,1);
		genome[83] = new Gene(83,23,32,4.0,1);
		genome[84] = new Gene(84,24,32,4.0,1);
		genome[85] = new Gene(85,25,32,4.0,1);
		genome[86] = new Gene(86,26,32,4.0,1);
		genome[87] = new Gene(87,27,32,4.0,1);
		genome[88] = new Gene(88,28,32,4.0,1);
		genome[89] = new Gene(89,29,32,4.0,1);



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
	protected ArrayList<Wall> getAllWalls(){
		return wall;
	}

	protected void Breed(Agent mother, Agent father) {
		/*ArrayList<Gene[]> children = god.CreateOffspring(mother, father);
		for (Gene[] child : children) {
			//TODO add new agent to env.
		}*/
	}

}