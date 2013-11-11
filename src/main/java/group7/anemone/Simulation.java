package group7.anemone;

import group7.anemone.UI.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

// We aren't going to serialise this.
//This comment exists because I'm testing out how branching and merging works.
@SuppressWarnings("serial")
public class Simulation extends PApplet {
	Environment env = new Environment(this);
	Agent selectedAgent = null;
	PFont f = createFont("Arial",12,true);
	int mouseMode=0;
	
	//UI Elements
	UIWindow win;
	UIWindow sidePanel;
	UIWindow btnGroupModes;
	UIWindow winStats;
	UIWindow winTheme;
	
	UIButton btnAddFood, btnAddAgent, btnSelectAgent, btnThrust, btnToggleTheme;
	UIAngle agentHeading;
	UILabel lblStatTitle, lblX, lblY, lblHeading, lblHealth;
	UIDropdown themeDrop;
	UIColorWheel themeColorWheel;
	UITheme theme;
	
	int width = 0;
	int height = 0;

	public static void main(String args[]){
		// Run the applet when the Java application is run
		PApplet.main(new String[] { "--present", "group7.anemone.Simulation" });
	}

	public void setup() {
		frameRate(30);
		size(screen.width, screen.height);
		setupUI();
		
		width = screen.width - 250;
		height = screen.height;
		
		for(int i = 0; i < 10; i++){
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);
			int heading = (int) Math.floor(Math.random() * 360);
			env.addFish(new Point2D.Double(x, y), heading);
		}
		env.getAllAgents().get(0).thrust(2);
		selectedAgent = env.getAllAgents().get(0);

