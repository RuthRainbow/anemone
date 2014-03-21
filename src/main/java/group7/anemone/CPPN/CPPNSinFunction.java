package group7.anemone.CPPN;

/**
 * A CPPN node that computes `sin' on its input.
 */
public class CPPNSinFunction extends CPPNFunction {
	public CPPNSinFunction(double paramA, double paramB, double paramC) {
		super(paramA, paramB, paramC);
	}
	
	@Override
	public CPPNSinFunction copyInstance() {
		CPPNSinFunction newFunc =
			new CPPNSinFunction(paramA, paramB, paramC);
		
		return newFunc;
	}
	
	@Override
	public double calculate(double input) {
		double result;
		
		result = Math.sin(input*paramA + paramB) + paramC;
		
		return result;
	}
}
