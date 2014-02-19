package group7.anemone.UI;

import processing.core.PApplet;

public class UIDrawable extends UIObject{
	public UIDrawable(PApplet c, int x, int y, int w, int h){
		super(c, x, y, w, h);
	}

	public void draw(){
		canvas.fill(bgColor);
		canvas.rect(x, y, width, height);
		
		canvas.pushMatrix();
		canvas.translate(x, y);

		if(events != null){
			events.draw(canvas);
		}
		
		canvas.popMatrix();
	}
}
