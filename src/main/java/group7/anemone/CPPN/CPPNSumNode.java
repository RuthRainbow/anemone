package group7.anemone.CPPN;

import java.util.Collection;

/**
 * A CPPN node that computes the sum of its input.
 */
public class CPPNSumNode extends CPPNNode {
	CPPNSumNode(boolean isInputNode, boolean isMutatable,
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
