package group7.anemone;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
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
	private World world;
	protected Body body;
	
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
		/*
		double length = getLength(startbox2Dx,startbox2Dy,endbox2Dx,endbox2Dy);
		double angle = Math.atan((endbox2Dy - startbox2Dy) / (endbox2Dx - startbox2Dx));
		double x = startbox2Dx + (endbox2Dx - startbox2Dx) / 2.0f;
		double y = startbox2Dy + (endbox2Dy - startbox2Dy) / 2.0f;
		
		if(length == 0) return;
		
		PolygonShape ps = new PolygonShape();
	    ps.setAsBox((float) (length / 2.0f), 1.0f, new Vec2(0, 0), (float) angle);
	         
	    FixtureDef fd = new FixtureDef();
	    fd.shape = ps;
	    fd.density = 1.0f;
	    fd.filter.categoryBits = (letsThrough == -1 ? Collision.TYPE_WALL : letsThrough);
	 
	    BodyDef bd = new BodyDef();
	    bd.position = new Vec2((float) x, (float) y);
	    bd.type = BodyType.STATIC;
	 
	    body = world.createBody(bd);
	    body.createFixture(fd);*/
		
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
	
	public int getWallType(){
		return letsThrough;
	}
	
	public void addToWorld(){
		setupBox2d();
	}
	
}
