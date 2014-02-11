package group7.anemone.UI;

import java.util.ArrayList;

import processing.core.PApplet;

public class UITab extends UIWindow{
	private ArrayList<UIWindow> tabs = new ArrayList<UIWindow>();
	private ArrayList<UIButton> buttons = new ArrayList<UIButton>();
	private int currentTab = 0;
	private int numTabs = 0;

	public UITab(PApplet canvas, int x, int y, int w, int h){
		super(canvas, x, y, w, h);
	}
	public UIWindow addTab(String name){
		UIWindow tab = new UIWindow(canvas, 0, 50, width, height - 50);
		addObject(tab);
		tabs.add(tab);

		UIButton btn = new UIButton(canvas, numTabs * 100, 0, 100, 50, name);
		btn.setEventHandler(new UIAction(){
			public void click(UIButton btn){
				tabs.get(currentTab).setVisible(false);
				currentTab = buttons.indexOf(btn);
				tabs.get(currentTab).setVisible(true);

				selectButton(btn);
			}
		});
		addObject(btn);
		buttons.add(btn);

		tab.setVisible(true);
		if(numTabs > 0){
			tab.setVisible(false);
		}
		selectButton(buttons.get(currentTab));
		numTabs++;

		return tab;
	}
	
	public void setTab(int i){
		currentTab = i;
		tabs.get(currentTab).setVisible(true);
		selectButton(buttons.get(currentTab));
	}

}