package group7.anemone;

public class Food {
	private double x = 100;
	private double y = 100;
	int value = 10;

	Food(int a, int b){
		x = a; y = b;
	}

	int getX(){return (int) x;}
	int getY(){return (int) y;}
	int getValue(){return value;}
}
