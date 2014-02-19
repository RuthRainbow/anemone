package group7.anemone.CPPN;

/**
 * A CPPN node that computes the sum of its input.
 */
public class CPPNSumFunction extends CPPNFunction {
	CPPNSumFunction(double paramA, double paramB, double paramC) {
		super(paramA, paramB, paramC);
	}
	
	@Override
	public CPPNSumFunction copyInstance() {
		CPPNSumFunction newFunc =
			new CPPNSumFunction(paramA, paramB, paramC);
		
		return newFunc;
	}
	
	@Override
	public double calculate(double input) {
		double result;
		
		result = input;
		
		return input;
	}
}
