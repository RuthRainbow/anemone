package group7.anemone.UI;

import java.awt.event.MouseWheelEvent;

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
		canvas.translate(x + width/2 - 25, y + height/2, 50);

		if(events != null){
			events.draw(canvas);
		}

		canvas.popMatrix();
		canvas.camera();
	}
	
	public boolean mouseWheel(MouseWheelEvent event){
		if(events != null) return events.mouseWheel(event);
		return false;
	}
	public boolean keyPressed(){
		if(events != null) return events.keyPressed();
		return false;
	}
	public boolean keyReleased(){
		if(events != null) return events.keyReleased();
		return false;
	}
}
