package group7.anemone.UI;

import processing.core.PApplet;

public class UIDrawable3D extends UIObject{
	public UIDrawable3D(PApplet c, int x, int y, int w, int h){
		super(c, x, y, w, h);
	}

	public void draw(){
		canvas.fill(bgColor);
		canvas.rect(x, y, width, height);

		canvas.lights();
		canvas.pushMatrix();
		canvas.translate(x + width/2, y + height/2, 50);

		if(events != null){
			events.draw(canvas);
		}

		canvas.popMatrix();
		canvas.camera();
	}
}
