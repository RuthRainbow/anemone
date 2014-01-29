package group7.anemone.UI;

import processing.core.PApplet;
import processing.core.PConstants;

public class UIColorWheel extends UIObject{
	private int px;
	private int py;
	private int hy = 0;
	int currentColor;
	private int[][][] colors;

	public UIColorWheel(PApplet c, int x, int y){
		super(c, x, y, 140, 140);

		currentColor = canvas.color(255);

		canvas.colorMode(PConstants.HSB, 360, 100, 100);
		colors = new int[101][width][height];
		for(int h = 0; h <= 100; h++){
			for(int a = 0; a < width; a++){
				for(int b = 0; b < height; b++){
					int i = x + a;
					int j = y + b;
					double mx = x + width / 2;
					double my = y + height / 2;

					double distance = Utilities.distanceBetweenPoints(mx, my, i, j);
					if(distance <= 70){
						double angle = Utilities.angleBetweenPoints(mx, my, i, j);  

						colors[h][a][b] = canvas.color((int) angle, (int) (distance / 70 * 100), 100 - h);
					}else {
						colors[h][a][b] = -1;
					}
				}
			}
		}
		canvas.colorMode(PConstants.RGB, 255);

		moved();
	}

	public int getColor(){
		return currentColor;
	}
	public void setColor(int col){ //finds closest color match in the displayed color wheel
		int dist = Integer.MAX_VALUE;
		int hh = 0, xx = 0, yy = 0;
		
		for(int h = 0; h <= 100; h++){
			for(int a = 0; a < width; a++){
				for(int b = 0; b < height; b++){
					int d = Math.abs(colors[h][a][b] - col);
					if(d < dist){
						dist = d;
						xx = a + x;
						yy = b + y;
						hh = h;
					}
				}
			}
		}
		
		px = xx; py = yy; hy = hh;
	}

	public void moved(){
		px = super.x + width / 2;
		py = super.y + height / 2;
	}
	public void draw(){
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(colors[hy][i][j] == -1) canvas.stroke(bgColor);
				else canvas.stroke(colors[hy][i][j]);

				canvas.point(x + i, y + j);
			}
		}

		canvas.noFill();
		canvas.stroke(0);
		canvas.arc(x + width / 2, y + height / 2, 140, 140, 0, (float) (2*Math.PI));

		canvas.strokeWeight(2);
		canvas.arc(px, py, 10, 10, 0, (float) (2*Math.PI));
		canvas.strokeWeight(1);

		canvas.fill(255);
		canvas.rect(x + 150, y+10, 25, 120);

		for(int i = 0; i < 120; i++){
			canvas.stroke((int) (255 - i / 120.0 * 255.0));
			canvas.line(x + 150, y + 10 + i, x + 175, y + 10 + i);
		}

		canvas.stroke(255, 150, 10);
		canvas.line(x + 150, (int) (y + 10 + hy / 100.0 * 120), x + 175, (int) (y + 10 + hy / 100.0 * 120));
	}

	public boolean mousePressed(){
		return checkMouseChange();
	}
	public boolean mouseDragged(){
		return checkMouseChange();
	}

	private boolean checkMouseChange(){
		boolean updated = false;
		double mx = x + width / 2;
		double my = y + height / 2;

		double distance = Utilities.distanceBetweenPoints(mx, my, canvas.mouseX, canvas.mouseY);
		if(distance <= 70){
			px = canvas.mouseX;
			py = canvas.mouseY;
			updated = true;
		}else if(Utilities.isPointInBox(canvas.mouseX, canvas.mouseY, x + 150, y + 12, 25, 120)){
			hy = (int) ((canvas.mouseY - y - 11) / 120.0 * 100);
			updated = true;
		}

		if(updated){
	      currentColor = colors[hy][px - x][py - y];

	      if(events != null) {
	    	  events.change(this);
	      }
		}

		return updated;
	}
}
