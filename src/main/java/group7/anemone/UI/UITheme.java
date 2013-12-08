package group7.anemone.UI;

import java.util.LinkedHashMap;
import java.util.Set;

public class UITheme {
	public enum Types {
		SHARK, FISH, FOOD, WALL, BACKGROUND, SIDEPANEL1, NEURON, NEURON_FIRED
	}
	
	private LinkedHashMap<Types, Integer> elements = new LinkedHashMap<Types, Integer>();

	public void setColor(Types key, int col){
		elements.put(key, col);
	}
	public int getColor(Types types){
		return elements.get(types);
	}
	public Types[] getKeys(){
		Set<Types> keys = elements.keySet();
		return keys.toArray(new Types[keys.size()]);
	}
}
