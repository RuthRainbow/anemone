package group7.anemone.HyperNeatGenetics;

import group7.anemone.CPPN.CPPNFunction;
import group7.anemone.Genetics.GenomeNode;

import java.util.Random;

public class HyperNeatNode extends GenomeNode {
	CPPNFunction cppnFunction;
	
	public enum Type {
		INPUT, HIDDEN, OUTPUT
	};
	
	private Type type;

	public HyperNeatNode(int id, CPPNFunction cppnFunction,
		Type type)
	{
		super(id);
		this.cppnFunction = new CPPNFunction(cppnFunction);
		this.type = type;
	}
	
	public Type getType() {
		return type;
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
}