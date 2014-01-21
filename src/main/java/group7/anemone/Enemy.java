package group7.anemone;

import java.awt.geom.Point2D.Double;
import java.io.Serializable;

import processing.core.PApplet;

public class Enemy extends Agent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3142943967920947865L;

	public Enemy(Double coords, double viewHeading, PApplet p,
                Genome genome)
        {
		super(coords, viewHeading, p, genome);	
	}
}
