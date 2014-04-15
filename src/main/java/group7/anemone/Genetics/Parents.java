package group7.anemone.Genetics;

public class Parents<geneticObject> {
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
