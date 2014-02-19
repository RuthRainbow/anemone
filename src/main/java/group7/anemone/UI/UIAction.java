package group7.anemone.UI;

import java.awt.event.MouseWheelEvent;

import processing.core.PApplet;

public abstract class UIAction {
	public void click(UIButton obj){}
	public void change(UIAngle obj){}
	public void change(@SuppressWarnings("rawtypes") UIDropdown obj){}
	public void change(@SuppressWarnings("rawtypes") UIListView obj){}
	public void change(UIColorWheel obj){}
	public void change(UIObject obj){}
	public void change(UISlider obj){}
	public void change(UITextField obj){}
	public void draw(PApplet canvas){}
	public void draw(PApplet canvas, Object obj){}
	
	public boolean mouseWheel(MouseWheelEvent event){return false;}
	public boolean keyPressed(){return false;}
	public boolean keyReleased(){return false;}
	public boolean mousePressed() {return false;}
}
