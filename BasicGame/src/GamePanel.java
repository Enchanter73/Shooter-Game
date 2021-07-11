import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {
	
	//FIELDS
	public static int WIDTH=700;
	public static int HEIGHT=700;
	
	private Thread thread;
	private boolean running;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private int FPS = 30;
	private double averageFPS;
	
	public static Player player;
	public static ArrayList<Bullet> bullets;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<PowerUp> powerups;
	
	private long waweStartTimer;
	private long waweStartTimerDiff;
	private int waweNumber;
	private boolean waweStart;
	private int waweDelay = 2000;
		
	//CONSTRUCTOR
	public GamePanel() {
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
	}
	
	//FUCNTIONS
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
	}

	public void run() {
		
		running = true;
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		player = new Player();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		powerups = new ArrayList<PowerUp>();
		
		waweStartTimer = 0;
		waweStartTimerDiff = 0;
		waweStart = true;
		waweNumber = 0;
		
		long startTime;
		long URDTimeMillis;
		long waitTime;
		long totalTime = 0;
		
		int frameCount = 0;
		int maxFrameCount = 30;
		
		long targetTime = 1000 / FPS;
		
		//GAME LOOP
		while(running) {
			
			startTime = System.nanoTime();
			
			gameUpdate();
			gameRender();
			gameDraw();
			
			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - URDTimeMillis;
			
			try {
				Thread.sleep(waitTime);
			}
			catch(Exception e) {			
			}
			
			totalTime += System.nanoTime() - startTime;
			frameCount++;
			
			if(frameCount == maxFrameCount) {
				averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
				frameCount = 0;
				totalTime = 0;
			}						
		}		
	}

	private void gameDraw() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	private void gameRender() {
		//draw background
		g.setColor(new Color(0, 100, 255));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//writing FPS
		g.setColor(Color.BLACK);
		g.setFont(new Font("Century Gothic", Font.PLAIN, 30));
		String fps = "FPS: " + averageFPS;
		g.drawString(fps, 10, 30);
		
		//player draws
		player.draw(g);
		
		//bullet draws
		for(int i=0; i<bullets.size(); i++) {
			bullets.get(i).draw(g);
		}
		
		//enemy draws
		for(int i=0; i<enemies.size(); i++) {
			enemies.get(i).draw(g);
		}
		
		//draw power ups
		for(int i=0; i<powerups.size(); i++) {
			powerups.get(i).draw(g);
		}
		
		//draw wawe number
		if(waweStartTimer != 0) {
			g.setFont(new Font("Century Gothic", Font.PLAIN, 40));
			String s = "-  W A W E  " + waweNumber + "  -";
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			int alpha = (int) (255 * Math.sin(3.14 * waweStartTimerDiff / waweDelay));
			if(alpha > 255) alpha = 255;
			g.setColor(new Color(255, 255, 255, alpha));
			g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
		}
		//draw player lives
		for(int i=0; i<player.getlives(); i++) {
			g.setColor(Color.WHITE);
			g.fillOval((WIDTH-50) - (40*i), 20, player.getr() * 2, player.getr() * 2);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());
			g.drawOval((WIDTH-50) - (40*i), 20, player.getr() * 2, player.getr() * 2);
			g.setStroke(new BasicStroke(1));
		}
		//draw player score
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.PLAIN, 40));
		g.drawString("Score: " + player.getscore(), 20, 58);
		
		//draw player power
		g.setColor(Color.YELLOW);
		g.fillRect(WIDTH-30, 100, player.getPower() * 20, 20);
		g.setColor(Color.YELLOW.darker());
		g.setStroke(new BasicStroke(2));
		for(int i=0; i<player.getRequiredPower(); i++) {
			g.drawRect((WIDTH-30) - 21*i, 100, 20, 20);
		}
		g.setStroke(new BasicStroke(1));
		
	}

	private void gameUpdate() {	
		
		//new wawe
		if(waweStartTimer == 0 && enemies.size() == 0) {
			waweNumber++;
			waweStart = false;
			waweStartTimer = System.nanoTime();
		}
		else {
			waweStartTimerDiff = (System.nanoTime() - waweStartTimer) / 1000000;
			if(waweStartTimerDiff > waweDelay) {
				waweStart = true;
				waweStartTimer = 0;
				waweStartTimerDiff = 0;
			}
		}
		//Create enemies
		if(waweStart && enemies.size() == 0) {
			createNewEnemies();		
		}
		
		//Player updates
		player.update();
		
		//Bullet updates
		for(int i=0; i<bullets.size(); i++) {
			boolean remove = bullets.get(i).update();
			if(remove) {
				bullets.remove(i);
				i--;
			}
		}
		//Enemy Updates
		for(int i=0; i<enemies.size(); i++) {
			enemies.get(i).update();
		}
		
		//power up updates
		for(int i=0; i<powerups.size(); i++) {
			boolean remove = powerups.get(i).update();
			if(remove) {
				powerups.remove(i);
				i--;
			}
		}
		
		//bullet-enemy collision
		for(int i=0; i<bullets.size(); i++) {
			
			Bullet b = bullets.get(i);
			double bx = b.getx();
			double by = b.gety();
			double br = b.getr();
			
			for(int j=0; j<enemies.size(); j++) {
				
				Enemy e = enemies.get(j);
				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();
				
				double dx = bx - ex;
				double dy = by - ey;
				double dist = Math.sqrt(dx*dx + dy*dy);
				
				if(dist < br+er) {
					e.hit();
					bullets.remove(i);
					i--;
					break;
				}
			}
		}
		//player - power up collision
		if(!player.isRecovering()) {
			int px = player.getx();
			int py = player.gety();
			int pr = player.getr();
			
			for(int j=0; j<powerups.size(); j++) {
				
				PowerUp u = powerups.get(j);
				double ux = u.getx();
				double uy = u.gety();
				double ur = u.getr();
				
				double dx = px - ux;
				double dy = py - uy;
				double dist = Math.sqrt(dx*dx + dy*dy);
				
				//collected power up
				if(dist < ur + pr) {
					
					int type = u.gettype();
					
					if(type == 1) {
						player.gainLife();
					}
					if(type == 2) {
						player.increasePower(2);
					}
					if(type == 3) {
						player.increasePower(1);
					}					
					powerups.remove(j);
					j--;
				}
				
			}	
		}
		
		//Check dead enemies
		for(int i=0; i<enemies.size(); i++) {
			if(enemies.get(i).isDead()) {
				
				Enemy e = enemies.get(i);
				
				//rng for powerup
				double rand = Math.random();
				if(rand < 0.01) powerups.add(new PowerUp(1, e.getx(), e.gety()));
				else if(rand < 0.05) powerups.add(new PowerUp(2, e.getx(), e.gety()));
				else if(rand < 0.1) powerups.add(new PowerUp(3, e.getx(), e.gety()));
				
				player.addScore((e.getRank() + e.getType())*5);
				enemies.remove(i);
				i--;
				
			}
		}	
		// Player - enemy collision
		if(!player.isRecovering()) {
			int px = player.getx();
			int py = player.gety();
			int pr = player.getr();
			
			for(int j=0; j<enemies.size(); j++) {
				
				Enemy e = enemies.get(j);
				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();
				
				double dx = px - ex;
				double dy = py - ey;
				double dist = Math.sqrt(dx*dx + dy*dy);
				
				if(dist < pr+er) {
					player.loselife();
				}
			}	
		}
	}
		
	private void createNewEnemies() {
		enemies.clear();
		Enemy e;
		if(waweNumber == 1) {
			for(int i=0; i<5; i++) {
				enemies.add(new Enemy(1, 1));
			}
		}
		if(waweNumber == 2) {
			for(int i=0; i<5; i++) {
				enemies.add(new Enemy(1, 1));
			}
			for(int i=0; i<5; i++) {
				enemies.add(new Enemy(2, 1));
			}
		}
		if(waweNumber == 3) {
			for(int i=0; i<5; i++) {
				enemies.add(new Enemy(1, 1));
			}
			for(int i=0; i<5; i++) {
				enemies.add(new Enemy(3, 1));
			}
			for(int i=0; i<5; i++) {
				enemies.add(new Enemy(2, 1));
			}
		}
		if(waweNumber == 4) {
			for(int i=0; i<8; i++) {
				enemies.add(new Enemy(1, 1));
			}
			for(int i=0; i<8; i++) {
				enemies.add(new Enemy(2, 1));
			}
			for(int i=0; i<8; i++) {
				enemies.add(new Enemy(3, 1));
			}
		}
		if(waweNumber == 5) {
			for(int i=0; i<10; i++) {
				enemies.add(new Enemy(1, 1));
			}
			for(int i=0; i<10; i++) {
				enemies.add(new Enemy(2, 1));
			}
			for(int i=0; i<10; i++) {
				enemies.add(new Enemy(3, 1));
			}
		}
	}
	
	public void keyTyped(KeyEvent key) {}
	
	public void keyPressed(KeyEvent key) {
		int keyCode = key.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT) {
			player.setLeft(true);
		}
		if(keyCode == KeyEvent.VK_RIGHT) {
			player.setRight(true);
		}
		if(keyCode == KeyEvent.VK_UP) {
			player.setUp(true);
		}
		if(keyCode == KeyEvent.VK_DOWN) {
			player.setDown(true);
		}
		if(keyCode == KeyEvent.VK_Z) {
			player.setFiring(true);
		}
	}
	
	public void keyReleased(KeyEvent key) {
		int keyCode = key.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT) {
			player.setLeft(false);
		}
		if(keyCode == KeyEvent.VK_RIGHT) {
			player.setRight(false);
		}
		if(keyCode == KeyEvent.VK_UP) {
			player.setUp(false);
		}
		if(keyCode == KeyEvent.VK_DOWN) {
			player.setDown(false);
		}
		if(keyCode == KeyEvent.VK_Z) {
			player.setFiring(false);
		}
	}
		
}
