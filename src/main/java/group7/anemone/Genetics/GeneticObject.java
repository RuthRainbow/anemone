package group7.anemone.Genetics;

/**
 * An interface to outline the methods required of a genetic object to use in reproduction.
 */
public interface GeneticObject {
	
	public abstract int getSize();
	
	public abstract Object getGeneticRep();
	
	public abstract int getSpeciesId();
	
	public abstract void setSpecies(int speciesId);
	
	public abstract GeneticObject getMother();
	
	public abstract GeneticObject getFather();
}
