package group7.anemone;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

public class Wall extends SimulationObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8042242408141705366L;
	private Double start;
	private Double end;
	private Line2D.Double line;
	
	Wall(Double start, Double end) {
		super(start);
		this.start = start;
		this.end = end;
		this.line = new Line2D.Double(start,end);
	}
	
	Wall(Line2D.Double wall){
		super((Double) wall.getP1());
		this.start = (Double) wall.getP1();
		this.end = (Double) wall.getP2();
		this.line = wall;
	}

	public Double getStart() {return start;}
	public Double getEnd() {return end;}
	public Line2D.Double getLine() {return line;
	}
	
}
