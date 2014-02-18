package group7.anemone.CPPN;

import java.util.Collection;

/**
 * A CPPN node that computes `sin' on its input.
 */
public class CPPNSinNode extends CPPNNode {
	CPPNSinNode(boolean isInputNode, boolean isMutatable,
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
