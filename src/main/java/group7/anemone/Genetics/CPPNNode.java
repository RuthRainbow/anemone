package group7.anemone.Genetics;

import group7.anemone.CPPN.CPPNFunction;

public class CPPNNode {
	CPPNFunction funcs;
	int id;

	public CPPNNode(int id, int type, double ina, double inb, double inc) {
		this.funcs = new CPPNFunction(type, ina, inb, inc);
		this.id = id;
	}
	
	public double runCalc(double x) {
		return funcs.calculate(x);
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return ""+this.id;
	}

}
