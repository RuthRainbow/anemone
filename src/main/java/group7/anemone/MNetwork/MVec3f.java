package group7.anemone.MNetwork;

import java.io.Serializable;

public class MVec3f implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -751880465109559304L;
	public float x, y, z;

	public MVec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}