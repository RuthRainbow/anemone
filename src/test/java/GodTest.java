import java.util.ArrayList;

import group7.anemone.Agent;
import group7.anemone.God;
import junit.framework.TestCase;

import org.mockito.Mockito;

public class GodTest extends TestCase {
	
	static God god = new God();
	
	public static void testSetup() {}
	public void testSinglePointCrossover() {
		String mother = "ABCDEF";
		String father = "GHIJKL";
		String child = god.SinglePointCrossover(mother, father);
		assertEquals("ABCJKL", child);	
	}
	
	public void testUniformCrossover() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		String mother = "ABCDEF";
		String father = "GHIJKL";
		String child = spy.UniformCrossover(mother, father);
		assertEquals("ABCDEF", child);	
	}
	
	public void testMutation() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		String mutated = spy.mutate("ABCDEF");
		assertEquals("!BCDEF", mutated);
	}
	
	public void testCreateOffspringNoMutation() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		Agent mother = new Agent("ABCDEF");
		Agent father = new Agent("GHIJKL");
		ArrayList<String> children = spy.CreateOffspring(mother, father);
		assertEquals("ABCJKL", children.get(0));
	}

	public void testCreateOffspringMutation() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.01);
		Agent mother = new Agent("ABCDEF");
		Agent father = new Agent("GHIJKL");
		ArrayList<String> children = spy.CreateOffspring(mother, father);
		assertEquals("!BCJKL", children.get(0));
	}
}
