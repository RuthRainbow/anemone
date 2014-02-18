package group7.anemone.CPPN;

import java.util.Collection;

/**
 * A CPPN node that computes the Gaussian of its input.
 */
public class CPPNGaussNode extends CPPNNode {
	CPPNGaussNode(boolean isInputNode, boolean isMutatable,
		Collection<CPPNNode> preNodes, Collection<CPPNNode> postNodes)
	{
		super(isInputNode, isMutatable, preNodes, postNodes);
	}
	
	@Override
	public double apply(double inputs) {
		/* TODO */
		return 0.0;
	}
}
