package group7.anemone.UI;

import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

public class UIListView<E> extends UIObject{
	private ArrayList<UIListItem<E>> options;
	
	PFont fontTitle;
	PFont fontDesc;
	private int selectedIndex = 0;
	private int showOffset = 0;
	private int showCapacity;
	private boolean drawIcon = true;

	public UIListView(PApplet c, int x, int y, int w, int h){
		super(c, x, y, w, h);
		
		showCapacity = (int) Math.floor(h / 50);
		options = new ArrayList<UIListItem<E>>();
		fontTitle = canvas.createFont("Arial", 12, true);
		fontDesc = canvas.createFont("Arial", 10, true);
	}

	public E getSelected(){
		return options.get(selectedIndex).value;
	}
	public int getSelectedIndex(){
		return selectedIndex;
	}
	public void addItem(String title, String description, E value){
		UIListItem<E> item = new UIListItem<E>(title, description, value);
		options.add(item);
	}
	public void clear(){
		options.clear();
		selectedIndex = 0;
	}
	public void setDrawIcon(boolean draw){
		drawIcon = draw;
	}

	public void draw(){
		canvas.fill(bgColor);
		canvas.rect(x, y, width, height);

		for(int i = 0; i < Math.min(options.size(), showCapacity); i++){
			int tx = x + 5;
			int ty = y + i * 50;
			int position = i + showOffset;
			String title = options.get(position).title;
			String desc = options.get(position).description;
			
			if(position == selectedIndex){
				canvas.fill(r, g, b);
				canvas.rect(x, ty, width, 50);
				canvas.fill(255 - r, 255 - g, 255 - b);
			}else{
				canvas.fill(r, g, b);
			}
			
			if(drawIcon && events != null){
				canvas.pushStyle();
				canvas.pushMatrix();
				canvas.translate(tx - 5, ty + 15);
				
				events.draw(canvas, options.get(position).value);
				
				canvas.popStyle();
				canvas.popMatrix();
				
				tx += 40;
			}
			
			canvas.textFont(fontTitle);
			canvas.text(title, tx, ty + 15);
			canvas.textFont(fontDesc);
			canvas.text(desc, tx, ty + 30);
			canvas.textFont(fontTitle);
		}
	}

	public boolean mousePressed(){
		if(!Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y, width, height)) return false;
		
		for(int i = 0; i < Math.min(options.size(), showCapacity); i++){
			if(Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y + i * 50, width, 50)){
				if(selectedIndex != (i + showOffset)){
					selectedIndex = i + showOffset;
					if(events != null) events.change(this);
				}
				
				return true;
			}
		}
			
		return false;
	}
	public boolean mouseWheel(MouseWheelEvent event){
		if(Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y, width, height)){
			showOffset += (event.getWheelRotation() < 0 ? -1 : 1);
			showOffset = Math.max(0, Math.min(showOffset, options.size() - showCapacity - 1));
			return true;
		}
		
		return false;
	}
}

class UIListItem<E>{
	public final String title;
	public final String description;
	public final E value;
	
	public UIListItem(String title, String description, E value){
		this.title = title;
		this.description = description;
		this.value = value;
	}
}