package group7.anemone.UI;

import processing.core.PApplet;
import processing.core.PFont;

public class UILabel extends UIObject{
	String value = "";
	private PFont f = canvas.createFont("Arial", 12, true);

	public UILabel(PApplet c, int x, int y, String val){
		super(c, x, y + 12, 0, 0);

		this.value = val;
	}

	public void setText(String txt){
		this.value = txt;
	}

	public void draw(){
		canvas.fill(r, g, b);
		canvas.textFont(f);
		canvas.text(this.value, this.x, this.y);
	}
}
