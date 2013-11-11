package group7.anemone.UI;

import processing.core.PApplet;

public class UIAngle extends UIObject{
	private double angle = 0;

	public UIAngle(PApplet c, int x, int y, int r){
		super(c, x, y, r, r);
	}

	public double getAngle(){
		return angle;
	}
	public void setAngle(double ang){
		angle = ang;
	}

	public void draw(){
		canvas.noFill();
		canvas.stroke(r, g, b);
		canvas.arc(x + width / 2, y + height / 2, width, height, 0, (float) (2*Math.PI));

		canvas.pushMatrix();
		canvas.translate(x + width / 2, y + height / 2);
		canvas.rotate((float) ((angle - 90) * Math.PI / 180));
		canvas.line(0, 0, 0, width / 2);
		canvas.popMatrix();
	}

	public boolean mousePressed(){
		return checkMouseChange();
	}
	public boolean mouseDragged(){
		return checkMouseChange();
	}

	private boolean checkMouseChange(){
		double mx = x + width / 2;
		double my = y + height / 2;
		double distance = Utilities.distanceBetweenPoints(mx, my, canvas.mouseX, canvas.mouseY);
		if(distance <= width / 2){
			angle = Utilities.angleBetweenPoints(mx, my, canvas.mouseX, canvas.mouseY);

			if(events != null) {
				events.change(this);
			}
			return true;
		}

		return false;
	}
}
