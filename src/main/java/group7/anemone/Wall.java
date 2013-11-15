package group7.anemone;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D.Double;

public class Wall extends SimulationObject{

	private Double start;
	private Double end;
	private Line2D.Double line;
	
	Wall(Double start, Double end) {
		super(start);
		this.start = start;
		this.end = end;
		this.line = new Line2D.Double(start,end);
	}

	public Double getStart() {return start;}
	public Double getEnd() {return end;}
	public Line2D.Double getLine() {return line;
	}
	
}
