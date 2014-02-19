package group7.anemone.CPPN;

/**
 * A CPPN function that computes the Gaussian of its input.
 */
public class CPPNGaussFunction extends CPPNFunction {
	CPPNGaussFunction(double paramA, double paramB, double paramC) {
		super(paramA, paramB, paramC);
	}
	
	@Override
	public CPPNGaussFunction copyInstance() {
		CPPNGaussFunction newFunc =
			new CPPNGaussFunction(paramA, paramB, paramC);
		
		return newFunc;
	}
	
	@Override
	public double calculate(double input) {
		double result;
		
		result = (1/(paramA*Math.sqrt(2*Math.PI))) *
			Math.pow(Math.E, Math.pow(-(input-paramB), 2) /
				(Math.pow(2*paramA, 2))) + paramC;
		
		return result;
	}
}
