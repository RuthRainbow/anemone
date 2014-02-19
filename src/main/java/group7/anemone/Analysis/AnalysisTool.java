package group7.anemone.Analysis;

import group7.anemone.Agent;
import group7.anemone.Collision;
import group7.anemone.Enemy;
import group7.anemone.Environment;
import group7.anemone.SightInformation;
import group7.anemone.MNetwork.MNetwork;
import group7.anemone.MNetwork.MNeuron;
import group7.anemone.MNetwork.MSynapse;
import group7.anemone.MNetwork.MVec3f;
import group7.anemone.UI.UIAction;
import group7.anemone.UI.UIDrawable;
import group7.anemone.UI.UIDrawable3D;
import group7.anemone.UI.UIListView;
import group7.anemone.UI.UITheme;
import group7.anemone.UI.UITheme.Types;
import group7.anemone.UI.UIWindow;
import group7.anemone.UI.Utilities;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class AnalysisTool extends PApplet {
	private PFont f = createFont("Arial",12,true);
	private boolean arrowsPressed[] = new boolean[4];
	
	//UI Window Panel
	private UIWindow win;
	
	//UI Elements
	private UITheme theme;
	private UIDrawable3D neuralVisual;
	private UIDrawable agentDrawing;
	private UIListView<Agent> listOfAgents;

	//Neural Network Visualisation
	private boolean useLayeredView = true;
	private float neuralRotation = 0;
	private float zoomLevel = 2.0f;
	private int moveSpeed = 50;
	private float minZoom = 0.15f;
	
	private Environment env;
	private Agent selectedAgent;
	private int selectedSegment = -1;
	private double normalisedDistance = 0;
	
	private boolean DEV_MODE = false;

	public static void main(String args[]){
		PApplet.main(new String[] { "--present", "group7.anemone.Analysis.AnalysisTool" });
	}

	public void setup() {
		frameRate(30);
		size(screen.width, screen.height, P3D);
		textMode(SCREEN);
		textFont(f);
		setupUI();
		
		if(DEV_MODE){
			File file = new File(System.getProperty("user.home") + "/anemone/save/MoreSegs.env");
			loadEnvironmentFile(file);
		}else{
			restoreEnvironment();
		}
	}
	public void mousePressed(){
		if(win.mousePressed()) return;

		
	}
	public void mouseReleased(){
		if(win.mouseReleased()) return;

		
	}

	public void mouseDragged(){
		if(win.mouseDragged()) return;

		
	}
	public void mouseMoved(){
		double posX = screen.width - 230;
		double posY = 200;
		double dist = new Point2D.Double(posX, posY).distance(mouseX, mouseY);
		double angleBetween = Math.atan((mouseY - posY) / (mouseX - posX));
		angleBetween = angleBetween * 180 / Math.PI;
		
		//adjust angles depending on quadrant to be represented in 0-360 rather than -180-180
		if(mouseX > posX) {
			if (mouseY < posY) angleBetween = 360 + angleBetween;
		}else{
			if (mouseY >= posY) angleBetween = 180 + angleBetween;
			else angleBetween += 180;
		}
		
		double fov = selectedAgent.getFOV();
		int numSegments = selectedAgent.configNumSegments;
		if(dist <= selectedAgent.getVisionRange() && angleBetween < fov){
			selectedSegment = (int) Math.floor(numSegments * ((angleBetween + fov) / (fov * 2)));
		}else if(dist <= selectedAgent.getVisionRange() && angleBetween > 360 - fov){
			selectedSegment = (int) Math.floor(numSegments * (angleBetween - (360 - fov)) / (fov * 2));
		}else{
			selectedSegment = -1;
		}
		
		normalisedDistance = dist / selectedAgent.getVisionRange();
	}
	public void mouseWheel(MouseWheelEvent event){
		if(win.mouseWheel(event)) return;

		
	}
	public void keyReleased(){	//Hotkeys for buttons
		if(win.keyReleased()) return;
		
		switch(key) {
			case('o'): restoreEnvironment(); break;
		}
	}
	public void keyPressed(){	//Hotkeys for buttons
		if(win.keyPressed()) return;
		
		
	}

	public void draw(){
		background(theme.getColor(Types.BACKGROUND));
		
		updateAgentBrains();
		
		win.draw();
	}
	
	private void updateAgentBrains(){
		for(Agent ag : env.getAllAgents()){
			ag.updateBrainOnly();
		}
	}
	
	
	private void loadEnvironment(){
		Agent.configNumSegments = env.agentNumSegments;
		selectedAgent = env.getAllAgents().get(0);
		
		listOfAgents.clear();
		for(Agent ag : env.getAllAgents()){
			listOfAgents.addItem((ag.getType() == Collision.TYPE_ENEMY ? "Enemy" : "Agent"), 
					"Fitness: " + (Math.round(ag.getFitness() * 100) / 100.0) + 
					"\nAge: " + (ag.getAge()), ag);
			ag.updateCanSee(new ArrayList<SightInformation>());
		}
	}
	private void setupUI(){
		//Set colors of each element of simulation
		theme = new UITheme();
		theme.setColor(Types.BACKGROUND, color(0));
		theme.setColor(Types.SIDEPANEL1, color(50));
		theme.setColor(Types.FOOD, color(0, 255, 0));
		theme.setColor(Types.FISH, color(255, 127, 0));
		theme.setColor(Types.SHARK, color(255, 0, 0));
		theme.setColor(Types.WALL, color(255, 255, 0));
		theme.setColor(Types.NEURON, color(200));
		theme.setColor(Types.NEURON_FIRED, color(0, 255, 0));

		//setup main window for UI elements
		win = new UIWindow(this, 0, 0, screen.width, screen.height);
		
		neuralVisual = new UIDrawable3D(this, 250, 0, screen.width - 500, screen.height);
		neuralVisual.setBackground(30);
		neuralVisual.setFixedBackground(true);
		win.addObject(neuralVisual);
		
		listOfAgents = new UIListView<Agent>(this, 0, 0, 250, screen.height);
		listOfAgents.setEventHandler(new UIAction(){
			public void change(UIListView list){
				selectedAgent = (Agent) list.getSelected();
			}
			public void draw(PApplet canvas, Object value){
				Agent ag = (Agent) value;
				int species = ag.getSpeciesId() + 1;
				int posX = 20;
				int posY = 10;
				
				noStroke();
				fill((species * 25) % 256, (species * 47) % 256, (species * 69) % 256);
				if (ag instanceof Enemy) {
					rect(posX - 10, posY - 10, 20, 20);
				}else{
					ellipse(posX, posY, 20, 20);
				}
			}
		});
		win.addObject(listOfAgents);
		
		//AGENT DRAWING
		agentDrawing = new UIDrawable(this, 250, 0, 250, 400);
		agentDrawing.setBackground(20);
		agentDrawing.setFixedBackground(true);
		agentDrawing.setIsLeft(false);
		agentDrawing.setEventHandler(new UIAction(){
			public void draw(PApplet canvas){
				if(selectedAgent == null) return;
				
				int species = selectedAgent.getSpeciesId() + 1;
				double range = selectedAgent.getVisionRange() * 2;
				double fov = selectedAgent.getFOV();
				int numSegments = Agent.configNumSegments;
				int posX = 20;
				int posY = 200;
				
				noFill();
				strokeWeight(1);
				
				for(int i = 0; i <= numSegments; i++){
					if(i == 0 || i == numSegments) stroke(128);
					else stroke(128, 125);
					pushMatrix();
					translate(posX, posY);
					rotate((float) Utilities.toRadians(-fov + (i * fov * 2 / numSegments)));
					line(0, 0, (int) (range / 2), 0);
					popMatrix();
					
					if(i == selectedSegment){
						fill(theme.getColor(Types.FOOD), 100);
						arc(posX, posY, (float) range, (float) range, 
								(float) Utilities.toRadians(-fov + (i * fov * 2 / numSegments)), 
								(float) Utilities.toRadians(-fov + ((i + 1) * fov * 2 / numSegments)));
						noFill();
					}
				}
				
				arc(posX, posY, (float) range, (float) range, (float) Utilities.toRadians(-fov) , (float) Utilities.toRadians(fov));
				
				noStroke();
				fill((species * 25) % 256, (species * 47) % 256, (species * 69) % 256);
				if (selectedAgent instanceof Enemy) {
					rect(posX - 10, posY - 10, 20, 20);
				}else{
					ellipse(posX, posY, 20, 20);
				}
			}
		});
		win.addObject(agentDrawing);
		
		// NEURAL NETWORK VISUALISATION CODE
		neuralVisual.setEventHandler(new UIAction(){
		    private int offX = 0;
		    private int offY = 0;
		    boolean arrows[] = new boolean[4];
		    private boolean rotating = true;
		    
			public void draw(PApplet canvas){
				if(selectedAgent == null) return;

				if(arrows[0] && !arrows[1]) offY -= moveSpeed/3; //UP
				if(arrows[1] && !arrows[0]) offY += moveSpeed/3; //DOWN

				MNetwork net = selectedAgent.getNetwork();

			    noStroke();
			    pushMatrix();

			    rotateY(neuralRotation);
			    scale(zoomLevel, zoomLevel, zoomLevel);
			    translate(offX, offY);
			    for(MNeuron n : net.getNeurons()){ //draw the neurons
			    	int isFired = (n.isFiring() ? 255 : 60);
			    	if(n.getID() >= 3 && n.getID() < 3 + Agent.configNumSegments) fill(theme.getColor(Types.FOOD), isFired);
			    	else if(n.getID() >= 3 + Agent.configNumSegments && n.getID() < 3 + Agent.configNumSegments * 2) fill(theme.getColor(Types.WALL), isFired);
			    	else if(n.getID() >= 3 + Agent.configNumSegments * 2 && n.getID() < 3 + Agent.configNumSegments * 3) fill(theme.getColor(Types.SHARK), isFired);
			    	else if(n.getID() < 3) fill(0, 255, 255, isFired);
			    	else fill(theme.getColor(Types.NEURON), isFired);

			    	MVec3f vec = n.getCoords();
			    	//clip node if off the display
			    	//if((vec.y + offY) * zoom < -135) continue;

		    		translate(vec.x, vec.y, vec.z);
			    	sphere(3);
			    	translate(-vec.x, -vec.y, -vec.z);
			    }
			    
			    strokeWeight(zoomLevel * 2);
			    for(MSynapse s : net.getSynapses()){ //draw the links between the neurons
			    	MNeuron pre = s.getPreNeuron();
			    	MNeuron post = s.getPostNeuron();
			    	MVec3f n1 = pre.getCoords();
			    	MVec3f n2 = post.getCoords();

			    	//clip edge if both nodes above clipping
			    	//if((n1.y + offY) * zoom < -135
			    	//		&& (n2.y + offY) * zoom < -135) continue;

			    	int isFired = (pre.isFiring() ? 100 : 10);
			    	if(pre.getID() >= 3 && pre.getID() < 3 + Agent.configNumSegments) stroke(theme.getColor(Types.FOOD), isFired);
			    	else if(pre.getID() >= 3 + Agent.configNumSegments && pre.getID() < 3 + Agent.configNumSegments * 2) stroke(theme.getColor(Types.WALL), isFired);
			    	else if(pre.getID() >= 3 + Agent.configNumSegments * 2 && pre.getID() < 3 + Agent.configNumSegments * 3) stroke(theme.getColor(Types.SHARK), isFired);
			    	else if(pre.getID() < 3) stroke(0, 255, 255, isFired);
			    	else stroke(255, isFired);

			    	//partial clipping when one node if above line
			    	/*if((n1.y + offY) * zoom < -135){
			    		double t = (((-135 / zoom) - offY)-n2.y) / (n1.y - n2.y);
			    		int x = (int) ((int) (n2.x + t * (n1.x - n2.x)) / zoom);
			    		line((int) (x), (int) (-135 / zoom) - offY, 0, (int) (n2.x), (int) (n2.y), (int) n2.z);
			    	}else if((n2.y + offY) * zoom < -135){
			    		double t = (((-135.0 / zoom) - offY)-(n1.y)) / (double) ((n2.y - n1.y));
			    		int x = (int) (n1.x + t * (n2.x - n1.x));
			    		line((int) (n1.x), (int) (n1.y), (int) n1.z, (int) (x), (int) (-135 / zoom) - offY, 0);
			    	}else{*/
			    		line((int) n1.x, (int) n1.y, (int) n1.z, (int) n2.x, (int) n2.y, (int) n2.z);
			    	//}
			    }
			    noStroke();
			    popMatrix();
			    if(rotating) neuralRotation -= 0.02;
			}

			public boolean mouseWheel(MouseWheelEvent event){
				if(!Utilities.isPointInBox(mouseX, mouseY, 250, 0, screen.width - 500, screen.height)) return false;

				if(zoomLevel > minZoom || event.getWheelRotation() > 0){
					zoomLevel = Math.max(minZoom, (zoomLevel + 0.1f * event.getWheelRotation()));
				}

				return true;
			}
			public boolean mousePressed(){
				if(!Utilities.isPointInBox(mouseX, mouseY, 250, 0, screen.width - 500, screen.height)) return false;
				
				rotating = !rotating;
				return true;
			}

			public boolean keyReleased(){	//Hotkeys for buttons
				if(!Utilities.isPointInBox(mouseX, mouseY, 250, 0, screen.width - 500, screen.height)) return false;

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
				if(!Utilities.isPointInBox(mouseX, mouseY, 250, 0, screen.width - 500, screen.height)) return false;

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
		
		//adds mouse scrolling listener to the applet
		addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent event){
				mouseWheel(event);
			}
		});
	}
	
	private void restoreEnvironment(){
		JFileChooser diag = getFileChooser();
		if(diag.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

		File file = diag.getSelectedFile();
		loadEnvironmentFile(file);
	}
	
	private void loadEnvironmentFile(File file){
		if(!file.exists()){
			System.out.println("Save file does not exist" + file.getAbsolutePath());
			return;
		}

		try{
			FileInputStream input = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(input);
			env = (Environment) in.readObject();
			in.close();
			input.close();
			
			loadEnvironment();
			System.out.println("Environment loaded");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//Builds a file selection dialog box for saving / opening an environment
	private JFileChooser getFileChooser(){
		File file = new File(System.getProperty("user.home") + "/anemone/save/");
		file.mkdirs();

		JFileChooser diag = new JFileChooser(file);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Environment (.env)", "env");
		diag.setFileFilter(filter);
		diag.setDialogTitle("Open Environment");
		diag.setMultiSelectionEnabled(false);

		return diag;
	}
}
