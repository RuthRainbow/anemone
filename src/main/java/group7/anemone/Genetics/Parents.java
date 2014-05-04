package group7.anemone.Genetics;

import java.io.Serializable;

/**
 * Class to hold the parents of a genetic object.
 */
public class Parents<geneticObject> implements Serializable{
	private static final long serialVersionUID = 7513392753476464850L;
	private final geneticObject mother;
	private final geneticObject father;
	
	public Parents(geneticObject mother, geneticObject father) {
		this.mother = mother;
		this.father = father;
	}

	public geneticObject getFather() {
		return father;
	}
	
	public geneticObject getMother() {
		return mother;
	}
}
