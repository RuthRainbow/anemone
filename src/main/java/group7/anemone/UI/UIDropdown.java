package group7.anemone.UI;

import java.awt.event.MouseWheelEvent;

import processing.core.PApplet;
import processing.core.PFont;

public class UIDropdown extends UIObject{
	private PFont f;
	private String[] options = new String[0];
	private int selectedIndex = 0;
	private int hoveredIndex = -1;
	private boolean showDropdown = false;
	private int showOffset = 0;
	private int showCapacity = 6;

	public UIDropdown(PApplet c, int x, int y, int w, String vals[]){
		super(c, x, y, w, 20);

		f = canvas.createFont("Courier", 12, true);
		options = vals;
	}

	public String getSelected(){
		return options[selectedIndex];
	}
	public int getSelectedIndex(){
		return selectedIndex;
	}
	public void setCapacity(int cap){
		showCapacity = cap;
	}

	public void draw(){
		canvas.fill(bgColor);
		canvas.stroke(r, g, b);
		canvas.rect(x, y, width-height, height);
		canvas.fill(r, g, b);
		canvas.rect(x+width - 20, y, height, height);

		canvas.textFont(f);
		canvas.text(options[selectedIndex], x + 5, y + 13);

		if(showDropdown){
			canvas.fill(bgColor);
			canvas.rect(x, y + 20, width, 20 * Math.min(options.length, showCapacity));

			for(int i = 0; i < Math.min(options.length, showCapacity); i++){
				String op = options[i + showOffset];
				int tx = x + 5;
				int ty = y + i * 20 + 33;

				if(i == selectedIndex){
					canvas.fill(r, g, b);
					canvas.rect(x, ty - 13, width, height);
					canvas.fill(255 - r, 255 - g, 255 - b);
				}else{
					canvas.fill(r, g, b);
				}
				canvas.text(op, tx, ty);
			}
		}
	}

	public boolean mousePressed(){
		if(showDropdown){
			for(int i = 0; i < Math.min(options.length, showCapacity); i++){
				if(Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y + i * 20 + 20, width, 20)){
					if(selectedIndex != (i + showOffset)){
						selectedIndex = i + showOffset;
						if(events != null) events.change(this);
					}
					showDropdown = false;
					return true;
				}
			}
		}

		if(Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y, width, height)){
			showDropdown = !showDropdown;
			return true;
		}

		return false;
	}
	public boolean mouseWheel(MouseWheelEvent event){
		if(showDropdown && Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y + 20, width, Math.min(options.length, showCapacity) * 20)){
			showOffset += (event.getWheelRotation() < 0 ? -1 : 1);
			showOffset = Math.max(0, Math.min(showOffset, options.length - showCapacity));
			return true;
		}
		
		return false;
	}
}
