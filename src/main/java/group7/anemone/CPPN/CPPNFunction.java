package group7.anemone.CPPN;

public class CPPNFunction {
	//This function will replace the parameter that NeatNodes have
	
	//Types of functions that can be implemented:
	// 0: Parabola
	// 1: Sine wave
	// 2: Gaussian Distibution
	// 3: Sigmoid Function
	
	public int type;
	
	private double ParaA; //Usually modifies the variable, so Para*X
	private double ParaB; //Usually scales the function so ParaB*(Function)
	private double ParaC; //Usually translates the whole function so (Function)+ParaC
	
	public CPPNFunction(int newType, double ina, double inb, double inc) {
		type = newType;
		
		//Parabola, Sine wave, and Gaussian Distribution curves can all be modeled by using just 3 variables.
		
		ParaA = ina;	
		ParaB = inb;
		ParaC = inc;	//Acts as a translation variable
		
		
	}

	public double calculate(double x) {
		double result=0;
		switch(type) {
			case(0):
				result = (ParaA*Math.pow(x, 2))+(ParaB*x)+ParaC;
				break;
			case(1):
				result = ParaA*(Math.sin(x*ParaB)) + ParaC;
				break;
			case(2):
				result = (1/(ParaA*Math.sqrt(2*Math.PI))) * Math.pow(Math.E, Math.pow(-(x-ParaB), 2)/(Math.pow(2*ParaA, 2))) + ParaC;
				break;
			case(3):
				result = ParaB*(1/(1+Math.pow(Math.E, -ParaA*x)))+ParaC;
			default:
				result=0;
		}
		
		return result;
	}
}
