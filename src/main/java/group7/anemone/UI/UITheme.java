package group7.anemone.UI;

import java.util.LinkedHashMap;
import java.util.Set;

public class UITheme {
	private LinkedHashMap<String, Integer> elements = new LinkedHashMap<String, Integer>();

	public void setColor(String key, int col){
		elements.put(key, col);
	}
	public int getColor(String key){
		return elements.get(key);
	}
	public String[] getKeys(){
		Set<String> keys = elements.keySet();
		return keys.toArray(new String[keys.size()]);
	}
}
