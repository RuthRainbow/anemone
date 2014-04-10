package group7.anemone.Genetics;

public class SharkGod extends God{
	private static final long serialVersionUID = 1226846014955083162L;
	
	/** Start of possible graphical vars **/
	// Mutation chances:
	public double structuralMutationChance = 0.9f;
	public double addConnectionChance = 0.8f;
	public double addNodeChance = 0.8f;
	public double addGenomeChance = 0.05f;
	public double weightMutationChance = 0.8f;
	// (chance of decrease is 1 - the chance of increase)
	public double weightIncreaseChance = 0.5f;
	public double parameterMutationChance = 0.8f;
	public double parameterIncreaseChance = 0.5f;

	// Crossover chances:
	public double twinChance = 0.01f;
	public double matchedGeneChance = 0.5f;

	public double offspringProportion = 0.1f; // ALSO COMPLETELY ARBITRARY
	// Parameters for use in difference calculation (can be tweaked).
	public double c1 = 0.45f; //weighting of excess genes
	public double c2 = 0.5f; //weighting of disjoint genes
	public double c3 = 0.5f; //weighting of weight differences
	public double c4 = 0.5f; //weighting of excess Genomes
	public double c5 = 0.5; //weight of disjoint Genomes

	// Threshold for max distance between species member and representative.
	// INCREASE THIS IF YOU THINK THERE ARE TOO MANY SPECIES!
	public double compatibilityThreshold = 2;
	public double minReproduced = 3;
	/** End of possible graphical vars **/
	
	/* Getter methods for variables that may differ between God types.*/
	public double getStructuralMutationChance() {
		return this.structuralMutationChance;
	}
	public double getAddConnectionChance() {
		return this.addConnectionChance;
	}
	public double getAddNodeChance() {
		return this.addNodeChance;
	}
	public double getAddGenomeChance() {
		return this.addGenomeChance;
	}
	public double getWeightMutationChance() {
		return this.weightMutationChance;
	}
	public double getWeightIncreaseChance() {
		return this.weightIncreaseChance;
	}
	public double getParameterMutationChance() {
		return this.parameterMutationChance;
	}
	public double getParameterIncreaseChance() {
		return this.parameterIncreaseChance;
	}
	public double getTwinChance() {
		return this.twinChance;
	}
	public double getMatchedGeneChance() {
		return this.matchedGeneChance;
	}
	public double getOffspringProportion() {
		return this.offspringProportion;
	}
	public double getc1() {
		return this.c1;
	}
	public double getc2() {
		return this.c2;
	}
	public double getc3() {
		return this.c3;
	}
	public double getc4() {
		return this.c4;
	}
	public double getc5() {
		return this.c5;
	}
	public double getCompatibilityThreshold() {
		return this.compatibilityThreshold;
	}
	public double getMinReproduced() {
		return this.minReproduced;
	}
}
