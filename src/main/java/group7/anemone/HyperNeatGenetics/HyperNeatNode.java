package group7.anemone.HyperNeatGenetics;

import group7.anemone.CPPN.CPPNFunction;
import group7.anemone.Genetics.GenomeNode;

import java.util.Random;

public class HyperNeatNode extends GenomeNode {
	CPPNFunction cppnFunction;

	public HyperNeatNode(int id, CPPNFunction cppnFunction) {
		super(id);
		this.cppnFunction = new CPPNFunction(cppnFunction);
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
		neatNode = new HyperNeatNode(id, func);
		
		return neatNode;
	}
	
	public CPPNFunction getCPPNFunction() {
		return cppnFunction;
	}
}