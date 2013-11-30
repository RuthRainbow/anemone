package group7.anemone.UI;

import processing.core.PApplet;
import processing.core.PFont;

public class UIButton extends UIObject{
	private String value = "";
	private boolean pressed = false;
	private boolean selected = true;
	private PFont f;

	private short fr = 0;
	private short fg = 0;
	private short fb = 0;

	public UIButton(PApplet c, int x, int y, int w, int h, String val){
		super(c, x, y, w, h);

		this.value = val;
		f = canvas.createFont("Courier",12,true);
	}

	public void setFontColor(int r, int g, int b){
		this.fr = (short) r;
		this.fg = (short) g;
		this.fb = (short) b;
	}
	public void setSelected(boolean select){
		this.selected = select;
	}
	public void setText(String txt){
		this.value = txt;
	}

	public void draw(){
		int r = this.r;
		int g = this.g;
		int b = this.b;
		if(pressed) {
			r -= 20;
			g -= 20;
			b -= 20;
		}
		if(selected) canvas.fill(r, g, b);
		else canvas.fill(r, g, b, 130);
		canvas.stroke(0);

		canvas.rect(this.x, this.y, this.width, this.height);

		canvas.fill(fr, fg, fb);
		canvas.textFont(f);
		canvas.text(this.value, this.x + this.width / 2 - this.value.length() * (canvas.textWidth(value) / value.length()) / 2, this.y + this.height / 2 + 12/3);
	}
	public boolean mouseReleased(){
		if(pressed && Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y, width, height)){
			events.click(this);

			return true;
		}

		pressed = false;
		return false;
	}
	public boolean mousePressed(){
		if(Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y, width, height)){
			pressed = true;
			return true;
		}
		return false;
	}
}
