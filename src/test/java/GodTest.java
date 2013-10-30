import group7.anemone.Agent;
import group7.anemone.God;
import junit.framework.TestCase;

import org.mockito.Mockito;

public class GodTest extends TestCase {
	
	static God god = new God();
	
	public static void testSetup() {}
	public void testCrossover() {
		String mother = "ABCDEF";
		String father = "GHIJKL";
		String child = god.crossover(mother, father);
		assertEquals("ABCJKL", child);	
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
		String child = spy.CreateOffspring(mother, father);
		assertEquals("ABCJKL", child);
	}
	
	public void testCreateOffspringMutation() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.01);
		Agent mother = new Agent("ABCDEF");
		Agent father = new Agent("GHIJKL");
		String child = spy.CreateOffspring(mother, father);
		assertEquals("!BCJKL", child);
	}
}
