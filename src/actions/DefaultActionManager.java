package actions;

import java.lang.reflect.InvocationTargetException;

import creatures.Creature2;
import enums.Behavior;
import interfaces.ActionManagerI;
import processing.core.PApplet;

public class DefaultActionManager implements ActionManagerI{

	private PApplet processing;
	private Creature2 self;

	@Override
	public void move() {
		changeAngle(self.getDirVariation());
		
		self.setDx(self.getSpeed() * processing.cos(self.getDirection()));  
		self.setDy(self.getSpeed() * processing.sin(self.getDirection()));
		self.setX(self.getX() + self.getDx()); 	
		self.setY(self.getY() + self.getDy()); 
		
		//If the space is defined as toroidal we must restart the coordinates at the borders.
		if(true){
			
			self.setX(self.getX() >= processing.width ? self.getX() - processing.width : self.getX());
			self.setX(self.getX() <= 0 ? self.getX() + processing.width : self.getX());
			self.setY(self.getY() >= processing.height ? self.getY() - processing.height : self.getY());
			self.setY(self.getY() <= 0 ? self.getY() + processing.displayHeight : self.getY());
		}
		
		self.setEnergy(self.getEnergy() - 1);
		
	}

	@Override
	public void draw() {
		processing.rectMode(processing.CENTER);
		processing.stroke(self.getColor().getRGB());
		processing.fill(self.getColor().getRGB());
	    processing.rect(self.getX(),self.getY(),self.getSize(),self.getSize());
		
	}

	@Override
	public void doAction(String action,Class[]cArgs,Object[]args) {
		java.lang.reflect.Method method;
		try {
			method = this.getClass().getMethod(action,cArgs);
			method.invoke(this, args);
		} 
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void solveEncounter(Creature2 other) {
		Behavior behavior = self.getActionChooser().selectAction(self, other);
		
		switch (behavior) {
		case CHASE:
			doAction("perseguirAlOtro", new Class[]{Creature2.class}, new Object[]{other});
			//perseguirAlOtro(other);
			break;
		case RUN_AWAY:
			doAction("huirDelOtro", new Class[]{Creature2.class}, new Object[]{other});
			//huirDelOtro(other);
			break;

		default:
			if(processing.dist(self.getX(), self.getY(), other.getX(), other.getY()) < self.getSize()*4)
				self.setDirection(processing.atan2(self.getY() - other.getY(), self.getX() - other.getX()));
			break;
		}
		
				
		
	}
	
	public void printNameCreature(Creature2 c){
		System.out.println(c.getName());
	}

	@Override
	public void init(PApplet p,Creature2 self) {
		this.processing = p;
		this.self=self;
		
	}
	
	void changeAngle( float angle ){ 

        float rad = processing.radians( angle ); 
        self.setDirection(self.getDirection() + processing.random( -rad , rad )); 

    }
	
	public void huirDelOtro( Creature2 other ){
	    self.setDirection(processing.atan2( self.getY()-other.getY() , self.getX()-other.getX() ));    
	    move();
	}
	
	public void perseguirAlOtro( Creature2 other  ){
	    self.setDirection(processing.atan2( other.getY()-self.getY() , other.getX()-self.getX() ));    
	    move();
	  }


}
