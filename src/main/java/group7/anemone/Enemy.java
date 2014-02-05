package group7.anemone;

import group7.anemone.Genetics.Genome;

import java.awt.geom.Point2D.Double;
import java.io.Serializable;


import processing.core.PApplet;

public class Enemy extends Agent implements Serializable {
	final double visionRange = 200;
	final double fov = 65;
	private static final long serialVersionUID = 3142943967920947865L;

	public Enemy(Double coords, double viewHeading, PApplet p,
                Genome genome)
        {
		super(coords, viewHeading, p, genome);	
	}
	
	public double getFOV() {
		return fov;
	}
	public double getVisionRange() {
		return visionRange;
	}
}
