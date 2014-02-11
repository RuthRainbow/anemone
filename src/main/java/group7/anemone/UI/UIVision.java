package group7.anemone.UI;

import java.util.ArrayList;

import group7.anemone.Agent;
import group7.anemone.SightInformation;
import group7.anemone.UI.UITheme.Types;
import processing.core.PApplet;

public class UIVision extends UIAngle{
	private Agent agent;
	
	public UIVision(PApplet c, int x, int y, int r){
		super(c, x, y, r);
	}
	
	public void setAgent(Agent ag){
		agent = ag;
	}

	public void draw(){
		if(agent == null) return;
		
		int centerX = x + width / 2;
		int centerY = y + height / 2;
		
		canvas.noFill();
		canvas.stroke(r, g, b);
		canvas.arc(centerX, centerY, width, height, 0, (float) (2*Math.PI));

		canvas.stroke(r, g, b, 20);
		Utilities.line(canvas, centerX, centerY, width / 2, agent.getViewHeading());
		
		canvas.stroke(r, g, b, 150);
		Utilities.line(canvas, centerX, centerY, width / 2, agent.getViewHeading() - agent.getFOV());
		Utilities.line(canvas, centerX, centerY, width / 2, agent.getViewHeading() + agent.getFOV());
		
		ArrayList<SightInformation> vision = agent.getAllViewingObjects();
		canvas.noStroke();
		for(SightInformation see : vision){
			if(theme != null){
				switch(see.getType()){
					case SightInformation.TYPE_FOOD: canvas.fill(theme.getColor(Types.FOOD)); break;
					case SightInformation.TYPE_AGENT: canvas.fill(theme.getColor(Types.FISH)); break;
					case SightInformation.TYPE_ENEMY: canvas.fill(theme.getColor(Types.SHARK)); break;
					case SightInformation.TYPE_WALL: canvas.fill(theme.getColor(Types.WALL)); break;
					default: canvas.fill(r, g, b);
				}
			}else canvas.fill(r, g, b);
			
			double ang = agent.getViewHeading() - agent.getFOV() + (agent.getFOV() * 2 * see.getDistanceFromLower());
			Utilities.pointAtAngle(canvas, centerX, centerY, see.getNormalisedDistance() * width / 2, ang, (int) Math.ceil(width / 100.0));
		}
	}

	public boolean mousePressed(){
		return super.mousePressed();
	}
	public boolean mouseDragged(){
		return super.mouseDragged();
	}
}
