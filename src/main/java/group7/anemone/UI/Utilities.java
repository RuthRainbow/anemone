package group7.anemone.UI;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

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
	public static void line(PApplet canvas, int x1, int y1, int len, double angle){
		canvas.pushMatrix();
		canvas.translate(x1, y1);
		canvas.rotate((float) ((angle - 90) * Math.PI / 180));
		canvas.line(0, 0, 0, len);
		canvas.popMatrix();
	}
	public static void pointAtAngle(PApplet canvas, double x, double y, double dist, double angle, int size){
		canvas.pushMatrix();
		canvas.translate((int) x, (int) y);
		canvas.rotate((float) ((angle - 90) * Math.PI / 180));
		canvas.arc(0, (int) dist, size, size, 0, (float) (2*Math.PI));
		canvas.popMatrix();
	}
	public static boolean isPointInBox(int x, int y, int rx, int ry, int w, int h){
		return (x >= rx && x <= rx + w && y >= ry && y <= ry + h);
	}
	
	//assumes that the lines do intersect 
	public static Point2D.Double findIntersection(Line2D.Double line1, Line2D.Double line2){
		Point2D.Double intersection = new Point2D.Double();
		//get gradients and y intercepts
		double m1 = (line1.y2-line1.y1)/(line1.x2-line1.x1);
		double c1 = line1.y1 - m1 * line1.x1;
		double m2 = (line2.y2-line2.y1)/(line2.x2-line2.x1);
		double c2 = line2.y1 - m2 * line2.x1;
		
		boolean m2Invalid = (m2 == java.lang.Double.POSITIVE_INFINITY || m2 == java.lang.Double.NEGATIVE_INFINITY || java.lang.Double.isNaN(m2));
		boolean c2Invalid = (c2 == java.lang.Double.POSITIVE_INFINITY || c2 == java.lang.Double.NEGATIVE_INFINITY || java.lang.Double.isNaN(c2));
		
		
		//if the lines aren't parallel
		if(m1 != m2){
			//get x value of intersection
			double intersectX = -(c1-c2)/(m1-m2);
			if(m2Invalid && c2Invalid) intersectX = line2.x1;
			
			//check it lies within both segments
			boolean withinLine1 = Math.min(line1.x1,line1.x2) < intersectX && intersectX < Math.max(line1.x1, line1.x2);
			boolean withinLine2 = Math.min(line2.x1,line2.x2) < intersectX && intersectX < Math.max(line2.x1, line2.x2);
			if((m2Invalid && c2Invalid)) withinLine2 = true;
			if(withinLine1  && withinLine2) {
				//calculate y value of intersection
				double intersectY = m1*intersectX + c1;
				intersection = new Point2D.Double(intersectX, intersectY);
			}
		}
		
		return intersection;
	}
	
	public static Line2D.Double generateLine(Point2D.Double point, double length, double angle){
		double endX = length * Math.cos(angle*(Math.PI/180)) + point.x;
		double endY = length * Math.sin(angle*(Math.PI/180)) + point.y;
		
		return new Line2D.Double(point,new Point2D.Double(endX,endY));
	}
	
	public static Point2D.Double getClosestPoint(Line2D.Double line, Point2D.Double point) {
		/*double opp = line.ptLineDist(point);
		double hyp = point.distance(line.getP1());
		double theta = Math.asin(opp/hyp);
		double agt = opp / Math.tan(theta);
		
		double alpha = Math.atan((line.y2-line.y1)/(line.x2-line.x1))*(Math.PI / 180);
		Line2D.Double tmp = generateLine((Double) line.getP1(),agt,alpha);
		
		System.out.println("opp: "+opp+" hyp: "+hyp+" theta: "+theta+" agt: "+agt+" alpha: "+alpha);
		return (Double) tmp.getP2();*/
		double A1 = line.y2 - line.y1; 
	    double B1 = line.x1 - line.x2; 
	    double C1 = (line.y2 - line.y1)*line.x1 + (line.x1 - line.x2)*line.y1; 
	    double C2 = -B1*point.x + A1*point.y; 
	    double det = A1*A1 - -B1*B1; 
	    double cx = 0; 
	    double cy = 0; 
	    if(det != 0){ 
	    	cx = (A1*C1 - B1*C2)/det; 
	     	cy = (A1*C2 - -B1*C1)/det; 
	    }else{ 
	    	cx = point.x; 
	    	cy = point.y; 
	    } 
	    return new Point2D.Double(cx, cy); 
	}
	public static double[] quadratic(double a, double b, double c) {
		double[] result = new double[2];
		result[0] = (-b + Math.sqrt(b*b - 4*a*c))/2*a;
		result[1] = (-b - Math.sqrt(b*b - 4*a*c))/2*a;
		return result;
	}

}
