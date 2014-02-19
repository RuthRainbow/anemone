package group7.anemone.CPPN;

public abstract class CPPNFunction {
	protected double paramA, paramB, paramC;
	
	public CPPNFunction(double paramA, double paramB, double paramC) {
		this.paramA = paramA;	
		this.paramB = paramB;
		this.paramC = paramC;
	}
	
	public abstract CPPNFunction copyInstance();

	public abstract double calculate(double input);
	
	public double getParamA() {
		return paramA;
	}
	
	public void setParamA(double paramA) {
		this.paramA = paramA;
	}
	
	public double getParamB() {
		return paramB;
	}
	
	public void setParamB(double paramB) {
		this.paramB = paramB;
	}
	
	public double getParamC() {
		return paramC;
	}
	
	public void setParamC(double paramC) {
		this.paramC = paramC;
	}
}
