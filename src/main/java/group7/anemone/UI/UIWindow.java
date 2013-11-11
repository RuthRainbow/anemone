package group7.anemone.UI;

import java.util.ArrayList;
import processing.core.PApplet;

public class UIWindow extends UIObject{
	ArrayList<UIButton> buttons = new ArrayList<UIButton>(); 
	ArrayList<UIObject> objects = new ArrayList<UIObject>(); 
	private UIObject focusedObject;

	public UIWindow(PApplet canvas){
		super(canvas, 0, 0, canvas.width, canvas.height);
	}

	public void draw(){
		for(UIObject object : objects){
			if(object.isVisible) object.draw();
		}
	}
	public boolean mousePressed(){
		for(int i = objects.size() - 1; i >= 0; i--){
			if(objects.get(i).isVisible && objects.get(i).mousePressed()) return true;
		}
		return false;
	}
	public boolean mouseReleased(){
		for(int i = objects.size() - 1; i >= 0; i--){
			if(objects.get(i).isVisible && objects.get(i).mouseReleased()) return true;
		}
		return false;
	}
	public boolean mouseDragged(){
		for(int i = objects.size() - 1; i >= 0; i--){
			if(objects.get(i).isVisible && objects.get(i).mouseDragged()) return true;
		}
		return false;
	}
	public boolean keyReleased(){
		for(int i = objects.size() - 1; i >= 0; i--){
			if(objects.get(i).isVisible && objects.get(i).keyReleased()) return true;
		}
		return false;
	}

	public void setFocusedObject(UIObject obj){
		focusedObject = obj;
	}
	public UIObject getFocusedObject(){
		return focusedObject;
	}

	public void addObject(UIObject obj){
		objects.add(obj);
		obj.setBackground(bgColor);
	}
	public void selectButton(UIButton btn){
		for(UIObject object : objects){
			if(object instanceof UIButton){
				((UIButton) object).setSelected(object == btn);
			}
		}
	}
	public void setBackground(int col){
		super.setBackground(col);

		for(UIObject object : objects){
			object.setBackground(col);
		}
	}
	public void setBackground(int r, int g, int b){
		setBackground(canvas.color(r, g, b));
	}
}
