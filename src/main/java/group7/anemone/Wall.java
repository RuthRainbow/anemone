package group7.anemone;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Wall extends SimulationObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8042242408141705366L;
	private Double start;
	private Double end;
	private int letsThrough = -1;
	private transient World world;
	protected transient Body body;
	
	public Wall(Double start, Double end, World world) {
		super(start);
		this.start = start;
		this.end = end;
		this.world = world;
		
		if(world != null) setupBox2d();
	}
	
	private void setupBox2d(){
		
		float startbox2Dx = (float) (start.x/Simulation.meterToPixel);
		float startbox2Dy = (float) (start.y/Simulation.meterToPixel);
		float endbox2Dx = (float) (end.x/Simulation.meterToPixel);
		float endbox2Dy = (float) (end.y/Simulation.meterToPixel);
				
		Vec2 point1 = new Vec2(startbox2Dx,startbox2Dy);
		Vec2 point2 = new Vec2(endbox2Dx,endbox2Dy);

		EdgeShape wall = new EdgeShape();
		wall.set(point1, point2);
		
	    BodyDef bd = new BodyDef();
	    bd.position = new Vec2();
	    bd.type = BodyType.STATIC;
	    
		 FixtureDef fd = new FixtureDef();
		 fd.shape = wall;
		 fd.density = 1.0f;
		 fd.filter.categoryBits = (letsThrough == -1 ? Collision.TYPE_WALL : letsThrough);
		 fd.userData = this;
		 body = world.createBody(bd);
		 body.createFixture(fd);
	}
	
	Wall(Double start, Double end, World world, int ag) {
		super(start);
		this.start = start;
		this.end = end;
		this.world = world;
		this.letsThrough = ag;
		
		setupBox2d();
	}
	
	public Wall(Line2D.Double wall){
		super((Double) wall.getP1());
		this.start = (Double) wall.getP1();
		this.end = (Double) wall.getP2();
	}

	public Double getStart() {
		return start;
	}
	public Double getEnd() {
		return end;
	}
	public Line2D.Double getLine() {
		return new Line2D.Double(start, end);
	}
	
	public double getLength(float startbox2Dx, float startbox2Dy, float endbox2Dx, float endbox2Dy){
		Point2D.Double start = new Point2D.Double(startbox2Dx,startbox2Dy);
		Point2D.Double end = new Point2D.Double(endbox2Dx,endbox2Dy);
		return start.distance(end);
	}
	
	public double getLength(){
		return start.distance(end);
	}
	
	
	public int getWallType(){
		return letsThrough;
	}
	
	public void addToWorld(){
		setupBox2d();
	}
	
}
