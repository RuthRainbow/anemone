package group7.anemone.UI;

import processing.core.PApplet;

public class UIProgress extends UIObject{
	private double value = 0;

	public UIProgress(PApplet c, int x, int y, int w, int h){
		super(c, x, y, w, h);
	}

	public double getValue(){
		return value;
	}
	public void setValue(double val){
		value = Math.max(0, Math.min(1, val));
	}

	public void draw(){
		canvas.noFill();
		canvas.stroke(r, g, b);
		canvas.rect(x, y, width, height);

		canvas.fill(r, g, b);
		canvas.rect(x, y, (int) (value * width), height);
	}
}
