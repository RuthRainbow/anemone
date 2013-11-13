package group7.anemone;

import group7.anemone.UI.*;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

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
	UIButton btnSelectKill, btnSelectHealth, btnSelectThrust;
	UIAngle agentHeading;
	UILabel lblStatTitle, lblX, lblY, lblHeading, lblHealth, lblAngle, lblSpeed;
	UIDropdown themeDrop;
	UIColorWheel themeColorWheel;
	UITheme theme;
	UIProgress progHealth;
	UISlider sliderX, sliderY;
	UIDrawable3D neuralVisual;
	
	int width = 0;
	int height = 0;
	float neuralRotation = 0;

	public static void main(String args[]){
		// Run the applet when the Java application is run
		PApplet.main(new String[] { "--present", "group7.anemone.Simulation" });
	}

	public void setup() {
		frameRate(30);
		size(screen.width, screen.height, P3D);
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
		env.addWall(new Point2D.Double(width,height), new Point2D.Double(0,height));
		env.addWall(new Point2D.Double(0,height), new Point2D.Double(0,0));
	}
	public void mousePressed(){
		ArrayList<Agent> agents = env.getAllAgents();
		Agent agent_clicked = null;
		
		if(win.mousePressed()) return;
		if(!Utilities.isPointInBox(mouseX, mouseY, 0, 0, width, height)) return;
		
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
	public void mouseWheel(MouseWheelEvent event){
		if(win.mouseWheel(event)) return;
		
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
			fill(theme.getColor("Agent")); //, (float) ag.getHealth()*200 +55); // Alpha was severly impacting performance of simulation
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
			progHealth.setValue(selectedAgent.getHealth());
			progHealth.setColor((int) (255 - 255 * selectedAgent.getHealth()), (int) (255 * selectedAgent.getHealth()), 0);
			sliderX.setValue((double) selectedAgent.getX() / width);
			sliderY.setValue((double) selectedAgent.getY() / height);
			
			lblX.setText("x = " + selectedAgent.getX());
			lblY.setText("y = " + selectedAgent.getY());
			lblHeading.setText("heading = " + Math.round(selectedAgent.getViewHeading()) + "°");
			lblHealth.setText("health = " + Math.round(selectedAgent.getHealth() * 1000) / 10.0 + "%");
			lblAngle.setText("angle = " + selectedAgent.getMovingAngle() + "°");
			lblSpeed.setText("speed = " + Math.round(selectedAgent.getMovingSpeed() * 10) / 10.0 + "MPH x 10^6");
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
		btnGroupModes = new UIWindow(this, 0, 0, 250, 150);
		btnGroupModes.setBackground(30);
		btnGroupModes.setFixedBackground(true);
		sidePanel.addObject(btnGroupModes);
		
		btnSelectAgent = addModeButton(0, "Select", 10, 2 ,118 ,255);
		btnAddFood = addModeButton(1, "Food", 70, 84, 255, 159);
		btnAddAgent = addModeButton(2, "Agent", 130, 255, 127, 0);
		btnThrust = addModeButton(3, "Thrust", 190, 0, 231, 125);
		
		btnGroupModes.selectButton(btnSelectAgent);
		
		//Statistics window for the currently selected agent
		winStats = new UIWindow(this, 0, 300, 300, 250);
		winStats.setBackground(30);
		winStats.setFixedBackground(true);
		sidePanel.addObject(winStats);
		
		//control to change the selected agents heading
		agentHeading = new UIAngle(this, 10, 30, 50);
		agentHeading.setEventHandler(new UIAction(){
			public void change(UIAngle ang){
				if(selectedAgent != null){
					selectedAgent.changeViewHeading(ang.getAngle() - selectedAgent.getViewHeading());
				}
			}
		});
		winStats.addObject(agentHeading);
		
		//progress bar of the selected agents current health
		progHealth = new UIProgress(this, 10, 93, 230, 10);
		winStats.addObject(progHealth);
		
		//sliders to move agents position
		sliderX = new UISlider(this, 75, 35, 165, 15);
		sliderX.setEventHandler(new UIAction(){
			public void change(UISlider slider){
				if(selectedAgent != null){
					selectedAgent.setX((int) (slider.getValue() * width));
				}
			}
		});
		winStats.addObject(sliderX);
		
		sliderY = new UISlider(this, 75, 60, 165, 15);
		sliderY.setEventHandler(new UIAction(){
			public void change(UISlider slider){
				if(selectedAgent != null){
					selectedAgent.setY((int) (slider.getValue() * height));
				}
			}
		});
		winStats.addObject(sliderY);
		
		//boost agent health back to 100%
		btnSelectHealth = new UIButton(this, 10, 115, 65, 20, "100%");
		btnSelectHealth.setColor(84, 255, 159);
		btnSelectHealth.setEventHandler(new UIAction(){
			public void click(UIButton btn){
				if(selectedAgent != null){
					selectedAgent.updateHealth(1);
				}
			}
		});
		winStats.addObject(btnSelectHealth);
		
		//thrust selected agent
		btnSelectThrust = new UIButton(this, 93, 115, 65, 20, "Thrust");
		btnSelectThrust.setColor(251, 150, 20);
		btnSelectThrust.setEventHandler(new UIAction(){
			public void click(UIButton btn){
				if(selectedAgent != null){
					selectedAgent.thrust(2);
				}
			}
		});
		winStats.addObject(btnSelectThrust);
		
		//kill poor agent
		btnSelectKill = new UIButton(this, 175, 115, 65, 20, "KILL");
		btnSelectKill.setColor(210, 50, 50);
		btnSelectKill.setEventHandler(new UIAction(){
			public void click(UIButton btn){
				if(selectedAgent != null){
					env.removeAgent(selectedAgent);
					selectedAgent = null;
				}
			}
		});
		winStats.addObject(btnSelectKill);
		
		//3D neural network visual
		neuralVisual = new UIDrawable3D(this, 0, 250, 250, 250);
		neuralVisual.setIsTop(false);
		neuralVisual.setBackground(30);
		neuralVisual.setFixedBackground(true);
		neuralVisual.setEventHandler(new UIAction(){
			public void draw(PApplet canvas){
				if(selectedAgent == null) return;
				
				MNetwork net = selectedAgent.getNetwork();
				HashMap<MNeuron, Point2D.Double> placed = new HashMap<MNeuron, Point2D.Double>(); //store coordinates of placed neurons
			    HashMap<Integer, Integer> maxInLevel = new HashMap<Integer, Integer>(); //store max y coordinate at each level of tree
			    maxInLevel.put(0, 0);
			    int maxLevel = 0;
				
			    rotateY(neuralRotation);
			    
			    //TODO: these positions of nodes can be precalculated when network is generated.
			    for(MSynapse s : net.getSynapses()){ //determine the x, y coordinates for each node based on links
			    	MNeuron pre = s.getPreNeuron();
			    	MNeuron post = s.getPostNeuron();
			    	int level = 0;
			    	
			    	if(!placed.containsKey(pre)){
			    		if(placed.containsKey(post)){ //pre node not placed but post is, place at level - 1
			    			level = (int) (placed.get(post).x / 20) - 1;
			    		}
			    		
			    		int max = maxInLevel.get(level);
			    		Point2D.Double n1 = new Point2D.Double(level * 20, max);
			    		maxInLevel.put(level, max + 20);
			    		placed.put(pre, n1);
			    		
			    		if(!maxInLevel.containsKey(level+1)) {
			    			maxInLevel.put(level+1, 0);
			    			maxLevel++;
			    		}
			    	}
			    	
			    	if(!placed.containsKey(post)){
			    		level++;
			    		int max = maxInLevel.get(level);
			    		Point2D.Double n2 = new Point2D.Double(level * 20, max);
			    		maxInLevel.put(level, max + 20);
			    		placed.put(post, n2);
			    	}
			    }
			    
			    int offsetX = -maxLevel * 10;
			    noStroke();
			    for(MNeuron n : placed.keySet()){ //draw the neurons
			    	if(n.isFiring()) fill(theme.getColor("NeuronFired"));
			    	else fill(theme.getColor("Neuron"));
		    		
		    		translate((float) placed.get(n).x + offsetX, (float) placed.get(n).y, 0);
			    	sphere(3);
			    	translate((float) -(placed.get(n).x + offsetX), (float) -placed.get(n).y, 0);
			    }
			    
			    for(MSynapse s : net.getSynapses()){ //draw the links between the neurons
			    	Point2D.Double n1 = placed.get(s.getPreNeuron());
			    	Point2D.Double n2 = placed.get(s.getPostNeuron());
			    	
			    	if(s.getPreNeuron().isFiring()) stroke(0, 255, 0);
			    	else stroke(255);
			    	line((int) (n1.x + offsetX), (int) n1.y, 0, (int) (n2.x + offsetX), (int) n2.y, 0);
			    }
			    
			    neuralRotation -= 0.02;
			}
		});
		sidePanel.addObject(neuralVisual);
		
		//printout of selected agents stats
		lblStatTitle = addStatLabel("Selected Agent", 5);
		lblX = addStatLabel("X", 155);
		lblY = addStatLabel("X", 170);
		lblHeading = addStatLabel("X", 185);
		lblHealth = addStatLabel("X", 200);
		lblAngle = addStatLabel("X", 215);
		lblSpeed = addStatLabel("X", 230);
		
		//Themes window
		theme = new UITheme();
		theme.setColor("Background", color(0));
		theme.setColor("Sidepanel", color(50));
		theme.setColor("Food", color(0, 255, 0));
		theme.setColor("Agent", color(255, 127, 0));
		theme.setColor("Neuron", color(200));
		theme.setColor("NeuronFired", color(0, 255, 0));
		
		winTheme = new UIWindow(this, 0, 485, 200, 200);
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
		
		//adds mouse scrolling listener to the applet
		addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent event){
				mouseWheel(event);
			}
		});
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
    			case Collision.TYPE_WALL: bounceAgent(cc); break;
    		}
		}
	}

	private void bounceAgent(Collision cc) {
		Agent ag = cc.getAgent();
		Line2D.Double line = ((Wall) cc.getCollidedObject()).getLine();
		double lineAngle = Utilities.angleBetweenPoints(line.x1, line.y1, line.x2, line.y2);
		double normalAngle = lineAngle + 90;
		double agentAngle = ag.getMovingAngle();
		double newAngle = normalAngle + (normalAngle - agentAngle - 180);
		double oldHeading = ag.getViewHeading();
		double thrust = ag.getMovingSpeed();
		
		ag.stop();
		ag.changeViewHeading(newAngle - ag.getViewHeading());
		ag.thrust(thrust);
		ag.changeViewHeading(oldHeading - newAngle);
		ag.updateHealth(thrust / -100);
	}

	private void eatFood(Collision cc) {
		Food fd = (Food) cc.getCollidedObject();
		
		env.removeFood(fd);
		cc.getAgent().updateHealth(fd.getValue());
	}
	

}
