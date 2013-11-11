package group7.anemone.UI;

import processing.core.PApplet;

public class UIObject {
	private int lx = 0;
	private int ly = 0;
	private int ox = 0;
	private int oy = 0;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected PApplet canvas;
	protected boolean isVisible = true;

	protected short r = 255;
	protected short g = 255;
	protected short b = 255;

	protected int bgColor = 0;
	protected boolean fixedBackground = false;

	protected UIAction events;
	private boolean isLeft = true;
	private boolean isTop = true;
	
	private UIObject parent;

	public UIObject(PApplet c, int x, int y, int w, int h){
		this.canvas = c;
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;

		lx = x;
		ly = y;

		bgColor = canvas.color(0);
	}

	public void setColor(int r, int g, int b){
		this.r = (short) r;
		this.g = (short) g;
		this.b = (short) b;
	}
	public void setBackground(int r, int g, int b){
		this.bgColor = canvas.color(r, g, b);
	}
	public void setBackground(int col){
		this.bgColor = col;
	}
	public void setEventHandler(UIAction f){
		events = f;
	}
	public void setIsLeft(boolean left){
		isLeft = left;
		if(left) x = lx;
		else x = (parent != null ? parent.width : canvas.width) - lx;
		x += ox;
		moved();
	}
	public void setIsTop(boolean top){
		isTop = top;
		if(top) y = ly;
		else y = (parent != null ? parent.height : canvas.height) - ly;
		y += oy;
		moved();
	}
	public void setFixedBackground(boolean fix){
		fixedBackground = fix;
	}
	public void setOffset(int x, int y){
		ox = x;
		oy = y;

		setIsLeft(isLeft);
		setIsTop(isTop);
	}
	public void setParent(UIObject p){
		parent = p;
	}
	public void setVisible(boolean vis){
		isVisible = vis;
	}
	public void toggleVisible(){
		isVisible = !isVisible;
	}

	public void moved(){}
	public void draw(){}
	public boolean mousePressed(){return false;}
	public boolean mouseReleased(){return false;}
	public boolean mouseDragged(){return false;}
	public boolean keyReleased(){return false;}
}
