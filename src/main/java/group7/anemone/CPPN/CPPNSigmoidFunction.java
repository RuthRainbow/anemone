package group7.anemone.CPPN;

/**
 * A CPPN function that computes the logistic function on its input.
 */
public class CPPNSigmoidFunction extends CPPNFunction {
	public CPPNSigmoidFunction(double paramA, double paramB, double paramC) {
		super(paramA, paramB, paramC);
	}
	
	@Override
	public CPPNSigmoidFunction copyInstance() {
		CPPNSigmoidFunction newFunc =
			new CPPNSigmoidFunction(paramA, paramB, paramC);
		
		return newFunc;
	}
	
	@Override
	public double calculate(double input) {
		double result;
		
		result = 1/(1+Math.pow(Math.E, -paramA*input))+paramB;

		return result;
	}
}
