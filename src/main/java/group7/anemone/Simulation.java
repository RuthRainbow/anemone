package group7.anemone;

import group7.anemone.MNetwork.MNetwork;
import group7.anemone.MNetwork.MNeuron;
import group7.anemone.MNetwork.MSynapse;
import group7.anemone.MNetwork.MVec3f;
import group7.anemone.UI.UIAction;
import group7.anemone.UI.UIAngle;
import group7.anemone.UI.UIButton;
import group7.anemone.UI.UIColorWheel;
import group7.anemone.UI.UIDrawable;
import group7.anemone.UI.UIDrawable3D;
import group7.anemone.UI.UIDropdown;
import group7.anemone.UI.UILabel;
import group7.anemone.UI.UIProgress;
import group7.anemone.UI.UISlider;
import group7.anemone.UI.UITheme;
import group7.anemone.UI.UIVision;
import group7.anemone.UI.UIWindow;
import group7.anemone.UI.Utilities;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PFont;

// We aren't going to serialise this.
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
	UIVision agentHeading;
	UILabel lblStatTitle, lblX, lblY, lblHeading, lblHealth, lblAngle, lblSpeed;
	UIDropdown themeDrop;
	UIColorWheel themeColorWheel;
	UITheme theme;
	UIProgress progHealth;
	UISlider sliderX, sliderY;
	UIDrawable3D neuralVisual;

	//size of the simulation environment
	int width = 1000;
	int height = 750;
	//size of the drawable region for the simulator
	int draw_width = 0;
	int draw_height = 0;
	//offset of the simulation drawing
	int offsetX = 50;
	int offsetY = 50;

	float neuralRotation = 0;
	float zoomLevel = 1;
	boolean arrowsPressed[] = new boolean[4];
	int moveSpeed = 50;
	float minZoom = 0.2f;

	public static void main(String args[]){
		// Run the applet when the Java application is run
		PApplet.main(new String[] { "--present", "group7.anemone.Simulation" });
	}

	public void setup() {
		frameRate(30);
		size(screen.width, screen.height, P3D);
		textMode(SCREEN);
		setupUI();

		draw_width = screen.width - 250;
		draw_height = screen.height;

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
		env.addWall(new Point2D.Double(0,0), new Point2D.Double(0,height));
	}
	public void mousePressed(){
		ArrayList<Agent> agents = env.getAllAgents();
		Agent agent_clicked = null;

		if(win.mousePressed()) return;
		if(!Utilities.isPointInBox(mouseX, mouseY, 0, 0, draw_width, draw_height)) return;

		/*
		 * Mouse Modes are as follows:
		 * 0 = Click tool - Select agents to see information on them in the top left hand corner
		 * 1 = Food tool - Place food where you click
		 */

		//coordinates of the mouse within the simulation environment
		int simMouseX = (int) ((float) (mouseX - offsetX) / zoomLevel);
		int simMouseY = (int) ((float) (mouseY - offsetY) / zoomLevel);
		if(!Utilities.isPointInBox(simMouseX, simMouseY, 0, 0, width, height)) return;
		
		switch(mouseMode){
		case 0: agent_clicked = getClickedAgent(agents, simMouseX, simMouseY);
				if(agent_clicked != null){ //agent was clicked so update selected
					selectedAgent = agent_clicked;
				}
				break;

		case 1: env.addFood(new Point2D.Double(simMouseX, simMouseY));
				break;

		case 2: int heading = (int) Math.floor(Math.random() * 360);
				env.addFish(new Point2D.Double(simMouseX, simMouseY), heading);
				break;
		case 3: agent_clicked = getClickedAgent(agents, simMouseX, simMouseY);
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
		
		if(!Utilities.isPointInBox(mouseX, mouseY, 0, 0, draw_width, draw_height)) return;
		
		if(zoomLevel > minZoom || event.getWheelRotation() > 0){
			zoomLevel = Math.max(minZoom, (zoomLevel + 0.1f * event.getWheelRotation()));
			offsetX -= (int) (((mouseX - offsetX) * (0.1f * event.getWheelRotation()))) / zoomLevel;
			offsetY -= (int) (((mouseY - offsetY) * (0.1f * event.getWheelRotation()))) / zoomLevel;
		}
	}
	public void keyReleased(){	//Hotkeys for buttons
		if(win.keyReleased()) return;
		
		if(!Utilities.isPointInBox(mouseX, mouseY, 0, 0, draw_width, draw_height)) return;

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

		switch(keyCode) {
			case(UP):	arrowsPressed[0] = false;
						break;
			case(DOWN):	arrowsPressed[1] = false;
						break;
			case(LEFT):	arrowsPressed[2] = false;
						break;
			case(RIGHT):arrowsPressed[3] = false;
						break;
		}
	}
	public void keyPressed(){	//Hotkeys for buttons
		if(win.keyPressed()) return;
		if(!Utilities.isPointInBox(mouseX, mouseY, 0, 0, draw_width, draw_height)) return;
		
		switch(keyCode) {
			case(UP):	arrowsPressed[0] = true;
						break;
			case(DOWN):	arrowsPressed[1] = true;
						break;
			case(LEFT):	arrowsPressed[2] = true;
						break;
			case(RIGHT):arrowsPressed[3] = true;
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
		env.killOutsideAgents(width, height);
		checkDeaths();
		updateUI();

		//move drawn region
		if(arrowsPressed[0] && !arrowsPressed[1]) offsetY -= moveSpeed * zoomLevel; //UP
		if(arrowsPressed[1] && !arrowsPressed[0]) offsetY += moveSpeed * zoomLevel; //DOWN
		if(arrowsPressed[2] && !arrowsPressed[3]) offsetX -= moveSpeed * zoomLevel; //LEFT
		if(arrowsPressed[3] && !arrowsPressed[2]) offsetX += moveSpeed * zoomLevel; //RIGHT

		win.draw();

		fill(255);
		text("FrameRate: " + frameRate, 10, 10);	//Displays framerate in the top left hand corner
	}

	private void drawSimulation(PApplet canvas){
		pushMatrix();
		translate(offsetX, offsetY);
		scale(zoomLevel);

		ArrayList<Agent> agents = env.getAllAgents();	//Returns an arraylist of agents
		ArrayList<Food> food = env.getAllFood();		//Returns an arraylist of all the food on the map
		ArrayList<Wall> walls = env.getAllWalls();		//Returns an arraylist of all walls

		for(int i = 0; i < agents.size(); i++){ //Runs through arraylist of agents, will draw them on the canvas
			Agent ag = agents.get(i);

			//draw the field of view for the agent
			stroke(128); //, (float) ag.getHealth()*200+55
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

		stroke(theme.getColor("Wall"));
		noFill();
		for(Wall wl : walls){ //Runs through arraylist of walls, will draw them on the canvas
			line((float) wl.getStart().x, (float) wl.getStart().y, (float) wl.getEnd().x, (float) wl.getEnd().y);
		}

		popMatrix();

		fill(0);
		//rect(draw_width - 50, 0, 250, draw_height);
	}

	private void updateUI(){
		agentHeading.setVisible(selectedAgent != null);
		winStats.setVisible(selectedAgent != null);

		if(selectedAgent != null){
			//agentHeading.setAngle(selectedAgent.getViewHeading());
			agentHeading.setAgent(selectedAgent);
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
		//Set colors of each element of simulation
		theme = new UITheme();
		theme.setColor("Background", color(0));
		theme.setColor("Sidepanel", color(50));
		theme.setColor("Food", color(0, 255, 0));
		theme.setColor("Agent", color(255, 127, 0));
		theme.setColor("Wall", color(255));
		theme.setColor("Neuron", color(200));
		theme.setColor("NeuronFired", color(0, 255, 0));

		//setup main window for UI elements
		win = new UIWindow(this, 0, 0, screen.width, screen.height);
		sidePanel = new UIWindow(this, 250, 0, 250, screen.height);
		sidePanel.setIsLeft(false);
		sidePanel.setBackground(50);
		sidePanel.setFixedBackground(true);

		neuralVisual = new UIDrawable3D(this, 0, 250, 250, 250);
		sidePanel.addObject(neuralVisual);

		//Simulation draw region
		UIDrawable sim = new UIDrawable(this, 0, 0, draw_width, draw_height);
		sim.setBackground(color(255));
		sim.setFixedBackground(true);
		sim.setEventHandler(new UIAction(){
			public void draw(PApplet canvas){
				drawSimulation(canvas);
			}
		});
		win.addObject(sim);
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
		winStats = new UIWindow(this, 0, 165, 300, 250);
		winStats.setBackground(30);
		winStats.setFixedBackground(true);
		sidePanel.addObject(winStats);

		//control to change the selected agents heading
		agentHeading = new UIVision(this, 10, 30, 50);
		agentHeading.setTheme(theme);
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
		
		neuralVisual.setIsTop(false);
		neuralVisual.setBackground(30);
		neuralVisual.setFixedBackground(true);
		neuralVisual.setEventHandler(new UIAction(){
			private HashMap<MNeuron, MVec3f> placed;
		    private HashMap<Integer, Integer> maxInLevel;
		    private int maxLevel = 0;
		    private int maxHeight = 0;
		    private float zoom = 1f;
		    private int offX = 0;
		    private int offY = -50;
		    boolean arrows[] = new boolean[4];
			public void draw(PApplet canvas){
				if(selectedAgent == null) return;
				
				if(arrows[0] && !arrows[1]) offY -= moveSpeed/3; //UP
				if(arrows[1] && !arrows[0]) offY += moveSpeed/3; //DOWN

				MNetwork net = selectedAgent.getNetwork();
				placed = new HashMap<MNeuron, MVec3f>(); //store coordinates of placed neurons
			    maxInLevel = new HashMap<Integer, Integer>(); //store max y coordinate at each level of tree
			    maxInLevel.put(0, 0);
			    maxLevel = 0;
			    maxHeight = 0;

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

			    		int max = maxValue(level);
			    		addNode(level, max, pre);
			    	}

			    	if(!placed.containsKey(post)){
			    		if(placed.containsKey(pre)){ //post node not placed but pre is, place at level + 1
			    			level = (int) (placed.get(pre).x / 20);
			    		}
			    		level++;

                        int max = maxValue(level);
			    		addNode(level, max, post);
			    	}
			    	
			    	if(placed.get(pre).x == placed.get(post).x) placed.get(pre).z += 20;
			    }

			    
			    int offsetX = -maxLevel * 10;
			    int offsetY = -maxHeight * 10;
			    noStroke();
			    scale(zoom, zoom, zoom);
			    for(MNeuron n : placed.keySet()){ //draw the neurons
			    	if(n.isFiring()) fill(theme.getColor("NeuronFired"));
			    	else fill(theme.getColor("Neuron"));

		    		translate((float) placed.get(n).x + offsetX + offX, (float) placed.get(n).y + offsetY + offY, placed.get(n).z);
			    	sphere(3);
			    	translate((float) -(placed.get(n).x + offsetX + offX), (float) -(placed.get(n).y + offsetY + offY), -placed.get(n).z);
			    }

			    for(MSynapse s : net.getSynapses()){ //draw the links between the neurons
			    	MVec3f n1 = placed.get(s.getPreNeuron());
			    	MVec3f n2 = placed.get(s.getPostNeuron());

			    	if(s.getPreNeuron().isFiring()) stroke(0, 255, 0);
			    	else stroke(255, 20);
			    	line((int) (n1.x + offsetX + offX), (int) (n1.y + offsetY + offY), (int) n1.z, (int) (n2.x + offsetX + offX), (int) (n2.y + offsetY + offY), (int) n2.z);
			    }

			    neuralRotation -= 0.02;
			}
			private void addNode(int level, int max, MNeuron node){
				MVec3f n1 = new MVec3f(level * 20, max, 0);
				maxInLevel.put(level, max + 20);
				placed.put(node, n1);
			}
			private int maxValue(int level){
				int max = 0;
				if(!maxInLevel.containsKey(level)) {
					maxInLevel.put(level, 0);
					maxLevel++;
				}else max = maxInLevel.get(level);
				maxHeight = Math.max(maxHeight, (max/20));
				return max;
			}
			
			public boolean mouseWheel(MouseWheelEvent event){
				if(!Utilities.isPointInBox(mouseX, mouseY, screen.width - 250, screen.height - 250, 250, 250)) return false;
				
				if(zoom > minZoom || event.getWheelRotation() > 0){
					zoom = Math.max(minZoom, (zoom + 0.1f * event.getWheelRotation()));
					//offX -= (int) (((mouseX - offX) * (0.1f * event.getWheelRotation()))) / zoom;
					//offY -= (int) (((mouseY - offY) * (0.1f * event.getWheelRotation()))) / zoom;
				}
				
				return true;
			}
			
			public boolean keyReleased(){	//Hotkeys for buttons
				if(!Utilities.isPointInBox(mouseX, mouseY, screen.width - 250, screen.height - 250, 250, 250)) return false;
				
				switch(keyCode) {
					case(UP):	arrows[0] = false;
								return true;
					case(DOWN):	arrows[1] = false;
								return true;
					case(LEFT):	arrows[2] = false;
								return true;
					case(RIGHT):arrows[3] = false;
								return true;
				}
				return false;
			}
			public boolean keyPressed(){	//Hotkeys for buttons
				if(!Utilities.isPointInBox(mouseX, mouseY, screen.width - 250, screen.height - 250, 250, 250)) return false;
				
				switch(keyCode) {
					case(UP):	arrows[0] = true;
								return true;
					case(DOWN):	arrows[1] = true;
								return true;
					case(LEFT):	arrows[2] = true;
								return true;
					case(RIGHT):arrows[3] = true;
								return true;
				}
				return false;
			}
		});
		

		//printout of selected agents stats
		lblStatTitle = addStatLabel("Selected Agent", 5);
		lblX = addStatLabel("X", 155);
		lblY = addStatLabel("X", 170);
		lblHeading = addStatLabel("X", 185);
		lblHealth = addStatLabel("X", 200);
		lblAngle = addStatLabel("X", 215);
		lblSpeed = addStatLabel("X", 230);

		//Themes window
		winTheme = new UIWindow(this, 0, 485, 250, 200);
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
    			case Collision.TYPE_AGENT: breeding(cc); break;
    		}
		}
	}

	// If two agents collided, breed with some random chance.
	private void breeding(Collision cc) {
		if (Math.random() > 0.8) {
			env.Breed(cc.getAgent(), (Agent) cc.getCollidedObject());
			// Encourage agents to breed...
			cc.getAgent().updateFitness(0.01);
		}
		Agent agent1 = cc.getAgent();
		Agent agent2 = (Agent) cc.getCollidedObject();
		bounceAgents(agent1, agent2);
		/*Point2D.Double midpoint = new Point2D.Double((agent1.coords.x+agent2.coords.x)/2,(agent1.coords.y+agent2.coords.y)/2);
		Wall bisector = new Wall(Utilities.generateLine(midpoint, 10, Utilities.angleBetweenPoints(agent1.coords.x, agent1.coords.y, agent2.coords.x, agent2.coords.y)+90));
		bounceAgent(new Collision(agent1,bisector));
		bounceAgent(new Collision(agent2, bisector));*/
	}

	private void bounceAgents(Agent agent1, Agent agent2) {
		Point2D.Double midpoint = new Point2D.Double((agent1.coords.x+agent2.coords.x)/2,(agent1.coords.y+agent2.coords.y)/2);
		double dist = agent1.coords.distance(agent2.coords);

		double changeX1 = agent1.coords.x - agent2.coords.x;
		double changeY1 = agent1.coords.y - agent2.coords.y;
		double changeX2 = agent2.coords.x - agent1.coords.x;
		double changeY2 = agent2.coords.y - agent1.coords.y;

		agent1.coords.x = midpoint.x + 10 * (changeX1 / dist);
		agent1.coords.y = midpoint.y + 10 * (changeY1 / dist);
		agent2.coords.x = midpoint.x + 10 * (changeX2 / dist);
		agent2.coords.y = midpoint.y + 10 * (changeY2 / dist);

		/*Line2D.Double line = new Line2D.Double(agent1.coords,new Point2D.Double(agent1.coords.x+agent1.getChangeX(),agent1.coords.y+agent1.getChangeY()));


		Point2D.Double closestPoint = Utilities.getClosestPoint(line, agent2.coords);
		double closestDistSq = Math.pow(agent2.coords.x - closestPoint.x, 2) + Math.pow((agent2.coords.y - closestPoint.y), 2);

		double backdist = Math.sqrt(Math.pow(20, 2) - closestDistSq);
		double movementvectorlength = Math.sqrt(Math.pow(agent1.getChangeX(), 2) + Math.pow(agent1.getChangeY(), 2));
		double c_x = closestPoint.x - backdist * (agent1.getChangeX() / movementvectorlength);
		double c_y = closestPoint.y - backdist * (agent1.getChangeY() / movementvectorlength);

		double collisiondist = Math.sqrt(Math.pow(agent2.coords.x - c_x, 2) + Math.pow(agent2.coords.y - c_y, 2));
		double n_x = (agent2.coords.x - c_x) / collisiondist;
		double n_y = (agent2.coords.y - c_y) / collisiondist;
		double p = 2 * (agent1.getChangeX() * n_x + agent1.getChangeY() * n_y) / (10);
		double w_x = agent1.getChangeX() - p * 5 * n_x - p * 5 * n_x;
		double w_y = agent2.getChangeY() - p * 5 * n_y - p * 5 * n_y;


		agent1.stop();
		double previousHeading = agent1.getViewHeading();
		double thrust = Math.sqrt(w_x*w_x + w_y*w_y);
		double newAngle = Math.atan(w_y/w_x)* Math.PI / 180;
		agent1.changeViewHeading(newAngle - agent1.getViewHeading());
		agent1.thrust(thrust);
		agent1.changeViewHeading(previousHeading - newAngle);
		agent1.updateHealth(thrust / -100);
		agent1.updateFitness(thrust / -100);*/

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


		Wall wl = (Wall) cc.getCollidedObject();
		double distanceToWall = wl.getLine().ptLineDist(ag.coords);
		double thrustIncrease = (10-distanceToWall)/100 + 1;
		boolean leftWall = wl.getLine().ptLineDist(new Point2D.Double(0,height/2)) == 0;
		boolean rightWall = wl.getLine().ptLineDist(new Point2D.Double(width,height/2)) == 0;
		boolean topWall = wl.getLine().ptLineDist(new Point2D.Double(width/2,0)) == 0;
		boolean bottomWall = wl.getLine().ptLineDist(new Point2D.Double(width/2,height)) == 0;

		/*double m = (line.y2-line.y1)/(line.x2-line.x1);
		double c = line.y1 - m*line.x1;
		boolean mInvalid = (m == java.lang.Double.POSITIVE_INFINITY || m == java.lang.Double.NEGATIVE_INFINITY || java.lang.Double.isNaN(m));
		boolean cInvalid = (c == java.lang.Double.POSITIVE_INFINITY || c == java.lang.Double.NEGATIVE_INFINITY || java.lang.Double.isNaN(c));

		double r = 10;
		double a = ag.coords.x;
		double b = ag.coords.y;
		double x = 1 + m;
		double y = 2*m*(c-b);
		double z = (c-b)*(c-b) + a*a +2*a - r*r;
		double[] Xcoord = Utilities.quadratic(x, y, z);
		double[] Ycoord = new double[2];
		Ycoord[0] = m*Xcoord[0] + c;
		Ycoord[1] = m*Xcoord[1] + c;
		System.out.println("***************************************************************************");
		if(leftWall) System.out.println("Collided with left wall");
		else if (rightWall) System.out.println("Collided with right wall");
		else if (topWall) System.out.println("Collided with top wall");
		else if (bottomWall) System.out.println("Collided with bottom wall");
		else System.out.println("Collided with agent");
		System.out.println("Original position: "+ag.coords.x+" "+ag.coords.y);
		Point2D.Double closestWallPoint = Utilities.getClosestPoint(line, ag.coords);
		System.out.println("Closest wall point: "+closestWallPoint.x+" "+closestWallPoint.y);
		double wallToNewPositionAngle = Utilities.angleBetweenPoints(closestWallPoint.x, closestWallPoint.y, ag.coords.x, ag.coords.y);
		Point2D.Double newAgentPosition = (Double) Utilities.generateLine(closestWallPoint, 10, wallToNewPositionAngle).getP2();
		ag.coords = newAgentPosition;
		System.out.println("New position: "+ag.coords.x+" "+ag.coords.y);
		System.out.println("Distance to wall after: "+wl.getLine().ptLineDist(ag.getCoordinates()));*/


		if(leftWall) ag.coords.x += (10 - distanceToWall);
		else if (rightWall) ag.coords.x -= (10 - distanceToWall);
		else if (topWall) ag.coords.y += (10 - distanceToWall);
		else if (bottomWall) ag.coords.y -= (10 - distanceToWall);


		ag.stop();
		ag.changeViewHeading(newAngle - ag.getViewHeading());
		ag.thrust(thrustIncrease * thrust);
		ag.changeViewHeading(oldHeading - newAngle);
		ag.updateHealth(thrust / -100);
		ag.updateFitness(thrust / -100);
	}

	private void eatFood(Collision cc) {
		Food fd = (Food) cc.getCollidedObject();

		env.removeFood(fd);
		cc.getAgent().updateHealth(fd.getValue());
		cc.getAgent().updateFitness(fd.getValue());
	}

	private Agent getClickedAgent(ArrayList<Agent> agents, int mx, int my){
		Agent agent_clicked = null;
		for(int i = 0; i < agents.size(); i++){ //loop through each agent and find one clicked
			Agent ag = agents.get(i);
			if(Math.sqrt(Math.pow(mx - ag.getX(), 2) + Math.pow(my - ag.getY(), 2)) < 10){
				agent_clicked = ag;
				break;
			}
		}
		return agent_clicked;
	}
}
