package group7.anemone;

//Class to hold a collision between an agent and an object, which could be another agent or a wall.
//(If we have breeding on collision, another agent vs wall matters).
public class Collision {
	public static final int TYPE_FOOD = 1;
	public static final int TYPE_AGENT = 2;
	public static final int TYPE_ENEMY = 3;
	public static final int TYPE_WALL = 4;
	
	private Agent agent;
	private Object collided;
	
	public Collision(Agent ag, Object ob){
		agent = ag;
		collided = ob;
	}
	
	public Agent getAgent(){
		return agent;
	}
	public Object getCollidedObject(){
		return collided;
	}
	public int getType(){
		if(collided instanceof Food) return TYPE_FOOD;
		if(collided instanceof Agent) return TYPE_AGENT;
		if(collided instanceof Wall) return TYPE_WALL;
		
		return -1;
	}
}
