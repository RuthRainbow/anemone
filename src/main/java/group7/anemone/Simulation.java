package group7.anemone;

import processing.core.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Simulation extends PApplet {
	Environment env = new Environment(this);
	Agent selectedAgent = null;
	PFont f = createFont("Arial",12,true);

	public static void main(String args[]){
		PApplet.main(new String[] { "--present", "group7.anemone.Simulation" });
	}
	public void setup() {
		frameRate(30);
		size(screen.width, screen.height);

		for(int i = 0; i < 10; i++){
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);
			env.addAgent(x, y);
		}
		env.getAllAgents().get(0).setThrust(2, 2);

		for(int i = 0; i < 10; i++){
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);
			env.addFood(x, y);
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
			env.addFood(mouseX, mouseY);
		}

	}
	public void draw(){
		background(0);
		noStroke();
		fill(255,255,0);

		env.updateAllAgents();
		ArrayList<Agent> agents = env.getAllAgents();
		ArrayList<Food> food = env.getAllFood();

		for(int i = 0; i < agents.size(); i++){ //drawing the ikkle agents
			Agent ag = agents.get(i);
			ellipse(ag.getX(), ag.getY(), 20, 20);
		}

		fill(102,255,0);
		for(int i = 0; i < food.size(); i++){ //drawing the food
			Food fd = food.get(i);
			ellipse(fd.getX(), fd.getY(), 5, 5);
		}

		fill(255);
		textFont(f);
		text("FrameRate: " + frameRate, 10, 10);


		if(selectedAgent != null){
			fill(255);
			textFont(f);
			text("Selected agent x = "+selectedAgent.getX(), 10, 25);
			text("Selected agent y = "+selectedAgent.getY(), 10, 40);
		}

	}
}
