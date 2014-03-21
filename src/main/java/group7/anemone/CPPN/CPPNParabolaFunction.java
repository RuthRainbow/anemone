package group7.anemone.CPPN;

/**
 * A CPPN function that computes the sum of its input.
 */
public class CPPNParabolaFunction extends CPPNFunction {
	public CPPNParabolaFunction(double paramA, double paramB, double paramC) {
		super(paramA, paramB, paramC);
	}
	
	@Override
	public CPPNParabolaFunction copyInstance() {
		CPPNParabolaFunction newFunc =
			new CPPNParabolaFunction(paramA, paramB, paramC);
		
		return newFunc;
	}
	
	@Override
	public double calculate(double input) {
		double result;
		
		result = 1/(1+Math.pow(Math.E, -paramA*input)) + paramB;
		
		return result;
	}
}
