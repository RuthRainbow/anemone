package group7.anemone.UI;

import processing.core.PApplet;

public class Utilities {
	public static double distanceBetweenPoints(double ax, double ay, double bx, double by){
		return Math.sqrt(Math.pow((float) (bx - ax), 2) + Math.pow((float) (by - ay), 2));
	}
	public static double angleBetweenPoints(double ax, double ay, double bx, double by){
		double angle = 0;
		if(bx - ax == 0){
			if(by > ay) angle = -90;
			else angle = 90;
		}else angle = Math.atan((by - ay) / (bx - ax)) * 180.0 / Math.PI;

		if(bx > ax) {
			if (by < ay) angle = 360 + angle;
		}else{
			if (by >= ay) angle = 180 + angle;
			else angle += 180;
		}

		return angle;
	}
	public static void circle(PApplet canvas, int x, int y, int r){
		canvas.arc(x, y, r, r, 0, (float) (2*Math.PI));
	}
	public static boolean isPointInBox(int x, int y, int rx, int ry, int w, int h){
		return (x >= rx && x <= rx + w && y >= ry && y <= ry + h);
	}
}