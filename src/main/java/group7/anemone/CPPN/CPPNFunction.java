package group7.anemone.CPPN;

import java.io.Serializable;

public class CPPNFunction implements Serializable{
	private static final long serialVersionUID = 4880158090809704826L;

	protected double paramA, paramB, paramC;
	
	private int type;
	
	public CPPNFunction(CPPNFunction cppnFunction) {
		this.paramA = cppnFunction.paramA;
		this.paramB = cppnFunction.paramB;
		this.paramC = cppnFunction.paramC;
		this.type = cppnFunction.type;
	}
	
	public CPPNFunction(double paramA, double paramB, double paramC, int newType) {
		this.paramA = paramA;	
		this.paramB = paramB;
		this.paramC = paramC;
		
		this.type = newType;
	}

	public double calculate(double input) {
		double result=0;
		
		//TODO: Double check all the formulas are correct
		switch(type) {
		case(0): //Parabola
			result = 1/(1+Math.pow(Math.E, -paramA*input)) + paramB;
			break;
		case(1): //Sigmoid
			result = 1/(1+Math.pow(Math.E, -paramA*input)) + paramB;
			break;
		case(2): //Gauss
			result = (1/(paramA*Math.sqrt(2*Math.PI))) *
			Math.pow(Math.E, Math.pow(-(input-paramB), 2) /
				(Math.pow(2*paramA, 2))) + paramC;
			break;
		case(3): //Sin
			result = Math.sin(input*paramA + paramB) + paramC;
			break;
		case(4): // Linear
			result = input;
			break;
		}
		
		return result;
	}
	
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
	
	public String toString() {
		return "Type: " + this.type + " A: " + this.paramA + " B: " + this.paramB + " C: " + this.paramC;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof CPPNFunction)) {
			return false;
		} else {
			CPPNFunction other = (CPPNFunction) o;
			if (other.paramA != this.paramA) return false;
			if (other.paramB != this.paramB) return false;
			if (other.paramC != this.paramC) return false;
			if (other.type != this.type) return false; 
			return true;
		}
	}
}
