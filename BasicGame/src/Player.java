import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


public class Player {
	
	//FIELDS
	private int x;
	private int y;
	private int r;
	
	private int dx;
	private int dy;
	private int speed;
	
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	private boolean firing;
	private long firingTimer;
	private long firingDelay;
	
	private boolean recovering;
	private long recoveryTimer;
	
	private int lives;
	private Color color1;
	private Color color2;
	
	private int score;
	
	private int powerLevel;
	private int power;
	private int[] requiredPower = {1, 2, 3, 4, 5, 6, 7};
	
	//CONSTRUCTOR
	public Player() {
		
		x = GamePanel.WIDTH / 2;
		y = GamePanel.HEIGHT / 2 + 50;
		r = 20;
		
		dx = 0;
		dy = 0;
		speed = 10;
		
		lives = 3;
		color1 = Color.WHITE;
		color2 = Color.RED;
		
		firing = false;
		firingTimer = System.nanoTime();
		firingDelay = 400;
		
		recovering = false;
		recoveryTimer = 0;
		
		score = 0;
	}
	
	//FUNCTIONS	
	public void update() {
		
		if(left) {
			dx = -speed;
		}
		if(right) {
			dx = speed;
		}
		if(up) {
			dy = -speed;
		}
		if(down) {
			dy = speed;
		}
		
		x += dx;
		y += dy;
		
		if(x < r) x = r;
		if(y < r) y = r;
		if(x > GamePanel.WIDTH - r) x = GamePanel.WIDTH - r;
		if(y > GamePanel.HEIGHT - r) y = GamePanel.HEIGHT - r;
		
		dx = 0;
		dy = 0;
		
		//Firing
		if(firing) {
			long elapsed = (System.nanoTime() - firingTimer) / 1000000;
			if(elapsed > firingDelay) {
				
				if(powerLevel < 2) {
					GamePanel.bullets.add(new Bullet(270, x, y));
				}
				else if(powerLevel < 3) {
					GamePanel.bullets.add(new Bullet(270, x-5, y));
					GamePanel.bullets.add(new Bullet(270, x+5, y));
				}
				else if(powerLevel < 4){
					GamePanel.bullets.add(new Bullet(270, x, y));
					GamePanel.bullets.add(new Bullet(275, x+5, y));
					GamePanel.bullets.add(new Bullet(265, x-5, y));
				}
				else {
					GamePanel.bullets.add(new Bullet(270, x, y));
					GamePanel.bullets.add(new Bullet(275, x+5, y));
					GamePanel.bullets.add(new Bullet(265, x-5, y));
					GamePanel.bullets.add(new Bullet(280, x+10, y+5));
					GamePanel.bullets.add(new Bullet(260, x-10, y+5));
				}
				firingTimer = System.nanoTime();
			}
		}
		
		long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
		if(elapsed > 2000) {
			recovering = false;
			recoveryTimer = 0;
		}
		
	}
	
	public boolean isRecovering() {
		return recovering;
	}
	public void loselife() {
		lives--;
		recovering = true;
		recoveryTimer = System.nanoTime();
	}
	public int getscore() {
		return score;
	}	
	public int getlives() {
		return lives;
	}
	public int getx() {
		return x;
	}
	public int gety() {
		return y;
	}
	public int getr() {
		return r;
	}
	public int getPowerLevel() {
		return powerLevel;
	}
	public int getPower() {
		return power;
	}
	public int getRequiredPower() {
		return requiredPower[powerLevel];
	}
	
	public void setLeft(boolean left) {
		this.left = left;
	}
	public void setRight(boolean right) {
		this.right = right;
	}
	public void setUp(boolean up) {
		this.up = up;
	}
	public void setDown(boolean down) {
		this.down = down;
	}	
	public void setFiring(boolean firing) {
		this.firing = firing;
	}
	
	public void addScore(int i) {
		score += i;
	}
	
	public void gainLife() {
		lives++;
	}
	
	public void increasePower(int i) {
		power += i;
		if(power >= requiredPower[powerLevel]) {
			power -= requiredPower[powerLevel];
			powerLevel++;
		}
	}

	public void draw(Graphics2D g) {
		
		if(recovering) {		
			g.setColor(color2);
			g.fillOval(x-r, y-r, 2*r, 2*r);
			
			g.setStroke(new BasicStroke(3));
			
			g.setColor(color2.darker());
			g.drawOval(x-r, y-r, 2*r, 2*r);
			
			g.setStroke(new BasicStroke(1));		
		}	
		else {
			g.setColor(color1);
			g.fillOval(x-r, y-r, 2*r, 2*r);
			
			g.setStroke(new BasicStroke(3));
			
			g.setColor(color1.darker());
			g.drawOval(x-r, y-r, 2*r, 2*r);
			
			g.setStroke(new BasicStroke(1));
		}		
	}
}
