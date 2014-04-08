package group7.anemone.Genetics;

import group7.anemone.CPPN.CPPNFunction;
import java.util.Random;

public class NeatNode {
	CPPNFunction cppnFunction;
	int id;

	public NeatNode(int id, CPPNFunction cppnFunction) {
		this.cppnFunction = new CPPNFunction(cppnFunction);
		this.id = id;
	}
	
	public static NeatNode createRandomNeatNode(int id) {
		Random rand;
		int randInt;
		double pA, pB, pC;
		CPPNFunction func;
		NeatNode neatNode;
		
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
		neatNode = new NeatNode(id, func);
		
		return neatNode;
	}
	
	public CPPNFunction getCPPNFunction() {
		return cppnFunction;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return ""+this.id;
	}
}