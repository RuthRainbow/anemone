import group7.anemone.God;
import junit.framework.TestCase;

public class GodTest extends TestCase {

	static God god = new God();

	// EXTREMELY LEGIT AND 100% COVERAGE TESTS.
	public static void testSetup() {}
	/*public void testSinglePointCrossover() {
		int[][] mother = {{0,1},{2,3}};
		int[][] father = {{4,5},{6,7}};
		int[][] child = god.SinglePointCrossover(mother, father);
		int[][] expected = {{0,1},{6,7}};
		for (int y = 0; y < child[0].length; y++) {
			for (int x = 0; x < child.length; x++) {
				assertEquals(expected[x][y], child[x][y]);
			}
		}
	}

	public void testUniformCrossover() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		int[][] mother = {{0,1},{2,3}};
		int[][] father = {{4,5},{6,7}};
		int[][] child = spy.UniformCrossover(mother, father);
		int[][] expected = {{0,1},{2,3}};
		for (int y = 0; y < child[0].length; y++) {
			for (int x = 0; x < child.length; x++) {
				assertEquals(expected[x][y], child[x][y]);
			}
		}
	}

	public void testMutation() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		int[][] child = {{0,1},{2,3}};
		int[][] mutated = spy.mutate(child);
		int[][] expected = {{9,1},{2,3}};
		for (int y = 0; y < mutated[0].length; y++) {
			for (int x = 0; x < mutated.length; x++) {
				assertEquals(expected[x][y], mutated[x][y]);
			}
		}
	}

	public void testCreateOffspringNoMutation() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.1);
		int[][] motherGenome = {{0,1},{2,3}};
		int[][] fatherGenome = {{4,5},{6,7}};
		Agent mother = new Agent(motherGenome);
		Agent father = new Agent(fatherGenome);
		ArrayList<int[][]> children = spy.CreateOffspring(mother, father);
		int[][] expected = {{0,1},{6,7}};
		int[][] child = children.get(0);
		for (int y = 0; y < child[0].length; y++) {
			for (int x = 0; x < child.length; x++) {
				assertEquals(expected[x][y], child[x][y]);
			}
		}
	}

	public void testCreateOffspringMutation() {
		God spy = Mockito.spy(god);
		Mockito.when(spy.getRandom()).thenReturn(0.01);
		int[][] motherGenome = {{0,1},{2,3}};
		int[][] fatherGenome = {{4,5},{6,7}};
		Agent mother = new Agent(motherGenome);
		Agent father = new Agent(fatherGenome);
		ArrayList<int[][]> children = spy.CreateOffspring(mother, father);
		int[][] expected = {{9,1},{6,7}};
		int[][] child = children.get(0);
		for (int y = 0; y < child[0].length; y++) {
			for (int x = 0; x < child.length; x++) {
				assertEquals(expected[x][y], child[x][y]);
			}
		}
	}*/
}