		for(int i = 0; i < 10; i++){
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);
			env.addFood(new Point2D.Double(x, y));
		}
		
		env.addWall(new Point2D.Double(0,0), new Point2D.Double(width,0));
		env.addWall(new Point2D.Double(width,0), new Point2D.Double(width,height));
		env.addWall(new Point2D.Double(0,height), new Point2D.Double(width,height));
		env.addWall(new Point2D.Double(0,height), new Point2D.Double(0,0));
	}
	public void mousePressed(){
		ArrayList<Agent> agents = env.getAllAgents();
		Agent agent_clicked = null;
		
		if(win.mousePressed()) return;
		
		/*
		 * Mouse Modes are as follows:
		 * 0 = Click tool - Select agents to see infromation on them in the top left hand corner
		 * 1 = Food tool - Place food where you click
		 */
		switch(mouseMode){
		case 0: for(int i = 0; i < agents.size(); i++){ //loop through each agent and find one clicked
					Agent ag = agents.get(i);
					if(Math.sqrt(Math.pow(mouseX - ag.getX(), 2) + Math.pow(mouseY - ag.getY(), 2)) < 10){
						agent_clicked = ag;
						break;
					}
				}
				if(agent_clicked != null){ //agent was clicked so update selected
					selectedAgent = agent_clicked;
				}
				break;
				
		case 1: env.addFood(new Point2D.Double(mouseX, mouseY));
				break;
				
		case 2: int heading = (int) Math.floor(Math.random() * 360);
				env.addFish(new Point2D.Double(mouseX, mouseY), heading);
				break;
		case 3: for(int i = 0; i < agents.size(); i++){ //loop through each agent and find one clicked
					Agent ag = agents.get(i);
					if(Math.sqrt(Math.pow(mouseX - ag.getX(), 2) + Math.pow(mouseY - ag.getY(), 2)) < 10){
						agent_clicked = ag;
						break;
					}
				}
				if(agent_clicked != null){ //agent was clicked so update selected
					agent_clicked.thrust(2);
				}
				break;
		}

	}
	public void mouseReleased(){
		if(win.mouseReleased()) return;
		
		
	}
	public void mouseDragged(){
		if(win.mouseDragged()) return;
		
		
	}
	public void keyReleased(){	//Hotkeys for buttons
		if(win.keyReleased()) return;
		
		switch(key) {
		case('e'):	mouseMode=2;
					btnGroupModes.selectButton(btnAddAgent);
					break;
		case('q'): 	mouseMode =0;
					btnGroupModes.selectButton(btnSelectAgent);
					break;
		case('r'):	mouseMode =3;
					btnGroupModes.selectButton(btnThrust);
					break;
		case('w'):	mouseMode=1;
					btnGroupModes.selectButton(btnAddFood);
					break;
		}
	}
	
	public void draw(){
		background(theme.getColor("Background"));	//Draws background, basically refreshes the screen
		win.setBackground(theme.getColor("Background"));
		sidePanel.setBackground(theme.getColor("Sidepanel"));
		
		env.updateAllAgents();	//'Ticks' for the new frame, sensors sense, networks network and collisions are checked.
		env.updateCollisions(); //update the environment with the new collisions
		env.updateAgentsSight(); //update all the agents to everything they can see in their field of view
		handleCollisions();
		checkDeaths();
		updateUI();
		
		win.draw();
		
		ArrayList<Agent> agents = env.getAllAgents();	//Returns an arraylist of agents
		ArrayList<Food> food = env.getAllFood();		//Returns an arraylist of all the food on the map

		for(int i = 0; i < agents.size(); i++){ //Runs through arraylist of agents, will draw them on the canvas
			Agent ag = agents.get(i);

			//draw the field of view for the agent
			stroke(128, (float) ag.getHealth()*200+55);
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
			fill(theme.getColor("Agent"), (float) ag.getHealth()*200 +55);
			ellipse(ag.getX(), ag.getY(), 20, 20);
		}

		noStroke();
		fill(theme.getColor("Food"));
		for(int i = 0; i < food.size(); i++){ //Runs through arraylist of food, will draw them on the canvas
			Food fd = food.get(i);
			ellipse(fd.getX(), fd.getY(), 5, 5);
		}
				
		fill(255);
		text("FrameRate: " + frameRate, 10, 10);	//Displays framerate in the top left hand corner

		/*if(selectedAgent != null){	//If an agent is seleted, display its coordinates in the top left hand corner, under the framerate
			fill(255);
			textFont(f);
			
			String tmp = "";
			for(SightInformation si : selectedAgent.getCanSee()) tmp += ", "+si.getType();
			
			text("Selected agent x = "+selectedAgent.getX(), 10, 25);
			text("Selected agent y = "+selectedAgent.getY(), 10, 40);
			text("Selected agent health = "+selectedAgent.getHealth(), 10, 55);
			text("Selected agent see = "+selectedAgent.getCanSee().size()+ " "+tmp, 10, 70);
			text("Selected agent see food (seg 0)= "+selectedAgent.viewingObjectOfTypeInSegment(0, SightInformation.TYPE_FOOD), 10, 85);
		}*/

	}
	
	private void updateUI(){
		agentHeading.setVisible(selectedAgent != null);
		winStats.setVisible(selectedAgent != null);
		
		if(selectedAgent != null){
			agentHeading.setAngle(selectedAgent.getViewHeading());
			lblX.setText("x = " + selectedAgent.getX());
			lblY.setText("y = " + selectedAgent.getY());
			lblHeading.setText("heading = " + selectedAgent.getViewHeading());
			lblHealth.setText("health = " + selectedAgent.getHealth());
			//lblX.setText("Selected agent see = "+selectedAgent.getCanSee().size()+ " "+tmp, 10, 70);
			//lblX.setText("Selected agent see food (seg 0)= "+selectedAgent.viewingObjectOfTypeInSegment(0, SightInformation.TYPE_FOOD), 10, 85);
		}
	}
	private void setupUI(){
		win = new UIWindow(this, 0, 0, screen.width, screen.height);
		sidePanel = new UIWindow(this, 250, 0, 250, screen.height);
		sidePanel.setIsLeft(false);
		sidePanel.setBackground(50);
		sidePanel.setFixedBackground(true);
		win.addObject(sidePanel);

		//Buttons to change the current mode
		btnGroupModes = new UIWindow(this, 0, 0, 200, 200);
		sidePanel.addObject(btnGroupModes);
		
		btnSelectAgent = addModeButton(0, "Select", 10, 2 ,118 ,255);
		btnAddFood = addModeButton(1, "Food", 70, 84, 255, 159);
		btnAddAgent = addModeButton(2, "Agent", 130, 255, 127, 0);
		btnThrust = addModeButton(3, "Thrust", 190, 0, 231, 125);
		
		btnGroupModes.selectButton(btnSelectAgent);
		
		//control to change the selected agents heading
		agentHeading = new UIAngle(this, 30, 100, 50);
		agentHeading.setEventHandler(new UIAction(){
			public void change(UIAngle ang){
				if(selectedAgent != null){
					selectedAgent.changeViewHeading(ang.getAngle() - selectedAgent.getViewHeading());
				}
			}
		});
		sidePanel.addObject(agentHeading);
		
		//Statistics window for the currently selected agent
		winStats = new UIWindow(this, 0, 300, 200, 200);
		sidePanel.addObject(winStats);
		
		lblStatTitle = addStatLabel("Selected Agent Stats", 10);
		lblX = addStatLabel("X", 30);
		lblY = addStatLabel("X", 45);
		lblHeading = addStatLabel("X", 60);
		lblHealth = addStatLabel("X", 75);
		
		//Themes window
		theme = new UITheme();
		theme.setColor("Background", color(0));
		theme.setColor("Sidepanel", color(50));
		theme.setColor("Food", color(0, 255, 0));
		theme.setColor("Agent", color(255, 127, 0));
		
		winTheme = new UIWindow(this, 0, 240, 200, 200);
		winTheme.setIsTop(false);
		sidePanel.addObject(winTheme);
		
		themeColorWheel = new UIColorWheel(this, 45, 40);
		themeColorWheel.setVisible(false);
		themeColorWheel.setEventHandler(new UIAction(){
			public void change(UIColorWheel wheel){
				theme.setColor(themeDrop.getSelected(), wheel.getColor());
			}
		});
		winTheme.addObject(themeColorWheel);
		
		themeDrop = new UIDropdown(this, 25, 10, 200, theme.getKeys());
		themeDrop.setVisible(false);
		themeDrop.setEventHandler(new UIAction(){
			public void change(UIDropdown drop){
				themeColorWheel.setColor(theme.getColor(drop.getSelected()));
			}
		});
		winTheme.addObject(themeDrop);
		
		btnToggleTheme = new UIButton(this, 25, 195, 200, 30, "Toggle Theme Editor");
		btnToggleTheme.setColor(20, 20, 20);
		btnToggleTheme.setFontColor(240, 240, 240);
		btnToggleTheme.setEventHandler(new UIAction(){
			public void click(UIButton btn){
				themeColorWheel.toggleVisible();
				themeDrop.toggleVisible();
			}
		});
		winTheme.addObject(btnToggleTheme);
	}
	private UIButton addModeButton(final int mode, String txt, int pos, int r, int g, int b){
		UIButton btn = new UIButton(this, pos, 20, 50, 50, txt);
		btn.setEventHandler(new UIAction(){
			public void click(UIButton btn){
				btnGroupModes.selectButton(btn);
				mouseMode = mode;
			}
		});
		btn.setColor(r, g, b);
		btnGroupModes.addObject(btn);
		return btn;
	}
	private UILabel addStatLabel(String value, int pos){
		UILabel lbl = new UILabel(this, 10, pos, value);
		winStats.addObject(lbl);
		return lbl;
	}
	private double toRadians(double deg){
		return deg * Math.PI / 180;
	}
	
	private void checkDeaths(){ //mwahahaha >:)
		ArrayList<Agent> agents = env.getAllAgents();
		
		for (Agent ag: agents) { 
			if(ag.getHealth() <= 0){
				env.removeAgent(ag);
				if(selectedAgent == ag) selectedAgent = null;
			}
		}
	}
	
	private void handleCollisions(){
		ArrayList<Collision> collisions = env.getCollisions();
		
		for (Collision cc: collisions) { //check collisions to food
    		int type = cc.getType();
    		
    		switch(type){
    			case Collision.TYPE_FOOD: eatFood(cc);break;
    			case Collision.TYPE_WALL: stopAgent(cc); break;
    		}
		}
	}

	private void stopAgent(Collision cc) {
		cc.getAgent().stop();
		
	}

	private void eatFood(Collision cc) {
		Food fd = (Food) cc.getCollidedObject();
		
		env.removeFood(fd);
		cc.getAgent().updateHealth(fd.getValue());
	}
	

}
