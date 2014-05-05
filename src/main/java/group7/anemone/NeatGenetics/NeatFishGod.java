package group7.anemone.NeatGenetics;

/**
 * This class provides the parameter values for the genetic algorithm breeding
 * agents/fish for NEAT.
 */
public class NeatFishGod extends NeatGod {
	private static final long serialVersionUID = -6489249838484577534L;

	/** Start of graphical vars **/
	// Mutation chances:
	public double structuralMutationChance = 0.9f;
	public double addConnectionChance = 0.8f;
	public double addNodeChance = 0.8f;
	public double weightMutationChance = 0.8f;
	// (chance of decrease is 1 - the chance of increase)
	public double weightIncreaseChance = 0.5f;
	public double parameterMutationChance = 0.8f;
	public double parameterIncreaseChance = 0.5f;

	// Crossover chances:
	public double twinChance = 0.05f;
	public double matchedGeneChance = 0.5f;

	// Change this if you think too many offspring are being generated.
	public double offspringProportion = 0.05f;
	// Parameters for use in difference calculation.
	public double c1 = 0.45f; //weighting of excess genes
	public double c2 = 0.5f; //weighting of disjoint genes
	public double c3 = 0.5f; //weighting of weight differences

	// Threshold for max distance between species member and representative.
	public double compatibilityThreshold = 5;
	public double minReproduced = 3;
	// Threshold over which an agent's fitness isn't counted in the sharing function.
	public double sharingThreshold = 20;
	/** End of graphical vars **/

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
	public double getCompatibilityThreshold() {
		return this.compatibilityThreshold;
	}
	public double getMinReproduced() {
		return this.minReproduced;
	}
	public String toString(){
		return "Fish God";
	}
	@Override
	public void setCompatabilityThreshold(double threshold) {
		this.compatibilityThreshold = threshold;
	}
	@Override
	public double getSharingThreshold() {
		return this.sharingThreshold;
	}
}
