package group7.anemone;

import processing.core.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Simulation extends PApplet {
	Environment env = new Environment(this);
	
	public static void main(String args[]){
		PApplet.main(new String[] { "--present", "group7.anemone.Simulation" });
	}
	public void setup() {
	  size(1000, 750);
	  
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
	}
}
