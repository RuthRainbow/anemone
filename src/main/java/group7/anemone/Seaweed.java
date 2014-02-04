package group7.anemone;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Seaweed extends SimulationObject{
	private ArrayList<Branch> branches;
	private Environment env;
	
	Seaweed(Point2D.Double coords, Environment env){
		super(coords);
		
		this.env = env;
		
		this.branches = new ArrayList<Branch>();
		branches.add(new Branch(coords,new double[]{-1*Math.random(),-1*Math.random()},coords, env));
		branches.add(new Branch(coords,new double[]{-1*Math.random(),Math.random()},coords, env));
		branches.add(new Branch(coords,new double[]{-1*Math.random(),1*Math.random()},coords, env));
		branches.add(new Branch(coords,new double[]{Math.random(),-1*Math.random()},coords, env));
		branches.add(new Branch(coords,new double[]{Math.random(),Math.random()},coords, env));
		branches.add(new Branch(coords,new double[]{Math.random(),1*Math.random()},coords, env));
		branches.add(new Branch(coords,new double[]{1*Math.random(),-1*Math.random()},coords, env));
		branches.add(new Branch(coords,new double[]{1*Math.random(),Math.random()},coords, env));
		branches.add(new Branch(coords,new double[]{1*Math.random(),1*Math.random()},coords, env));
	}
	public void update(){
		for(Branch branch : branches){
			branch.grow();
		}
	}
}
