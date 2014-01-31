package group7.anemone;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Seaweed extends SimulationObject{
	private ArrayList<Branch> branches;
	Seaweed(Point2D.Double coords){
		super(coords);
		this.branches = new ArrayList<Branch>();
		branches.add(new Branch(coords,new double[]{-1,-1},coords));
		branches.add(new Branch(coords,new double[]{-1,0},coords));
		branches.add(new Branch(coords,new double[]{-1,1},coords));
		branches.add(new Branch(coords,new double[]{0,-1},coords));
		branches.add(new Branch(coords,new double[]{0,0},coords));
		branches.add(new Branch(coords,new double[]{0,1},coords));
		branches.add(new Branch(coords,new double[]{1,-1},coords));
		branches.add(new Branch(coords,new double[]{1,0},coords));
		branches.add(new Branch(coords,new double[]{1,1},coords));
	}
	public void update(){
		for(Branch branch : branches){
			branch.grow();
		}
	}
}
