package group7.anemone.MNetwork;

import java.io.Serializable;

public class MVec3f implements Serializable {
	private static final long serialVersionUID = -751880465109559304L;
	public float x, y, z;

	/**
	 * Copy constructor.
	 * 
	 * @param mVec3f	3-dimensional vector to be copied
	 */
	public MVec3f(MVec3f mVec3f) {
		this.x = mVec3f.x;
		this.y = mVec3f.y;
		this.z = mVec3f.z;
	}

	/**
	 * Constructs a 3-dimensional vector.
	 *
	 * @param x	x-coordinate
	 * @param y	y-coordinate
	 * @param z	z-coordinate
	 */
	public MVec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MVec3f)) return false;
		else {
			MVec3f other = (MVec3f) o;
			if (this.x != other.x) return false;
			if (this.y != other.y) return false;
			if (this.z != other.z) return false;
			return true;
		}
	}

	public static MVec3f zero() {
		return new MVec3f(0, 0, 0);
	}

	public MVec3f add(MVec3f vec) {
		return new MVec3f(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}

	public MVec3f subtract(MVec3f vec) {
		return new MVec3f(this.x - vec.x, this.y - vec.y, this.z - vec.z);
	}

	public MVec3f multiply(MVec3f vec) {
		return new MVec3f(this.x * vec.x, this.y * vec.y, this.z * vec.z);
	}

	public MVec3f divide(MVec3f vec) {
		return new MVec3f(this.x / vec.x, this.y / vec.y, this.z / vec.z);
	}

	public MVec3f onZero(float v) {
		return new MVec3f((this.x == 0 ? v : this.x), (this.y == 0 ? v : this.y), (this.z == 0 ? v : this.z));
	}

	public MVec3f abs() {
		return new MVec3f(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
	}

	public MVec3f pow(int n) {
		return new MVec3f((float) Math.pow(this.x, n), (float) Math.pow(this.y, n), (float) Math.pow(this.z, n));
	}

	public MVec3f min(int n) {
		return new MVec3f((float) Math.min(this.x, n), (float) Math.min(this.y, n), (float) Math.min(this.z, n));
	}

	public MVec3f max(int n) {
		return new MVec3f((float) Math.max(this.x, n), (float) Math.max(this.y, n), (float) Math.max(this.z, n));
	}

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}
