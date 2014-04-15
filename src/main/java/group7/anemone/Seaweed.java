package group7.anemone;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class Seaweed extends SimulationObject{
	private static final long serialVersionUID = -3660831447912025931L;
	private ArrayList<Branch> branches;
	
	Seaweed(Point2D.Double coords, Environment env){
		super(coords);
		
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
		boolean newSw = false;
		for(Branch branch : branches){
			newSw = newSw || branch.grow();
		}
		if(newSw){
			for(Branch branch : branches){
				branch.currentPoint = coords;
			}
		}
	}
	
	public ArrayList<Branch> getBranches(){
		return branches;
	}
}
