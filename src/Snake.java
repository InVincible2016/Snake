import javax.swing.*;

import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Snake extends JComponent implements ActionListener {
	public Snake snake;

	private final int Window_Width = 800;
	private final int Window_Height = 600;

	private final int Grid_Width = 600;
	private final int Grid_Height = 540;

	private final int Grid_X = 10;// Coordinates of the grid, top right corner
	private final int Grid_Y = 20;

	private final int scal = 20;

	private int StartX;
	private int StartY;

	private ArrayList<Rectangle> SnakeBody;

	private Rectangle head, food;

	private int BodyLength;

	private int direction; // UP=0, DOWN =1, LEFT=2, RIGHT=3;

	private int FPS;
	private int Eaten;
	private int Preset_GameSpeed, CurrentGameSpeed;

	public boolean paused, over, falseStart = false;
	private boolean ActionUpdated;
	public boolean GameStarted;
	

	private JFrame jframe;
	Timer timer;

	public static void main(String[] args) {
		Snake s;
		if (args.length != 0) {
			s = new Snake(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		} else {
			s = new Snake(10, 2);// Default game mode at 30 fps and speed of 2;
		}
		while (true) { // Waiting for user to press key 's' to start game;
			if (s.GameStarted) {
				break;
			}
			System.out.println("Waiting");
		}
		s.timer.restart();// reset render timer.

		long Gametick = 0;// This is used to control game speed.
		while (true) {// game proceed.
			// System.out.println("Running");
			// System.out.println(Gametick);
			if (Gametick >= 800000000 * 2 && !s.paused && !s.over) { // gamespeed
																		// offset
				s.SnakeUpdate();
				Gametick = 0;
			}
			Gametick += s.CurrentGameSpeed; // GameSpeed ranges from 1-10;

		}
	}

	private boolean HitBoundary() {
		// Check if snake hits boundary;
		/*
		if (head.x < Grid_X || head.x + scal > Grid_Width + Grid_X || head.y < Grid_Y
				|| head.y + scal > Grid_Height + Grid_Y) {
			System.out.println("HitBoundary");
			return true;
		}
		return false;
		*/
		return head.x < scal || head.x > (Grid_Width - scal) || head.y < 2 * scal || head.y > (Grid_Height - scal);
	}

	private boolean HitBody() {
		// Check is snake hits itself;
		for (int i = 0; i < BodyLength - 2; i++) {		
			if (head.getX() == SnakeBody.get(i).getX() && head.getY() == SnakeBody.get(i).getY()) {
				if (falseStart == false) { // disables first collision on start up
					falseStart = true;
				}
				else {
					return true;
				}
			}
		}
		return false;
	}

	private void GenerateFood() {
		//
		Random rand = new Random();
		if (food == null) {
			food = new Rectangle(((rand.nextInt(Grid_Width / scal)) * scal) + Grid_X,
					((rand.nextInt(Grid_Height / scal)) * scal) + Grid_Y, scal, scal);
		}

		else {
			food.setLocation(((rand.nextInt(Grid_Width / scal)) * scal) + Grid_X,
					((rand.nextInt(Grid_Height / scal)) * scal) + Grid_Y);
		}
	}

	public void SnakeUpdate() {

		if (head.intersects(food)) {
			BodyLength++;
			Eaten++;
			if (Eaten >= 5 && CurrentGameSpeed <= 10) {// Fasten game speed
														// after
				// eating three foods.
				CurrentGameSpeed++;
				Eaten = 0;
			}
			GenerateFood();
		}
		if (!HitBoundary() && !HitBody()) {	// check hit boundary + hit body bool
			switch(direction) {
				case 1:		// down
					head = new Rectangle(head.x, head.y + scal, scal, scal);
					break;
				case 2:		// left
					head = new Rectangle(head.x - scal, head.y, scal, scal);
					break;
				case 3:		// right
					head = new Rectangle(head.x + scal, head.y, scal, scal);
					break;
				default:	// up
					head = new Rectangle(head.x, head.y - scal, scal, scal);
			}
		}
		else {
			System.out.println("dead");
			over = true;
			ActionUpdated = true;
			return;
		}

		SnakeBody.add(new Rectangle(head.x, head.y, scal, scal));// add to the
																	// end of
																	// SnakeBody,
																	// i.e.
																	// index
																	// (size-1);
		if (SnakeBody.size() > BodyLength) {
			SnakeBody.remove(0);
		}
		ActionUpdated = true;
	}

	public Snake(int fps, int gamespeed) {
		Preset_GameSpeed = gamespeed;
		GameStarted = false;
		FPS = fps;
		jframe = new JFrame("Snake");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(Window_Width, Window_Height);
		jframe.setResizable(false);
		jframe.addKeyListener(new MyKeyListener());

		jframe.setContentPane(this);

		jframe.setVisible(true);
		InitiateGameData();

	}

	private void InitiateGameData() {
		// Set initial game data.
		CurrentGameSpeed = Preset_GameSpeed;
		paused = false;

		ActionUpdated = false;
		BodyLength = 3;
		Eaten = 0;
		Random rand = new Random();
		SnakeBody = new ArrayList<Rectangle>();

		// Generate starting head coordinate of the snake
		StartX = (100 + (rand.nextInt(400 / scal)) * scal);// Adjusted to avoid
															// dying when the
															// game just
															// started;
		StartY = (100 + (rand.nextInt(300 / scal)) * scal);
		direction = rand.nextInt(4);// random direction 0 to 3

		// Generate Body:
		head = new Rectangle(StartX + Grid_X, StartY + Grid_Y, scal, scal);
		if (direction == 0) {
			for (int i = BodyLength - 1; i > 0; i--) {
				SnakeBody.add(new Rectangle(StartX + Grid_X, StartY + Grid_Y + i * scal, scal, scal));
			}
		} else {
			for (int i = BodyLength - 1; i > 0; i--) {
				SnakeBody.add(new Rectangle(StartX + Grid_X, StartY + Grid_Y - i * scal, scal, scal));
			}
		}
		SnakeBody.add(head);
		GenerateFood();
		over = false;
		timer = new Timer((int) 1000 / FPS, this); // e.g FPS=50, then call
													// repaint() every 20
													// milliseconds.
													// (1000/50=20)
		timer.start();
	}

	private class MyKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (GameStarted) {
				if (ActionUpdated == true) {// Wait and make sure the last
											// action has been updated.
					switch (keyCode) {
						case KeyEvent.VK_UP:
							if (direction != 1 && direction != 0) {
								direction = 0; // set to go up
								ActionUpdated = false;
							}
							break;
						case KeyEvent.VK_DOWN:
							if (direction != 0 && direction != 1) {
								direction = 1;// set to go down
								ActionUpdated=false;
							}
							break;
						case KeyEvent.VK_LEFT:
							if (direction != 3 && direction != 2) {
								direction = 2; // set to go left
								ActionUpdated=false;	
							}
							break;
						case KeyEvent.VK_RIGHT:
							if (direction != 3 && direction != 2) {
								direction = 3;// set to go right
								ActionUpdated=false;
							}
							break;
						case KeyEvent.VK_SPACE:
							if (paused == false) {
								paused = true;
								break;
							} 
							else {
								paused = false;
								break;
							}	
						case KeyEvent.VK_R:
							reset();
							break;
					}
				}
			} else if (keyCode == KeyEvent.VK_S) { // 's' for Start.
				// System.out.println("Start");
				GameStarted = true;
			}
		}
	}

	private void reset() {
		// Reset Game data. program continues.
		timer.stop();
		ActionUpdated = false;
		repaint();
		InitiateGameData();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // 2D drawing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // antialiasing
																// look
																// nicer
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (GameStarted) {
			// Draw
			drawFood(g);
			drawSnake(g);
			drawLine(g);
			drawGUI(g);
			
			if (over) {
				drawGUIDead(g);
			}

		} else {
			g2.setBackground(Color.GRAY);
			drawMenuScreen(g);
		}
	}
	
	public void drawFood(Graphics g) {
		g.setColor(Color.GREEN);//
		g.fillRect(food.x, food.y, food.width, food.height);
	}
	
	public void drawSnake(Graphics g) {
		g.setColor(Color.RED);
		for (int i = 0; i < SnakeBody.size(); i++) {
			Rectangle temp = SnakeBody.get(i);
			g.fillRect(temp.x, temp.y, temp.width, temp.height);
			// g2.drawString(Integer.toString(temp.x)+ " ,
			// "+Integer.toString(temp.y), 700,400+(i*10));
			// g2.drawString(Boolean.toString(over), 700,290);
			// g2.drawString(Boolean.toString(paused), 700,300);
		}

		// Draw coordinate of head; For debugging purpose.
		// g2.drawString(Integer.toString(head.x), 300, 300);
		// g2.drawString(" , ", 350, 300);
		// g2.drawString(Integer.toString(head.y), 400, 300);	
	}
	
	public void drawLine(Graphics g) {
		// Draw Horizontal lines:
		g.setColor(Color.BLUE);
		for (int i = 0; i <= Grid_Height; i += scal) {
			g.drawLine(Grid_X, Grid_Y + i, Grid_Width + Grid_X, i + Grid_Y);
		}
		// Draw Vertical lines:
		for (int i = 0; i <= Grid_Width; i += scal) {
			g.drawLine(i + Grid_X, Grid_Y, i + Grid_X, Grid_Height + Grid_Y);
		}
	}
	
	public void drawGUI(Graphics g) {
		String Title1 = "Speed inscreases every 5";
		String Title2 = "foods eaten.";
		String Title3 = "Current Speed: ";
		String Title4 = "Press key 'r' to start.";
		Font TitleFont = new Font(Font.SERIF, Font.BOLD, 15);
		
		g.setColor(Color.RED);
		g.setFont(TitleFont);
		g.drawString(Title1, 615, 200);
		g.drawString(Title2, 615, 215);
		g.drawString(Title3 + Integer.toString(CurrentGameSpeed), 615, 230);
		g.drawString(Title4, 615, 300);
	}
	
	public void drawGUIDead(Graphics g) {
		String Title1 = "Game Over";
		String Title2 = "press 'r' to restart";
		Font TitleFont = new Font(Font.SERIF, Font.BOLD, 40);
		
		g.setColor(Color.RED);
		g.setFont(TitleFont);
		g.drawString(Title1, 150, 300);
		g.drawString(Title2, 150, 350);
	}

	public void drawMenuScreen(Graphics g) {
		String Title = "Changlong Zhong's Snake";
		String My_Userid = "User ID: Clzhong";
		String Instruction1 = " Use Arraw keys to control the snake";
		String Instruction2 = " Press key 's' to start the game.";
		Font TitleFont = new Font(Font.SERIF, Font.BOLD, 40);
		Font TitleFont2 = new Font(Font.SANS_SERIF, Font.ITALIC, 30);
		Font InstructionFont = new Font(Font.SERIF, Font.PLAIN, 20);

		g.setFont(TitleFont);
		g.setColor(Color.BLUE);
		g.drawString(Title, 150, 300);
		g.setFont(TitleFont2);
		g.drawString(My_Userid, 200, 400);
		g.setFont(InstructionFont);
		g.setColor(Color.GRAY);
		g.drawString(Instruction1, 200, 480);
		g.drawString(Instruction2, 200, 510);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Render.
		repaint();
	}
}
