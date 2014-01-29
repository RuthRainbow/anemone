package group7.anemone.UI;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class UITextField extends UIObject{
	private String value = "";
	private PFont f;
	private float fw = 0;
	private int cursorPosition = 3;
	private int cursorStart = 0;
	private int offset = 0;
	private boolean cursorShow = true;
	private int tick = 0;
	private int doubleTick = 0;
	private boolean focused = false;

	public UITextField(PApplet c, int x, int y, int w, String val){
		super(c, x, y, w, 0);

		this.value = val;
		this.r = 0;
		this.g = 0;
		this.b = 0;

		f = c.createFont("Courier", 12, true);
		c.textFont(f);
		fw = c.textWidth(value) / value.length();
	}

	public void setText(String txt){
		this.value = txt;
	}
	public String getText(){
		return this.value;
	}

	public void draw(){
		canvas.stroke(0);
		canvas.fill(255);
		canvas.rect(x, y, width, 20);

		canvas.fill(r, g, b);
		canvas.textFont(f);
		String txt = value;
		if(value.substring(offset).length() * fw > width){
			txt = value.substring(offset, (int) Math.floor((width - 5 - 3 * fw) / fw)) + "...";
		}
		canvas.text(txt, x + 5, y + 13);

		if(focused){
			canvas.fill(50, 150, 255, 50);
			canvas.noStroke();
			canvas.rect(x+5 + fw * cursorStart, y, fw * (cursorPosition - cursorStart), 20);

			if(cursorShow){
				canvas.stroke(0);
				int cx = (int) (x + 5 + fw * cursorPosition);
				canvas.line(cx, y + 3, cx, y + 20 - 3);
			}
		}

		if(tick > 20) {
			cursorShow = !cursorShow;
			tick = 0;
		}
		tick++;

		if(doubleTick > 0) doubleTick--;
	}

	public boolean mousePressed(){
		if(Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x, y, width, 20)){
			focused = true;
			if(canvas.mouseX >= this.x + 5) cursorStart = (int) Math.floor((canvas.mouseX - this.x) / fw);
			else cursorStart = 0;
			cursorStart = Math.min(cursorStart, value.length());
			cursorPosition = cursorStart;
			return true;
		}

		focused = false;
		return false;
	}
	public boolean mouseDragged(){
		if(canvas.mouseX >= this.x && canvas.mouseX <= this.x + this.width &&
				canvas.mouseY >= this.y && canvas.mouseY <= this.y + 20){
			if(canvas.mouseX >= this.x + width - 5) offset ++;
			if(canvas.mouseX >= this.x + 5) cursorPosition = (int) Math.floor((canvas.mouseX - this.x) / fw);
			else cursorPosition = 0;
			cursorPosition = Math.min(cursorPosition, value.length());
			return true;
		}
		return false;
	}
	public boolean mouseReleased(){
		if(canvas.mouseX >= this.x && canvas.mouseX <= this.x + this.width &&
				canvas.mouseY >= this.y && canvas.mouseY <= this.y + 20){
			focused = true;
			if(canvas.mouseX >= this.x + 5) cursorPosition = (int) Math.floor((canvas.mouseX - this.x) / fw);
			else cursorPosition = 0;
			cursorPosition = Math.min(cursorPosition, value.length());

			if(doubleTick > 0){
				String head = value.substring(0, cursorPosition);
				cursorStart = head.lastIndexOf(" ");
				if(cursorStart == -1) cursorStart = 0;
				else cursorStart++;

				String tail = value.substring(cursorPosition);
				int index = tail.indexOf(" ");
				if(index == -1) cursorPosition = value.length();
				else cursorPosition += index;
			}else doubleTick = 10;

			return true;
		}

		focused = false;
		events.change(this);
		return false;
	}
	public boolean keyReleased(){
		if(focused){
			if(canvas.key == PConstants.CODED){
				if(canvas.keyCode == PConstants.RIGHT){
					cursorPosition = Math.min(cursorPosition+1, value.length());
				}else if(canvas.keyCode == PConstants.LEFT){
					cursorPosition = Math.max(cursorPosition-1, 0);
				}
			}else if(canvas.keyCode == PConstants.BACKSPACE){
				if(cursorPosition > cursorStart){
					value = value.substring(0, cursorStart) + value.substring(cursorPosition);
					cursorPosition = cursorStart;
				}else if(cursorPosition < cursorStart){
					value = value.substring(0, cursorPosition) + value.substring(cursorStart);
					cursorStart = cursorPosition;
				}else if(cursorPosition > 0){
					value = value.substring(0, cursorPosition - 1) + value.substring(cursorPosition);
					cursorPosition--;
					cursorStart--;
				}
			}else if(canvas.keyCode == PConstants.DELETE){
				if(cursorPosition > cursorStart){
					value = value.substring(0, cursorStart) + value.substring(cursorPosition);
					cursorPosition = cursorStart;
				}else if(cursorPosition < cursorStart){
					value = value.substring(0, cursorPosition) + value.substring(cursorStart);
					cursorStart = cursorPosition;
				}else if(cursorPosition < value.length()){
					value = value.substring(0, cursorPosition) + value.substring(cursorPosition + 1);
				}
			}else{
				value = value.substring(0, cursorPosition) + canvas.key + value.substring(cursorPosition);
				cursorPosition++;
				cursorStart++;
			}
			return true;
		}

		return false;
	}
}