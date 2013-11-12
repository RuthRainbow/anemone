package group7.anemone.UI;

import processing.core.PApplet;

public class UISlider extends UIObject{
	private double value = 0;

	public UISlider(PApplet c, int x, int y, int w, int h){
		super(c, x, y, w, h);
	}

	public double getValue(){
		return value;
	}
	public void setValue(double val){
		value = val;
	}

	public void draw(){
		canvas.noFill();
		canvas.stroke(r, g, b);
		canvas.rect(x, y, width, height);

		canvas.fill(r, g, b);
		canvas.rect(x, y, (int) (value * width), height);

		canvas.strokeWeight(3);
		canvas.stroke(255, 150, 10);
		canvas.line((int) (x + (value * width)) - 1, y + 1, (int) (x + (value * width)) - 1, y + height);
		canvas.strokeWeight(1);
	}

	public boolean mousePressed(){
		return checkMouseChange();
	}
	public boolean mouseDragged(){
		return checkMouseChange();
	}

	private boolean checkMouseChange(){
		if(Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y, width, height)){
			value = (double) (canvas.mouseX - x) / width;

			if(events != null) {
				events.change(this);
			}
			return true;
		}

		return false;
	}
}
