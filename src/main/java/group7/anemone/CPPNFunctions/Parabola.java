package group7.anemone.CPPNFunctions;

public class Parabola {
	
	//Parabola function: http://en.wikipedia.org/wiki/Parabola
	//Will be contained in a CPPNNode, like NeuronParameters currently are with NeatNodes
	
	//These parameters will augment the parabola
	private double a;
	private double b;
	private double c;
	
	public Parabola(double ina, double inb, double inc) {
		a = ina;
		b = inb;
		c = inc;
	}
	
	double calc(double x, double y) {
		double result = 0;
		//Do the parabola equation
		
		result = (a*Math.pow(x, 2))+(b*x)+c;
		
		return result;
	}

}
