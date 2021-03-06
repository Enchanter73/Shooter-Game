import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


public class PowerUp {

	//FIELDS
	private double x;
	private double y;
	private int r;
	
	private int type;
	private Color color1;
	
	//CONSTRUCTOR
	public PowerUp(int type, double x, double y) {
		this.type = type;
		this.x = x;
		this.y = y;
		
		if(type == 1) {
			color1 = Color.PINK;
			r=8;
		}
		else if(type == 2){
			color1 = Color.ORANGE;
			r=12;
		}
		else {
			color1 = Color.ORANGE;
			r=6;
		}
	}
	
	//FUNCTIONS
	public double getx() {
		return x;
	}
	public double gety() {
		return y;
	}
	public int getr() {
		return r;
	}
	public int gettype() {
		return type;
	}
	
	public boolean update() {
		
		y += 3;
		
		if(y > GamePanel.HEIGHT + r) {
			return true;
		}
		
		return false;
	}
	
	public void draw(Graphics2D g) {
		
		g.setColor(color1);
		g.fillRect((int)(x - r), (int)(y - r), 2*r, 2*r);
		
		g.setStroke(new BasicStroke(3));
		g.setColor(color1.darker());
		g.drawRect((int)(x - r), (int)(y - r), 2*r, 2*r);
		g.setStroke(new BasicStroke(1));
		
	}
	
	
	
}
