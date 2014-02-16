package group7.anemone.CPPNFunctions;

public class CPPNFunction {
	//This function can act as any possible sigmoid function.
	
	public int type;
	
	private double ParaA;
	private double ParaB;
	private double ParaC;
	
	public CPPNFunction(int newType, double ina, double inb, double inc) {
		type = newType;
		
		switch(type) {
		case(0):
			ParaA = ina;
			ParaB = inb;
			ParaC = inc;
		}
		
	}

	public double calculate(double x, double y) {
		double result=0;
		switch(type) {
			case(0):
				result = (ParaA*Math.pow(x, 2))+(ParaB*x)+ParaC; 
		}
		
		return result;
	}
}
