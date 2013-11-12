package group7.anemone.UI;

import processing.core.PApplet;

public abstract class UIAction {
	public void click(UIButton obj){}
	public void change(UIAngle obj){}
	public void change(UIDropdown obj){}
	public void change(UIColorWheel obj){}
	public void change(UIObject obj){}
	public void change(UISlider obj){}
	public void draw(PApplet canvas){}
}
