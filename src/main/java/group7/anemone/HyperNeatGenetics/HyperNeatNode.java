package group7.anemone.HyperNeatGenetics;

import group7.anemone.CPPN.CPPNFunction;
import group7.anemone.Genetics.GenomeNode;

import java.util.Random;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class HyperNeatNode extends GenomeNode implements Comparable {
	CPPNFunction cppnFunction;
	
	public enum Type {
		INPUT, HIDDEN, OUTPUT
	};
	
	private Type type;

	public HyperNeatNode(int id, CPPNFunction cppnFunction,
		Type type) {
		super(id);
		this.cppnFunction = new CPPNFunction(cppnFunction);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public static HyperNeatNode createNonRandomNode(int id) {
		/* Create a CPPN function. */
		CPPNFunction func = new CPPNFunction(1.0, 1.0, 1.0, 1);
		
		/* Create NeatNode. */
		return new HyperNeatNode(id, func, Type.HIDDEN);
	}
	
	public static HyperNeatNode createRandomNeatNode(int id) {
		Random rand;
		int randInt;
		double pA, pB, pC;
		CPPNFunction func;
		HyperNeatNode neatNode;
		
		rand = new Random();
		
		/* Random number for choosing a function. */
		randInt = rand.nextInt(5);
		
		/* Random function parameters. */
		pA = rand.nextDouble();
		pB = rand.nextDouble();
		pC = rand.nextDouble();
		
		/* Create a random CPPN function. */
		func = new CPPNFunction(pA, pB, pC, randInt);
		
		/* Create NeatNode. */
		neatNode = new HyperNeatNode(id, func, Type.HIDDEN);
		
		return neatNode;
	}
	
	public CPPNFunction getCPPNFunction() {
		return cppnFunction;
	}
	
	public HyperNeatNode clone() {
		return new HyperNeatNode(this.id, this.cppnFunction, this.type);
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof HyperNeatNode)) {
			return false;
		} else {
			HyperNeatNode other = (HyperNeatNode) o;
			if (!(other.cppnFunction.equals(this.cppnFunction))) return false;
			if (other.id != this.id) return false;
			if (other.type != this.type) return false;
			return true;
		}
	}
	
	public String toString() {
		return "Type: " + this.type + " id: " + this.id;
	}
	
	// This is needed so HashSet can tell two nodes are equal by ID.
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
        append(type).
        append(id).
        toHashCode();
	}

	@Override
	public int compareTo(Object o) {
		HyperNeatNode other = (HyperNeatNode) o;
		if (other.id < this.id) return 1;
		else if (other.id > this.id) return -1;
		else return 0;
	}
}