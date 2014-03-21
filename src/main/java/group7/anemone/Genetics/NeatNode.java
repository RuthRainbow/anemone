package group7.anemone.Genetics;

import group7.anemone.CPPN.CPPNFunction;
import group7.anemone.CPPN.CPPNGaussFunction;
import group7.anemone.CPPN.CPPNParabolaFunction;
import group7.anemone.CPPN.CPPNSigmoidFunction;
import group7.anemone.CPPN.CPPNSinFunction;
import group7.anemone.CPPN.CPPNSumFunction;
import java.util.Random;

public class NeatNode {
	CPPNFunction cppnFunction;
	int id;

	public NeatNode(int id, CPPNFunction cppnFunction) {
		this.cppnFunction = cppnFunction.copyInstance();
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
		switch(randInt) {
			case 0:
				func = new CPPNGaussFunction(pA, pB, pC);
				break;
			case 1:
				func = new CPPNParabolaFunction(pA, pB, pC);
				break;
			case 2:
				func = new CPPNSigmoidFunction(pA, pB, pC);
				break;
			case 3:
				func = new CPPNSinFunction(pA, pB, pC);
				break;
			default:
				func = new CPPNSumFunction(pA, pB, pC);
				break;
		}
		
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