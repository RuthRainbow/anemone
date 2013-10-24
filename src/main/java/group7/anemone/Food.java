package group7.anemone;

public class Food {
	private Coordinates coords = new Coordinates(100, 100);
	int value = 10;

	Food(Coordinates coords){
		this.coords = coords;
	}

	int getX(){return (int) coords.x;}
	int getY(){return (int) coords.y;}
	int getValue(){return value;}
}
