import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D;


Environment env = new Environment();
void setup() {
  size(1000, 750);
  
  for(int i = 0; i < 10; i++){
    int x = (int) Math.floor(Math.random() * width);
    int y = (int) Math.floor(Math.random() * height);
    env.addAgent(x, y);
  }
  env.getAllAgents().get(0).setThrust(2, 2);
  
  for(int i = 0; i < 10; i++){
    int x = (int) Math.floor(Math.random() * width);
    int y = (int) Math.floor(Math.random() * height);
    env.addFood(x, y);
  }
}
void draw(){
  background(0);
  noStroke();
  fill(255,255,0);
  
  env.updateAllAgents();
  ArrayList<Agent> agents = env.getAllAgents();
  ArrayList<Food> food = env.getAllFood();
  
  for(int i = 0; i < agents.size(); i++){ //drawing the ikkle agents
      Agent ag = agents.get(i);
      ellipse(ag.getX(), ag.getY(), 20, 20);
  }
  
  fill(102,255,0);
  for(int i = 0; i < food.size(); i++){ //drawing the food
      Food fd = food.get(i);
      ellipse(fd.getX(), fd.getY(), 5, 5);
  }
}

class Environment{
   private ArrayList<Agent> agents = new ArrayList();
   private ArrayList<Food> food = new ArrayList();
   
   void updateAllAgents(){
     for(int i = 0; i < agents.size(); i++){ //drawing the ikkle agents
        Agent ag = agents.get(i);
        ag.update();
      }
   }
   
   void addAgent(int x, int y){
       agents.add(new Agent(x, y));
   }
   void addFood(int x, int y){
       food.add(new Food(x, y));
   }
   
   ArrayList<Agent> getAllAgents(){
       return agents;
   }
   ArrayList<Food> getAllFood(){
       return food; 
   }
}

class Food{
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

class Agent{
  private double x = 100;
  private double y = 100;
  private Point2D.Double speed = new Point2D.Double(0, 0);
  private Point2D.Double thrust = new Point2D.Double(0, 0);
  private double acceleration;
  private Point2D.Double drag = new Point2D.Double(0, 0);;
  
  Agent(int a, int b){
    x = a; y = b;
    speed = new Point2D.Double(1, 0);
    
  }
  
  void updateSpeed(){//update speed to be ...
    //calculate new drag value, average of speed x / y
    drag.x = Math.abs(speed.x / 100);
    drag.y = Math.abs(speed.y / 100);
    if(drag.x < 0.0001) speed.x = 0;
    if(drag.y < 0.0001) speed.y = 0;
    
    //implements thrusting
    speed.x += thrust.x;
    speed.y += thrust.y;
    thrust.x = 0;
    thrust.y = 0;
    
    //implements drag
    if(speed.x > 0) speed.x -= drag.x;
    else if(speed.x < 0) speed.x += drag.x;
       
    if(speed.y > 0) speed.y -= drag.y;
    else if(speed.y < 0) speed.y += drag.y;
  }
  void update(){
    updateSpeed();
    
    x += speed.x;
    y += speed.y;

    if(x > width + 10) x = -10;
    if(y > height + 10) y = -10;
  }
  
  void setThrust(double x, double y){
     thrust.x = x; 
     thrust.y = y;
  }
  int getX(){return (int) x;}
  int getY(){return (int) y;}
} 
