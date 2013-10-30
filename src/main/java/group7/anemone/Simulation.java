package group7.anemone;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

// We aren't going to serialise this.
@SuppressWarnings("serial")
public class Simulation extends PApplet {
	Environment env = new Environment(this);
	Agent selectedAgent = null;
	PFont f = createFont("Arial",12,true);

	public static void main(String args[]){
		// Run the applet when the Java application is run
		PApplet.main(new String[] { "--present", "group7.anemone.Simulation" });
	}

	public void setup() {
		frameRate(30);
		size(screen.width, screen.height);

		for(int i = 0; i < 10; i++){
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);
			env.addFish(new Point2D.Double(x, y));
		}
		env.getAllAgents().get(0).setThrust(2, 2);

		for(int i = 0; i < 10; i++){
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);
			env.addFood(new Point2D.Double(x, y));
		}
	}
	public void mousePressed(){
		ArrayList<Agent> agents = env.getAllAgents();
		Agent agent_clicked = null;

		for(int i = 0; i < agents.size(); i++){ //loop through each agent and find one clicked
			Agent ag = agents.get(i);
			if(Math.sqrt(Math.pow(mouseX - ag.getX(), 2) + Math.pow(mouseY - ag.getY(), 2)) < 10){
				agent_clicked = ag;
				break;
			}
		}

		if(agent_clicked != null){ //agent was clicked so update selected
			selectedAgent = agent_clicked;
		}else{ //agent was not clicked
			env.addFood(new Point2D.Double(mouseX, mouseY));
		}

	}
	public void draw(){
		background(0);	//Draws background, basically refreshes the screen
		
		env.updateAllAgents();	//'Ticks' for the new frame, sensors sense, networks network and collisions are checked.
		env.updateCollisions(); //update the environment with the new collisions
		handleCollisions();
		checkDeaths();
		ArrayList<Agent> agents = env.getAllAgents();	//Returns an arraylist of agents
		ArrayList<Food> food = env.getAllFood();		//Returns an arraylist of all the food on the map

		for(int i = 0; i < agents.size(); i++){ //Runs through arraylist of agents, will draw them on the canvas
			Agent ag = agents.get(i);

			//draw the field of view for the agent
			stroke(128);
			noFill();
			double range = ag.getVisionRange() * 2;
			
			pushMatrix();
			translate(ag.getX(), ag.getY());
			rotate((float) toRadians(ag.getViewHeading() - ag.getFOV()));
			line(0, 0, (int) (range / 2), 0);
			popMatrix();
			
			pushMatrix();
			translate(ag.getX(), ag.getY());
			rotate((float) toRadians(ag.getViewHeading() + ag.getFOV()));
			line(0, 0, (int) (range / 2), 0);
			popMatrix();
			
			arc((float) ag.getX(), (float) ag.getY(), (float) range, (float) range, (float) toRadians(ag.getViewHeading() - ag.getFOV()) , (float) toRadians(ag.getViewHeading() + ag.getFOV()));
		
			//draw our circle representation for the agent
			noStroke();
			fill(255, 127, 0);
			ellipse(ag.getX(), ag.getY(), 20, 20);
		}

		fill(0, 255, 0);
		for(int i = 0; i < food.size(); i++){ //Runs through arraylist of food, will draw them on the canvas
			Food fd = food.get(i);
			ellipse(fd.getX(), fd.getY(), 5, 5);
		}

		fill(255);
		textFont(f);
		text("FrameRate: " + frameRate, 10, 10);	//Displays framerate in the top left hand corner

		if(selectedAgent != null){	//If an agent is seleted, display its coordinates in the top left hand corner, under the framerate
			fill(255);
			textFont(f);
			text("Selected agent x = "+selectedAgent.getX(), 10, 25);
			text("Selected agent y = "+selectedAgent.getY(), 10, 40);
			text("Selected agent health = "+selectedAgent.getHealth(), 10, 55);
		}

	}
	private double toRadians(double deg){
		return deg * Math.PI / 180;
	}
	
	private void checkDeaths(){ //mwahahaha >:)
		ArrayList<Agent> agents = env.getAllAgents();
		
		for (Agent ag: agents) { 
			if(ag.getHealth() <= 0){
				env.removeAgent(ag);
				selectedAgent = null;
			}
		}
	}
	
	private void handleCollisions(){
		ArrayList<Collision> collisions = env.getCollisions();
		
		for (Collision cc: collisions) { //check collisions to food
    		int type = cc.getType();
    		
    		switch(type){
    			case Collision.TYPE_FOOD: eatFood(cc);break;
    		}
		}
	}

	private void eatFood(Collision cc) {
		Food fd = (Food) cc.getCollidedObject();
		
		env.removeFood(fd);
		cc.getAgent().updateHealth(fd.getValue());
	}
}
