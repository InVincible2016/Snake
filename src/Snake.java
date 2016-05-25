import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
	private BufferedImage BackGroundImg;
	private BufferedImage HeadImgUp;
	private BufferedImage HeadImgDown;
	private BufferedImage HeadImgLeft;
	private BufferedImage HeadImgRight;
	private BufferedImage BodyImg;
	private BufferedImage FoodImg;

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

	public int BodyLength;

	private int direction; // UP=0, DOWN =1, LEFT=2, RIGHT=3;

	private int FPS;
	private int Eaten;
	private int Score;
	private int Preset_GameSpeed, CurrentGameSpeed;

	public boolean paused, over;
	private boolean LevelUpEnabled = false;
	private boolean ActionUpdated;
	private boolean GameStarted;

	private JFrame jframe;
	Timer timer;

	public static void main(String[] args) {
		Snake s;
		if (args.length != 0) {
			s = new Snake(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		} else {
			s = new Snake(30, 3	);// Default game mode at 30 fps and speed of 2;
		}
		long Gametick = 0;// This is used to control game speed.

		while (true) {// game proceed.
			if (Gametick >= 800000000 * 2 && !s.paused && !s.over) { // gamespeed
																		// offset
				s.SnakeUpdate();
				Gametick = 0;
			}
			Gametick += s.CurrentGameSpeed; // GameSpeed ranges from 1-10;

		}
	}

	private boolean HitBoundary() {
		// Check if snake hits boundary.
		if (head.x < Grid_X || head.x + scal > Grid_Width + Grid_X || head.y < Grid_Y
				|| head.y + scal > Grid_Height + Grid_Y) {
			return true;
		}
		return false;

	}

	private boolean HitBody() {
		if (SnakeBody.size() < 5) {// It's impossible that a snake hit itself
									// with length less than 5;
			return false;
		} else {
			for (int i = SnakeBody.size() - 3; i >= 0; i--) {
				if (head.intersects(SnakeBody.get(i))) {
					return true;
				}
			}
		}
		return false;

	}

	private void GenerateBody() {
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
		switch (direction) {
		case 0:
			for (int i = BodyLength - 1; i > 0; i--) {
				SnakeBody.add(new Rectangle(StartX + Grid_X, StartY + Grid_Y + i * scal, scal, scal));
			}
			break;
		case 1:
			for (int i = BodyLength - 1; i > 0; i--) {
				SnakeBody.add(new Rectangle(StartX + Grid_X, StartY + Grid_Y - i * scal, scal, scal));
			}
			break;
		case 2:
			for (int i = BodyLength - 1; i > 0; i--) {
				SnakeBody.add(new Rectangle((StartX + Grid_X) + i * scal, StartY + Grid_Y, scal, scal));
			}
			break;
		case 3:
			for (int i = BodyLength - 1; i > 0; i--) {
				SnakeBody.add(new Rectangle((StartX + Grid_X) - i * scal, StartY + Grid_Y, scal, scal));
			}
			break;
		}
		head = new Rectangle(StartX + Grid_X, StartY + Grid_Y, scal, scal);
		SnakeBody.add(head);
	}
	
	private boolean CheckInsideSnake(){
		for (int i =0; i<SnakeBody.size();i++){
			if (food.intersects(SnakeBody.get(i))){
				return true;
			}
		}
		return false;
	}
	
	private void GenerateFood() {
		Random rand = new Random();
			food = new Rectangle(((rand.nextInt(Grid_Width / scal)) * scal) + Grid_X,
					((rand.nextInt(Grid_Height / scal)) * scal) + Grid_Y, scal, scal);
			while(CheckInsideSnake()){//if food is part of snake, re-generate;
				food = new Rectangle(((rand.nextInt(Grid_Width / scal)) * scal) + Grid_X,
						((rand.nextInt(Grid_Height / scal)) * scal) + Grid_Y, scal, scal);
			}
	}

				

	public void SnakeUpdate() {
		if (GameStarted) {
			if (head.intersects(food)) {
				BodyLength++;
				if (LevelUpEnabled) {
					Eaten++;
					if (Eaten >= 8 && CurrentGameSpeed <= 10) {// Fasten game
																// speed
																// after
						// eating three foods.
						CurrentGameSpeed++;
						Eaten = 0;
					}
				}
				Score += 100 * CurrentGameSpeed;
				GenerateFood();
			}
			if (!HitBoundary() && !HitBody()) { // check hit boundary + hit body
												// bool
				switch (direction) {
				case 1: // down
					head = new Rectangle(head.x, head.y + scal, scal, scal);
					break;
				case 2: // left
					head = new Rectangle(head.x - scal, head.y, scal, scal);
					break;
				case 3: // right
					head = new Rectangle(head.x + scal, head.y, scal, scal);
					break;
				default: // up
					head = new Rectangle(head.x, head.y - scal, scal, scal);
				}
			} else {
				over = true;
				ActionUpdated = true;
				return;
			}

			SnakeBody.add(new Rectangle(head.x, head.y, scal, scal));// add to
																		// the
																		// end
																		// of
																		// SnakeBody,
																		// i.e.
																		// index
																		// (size-1);
			if (SnakeBody.size() > BodyLength) {
				SnakeBody.remove(0);
			}
			Score = Score + BodyLength / 2 + CurrentGameSpeed / 2;// Calculate
																	// Player's
																	// Score.
			ActionUpdated = true;
		}
	}

	public Snake(int fps, int gamespeed) {
		try {
			BackGroundImg = ImageIO.read(new File("BackGround.png"));
			HeadImgUp = ImageIO.read(new File("SnakeHeadUp.png"));
			HeadImgDown = ImageIO.read(new File("SnakeHeadDown.png"));
			HeadImgLeft = ImageIO.read(new File("SnakeHeadLeft.png"));
			HeadImgRight = ImageIO.read(new File("SnakeHeadRight.png"));
			BodyImg = ImageIO.read(new File("SnakeBody.png"));
			FoodImg = ImageIO.read(new File("Food.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		Score = 0;
		CurrentGameSpeed = Preset_GameSpeed;
		paused = false;
		ActionUpdated = false;
		BodyLength = 5;
		Eaten = 0;
		GenerateBody();
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
				if (ActionUpdated == true && !over) {// Wait and make sure the
														// last
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
							ActionUpdated = false;
						}
						break;
					case KeyEvent.VK_LEFT:
						if (direction != 3 && direction != 2) {
							direction = 2; // set to go left
							ActionUpdated = false;
						}
						break;
					case KeyEvent.VK_RIGHT:
						if (direction != 3 && direction != 2) {
							direction = 3;// set to go right
							ActionUpdated = false;
						}
						break;

					}
				}
				if (keyCode == KeyEvent.VK_R) {
					reset();
				}
				if (keyCode == KeyEvent.VK_SPACE) {
					if (paused == false) {
						paused = true;
					} else {
						paused = false;
					}
				}
				if (keyCode == KeyEvent.VK_L) {
					if (LevelUpEnabled) {
						LevelUpEnabled = false;

					} else {
						LevelUpEnabled = true;
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
			g2.setBackground(Color.BLACK);
			drawFood(g2);
			drawSnake(g2);
			drawLine(g2);
			drawGUI(g2);

			if (over) {
				drawGUIDead(g2);
			}

		} else {
			g2.setBackground(Color.GRAY);
			drawMenuScreen(g2);
		}
	}

	public void drawFood(Graphics2D g) {
		g.drawImage(FoodImg, food.x, food.y, food.width, food.height, null);
	}

	public void drawSnake(Graphics2D g) {
		// g.setColor(Color.RED);

		for (int i = 0; i < SnakeBody.size(); i++) {
			Rectangle temp = SnakeBody.get(i);
			if (i == SnakeBody.size() - 1) {
				switch (direction) {
				case 0:
					g.drawImage(HeadImgUp, head.x, head.y, head.width, head.height, null);
					break;
				case 1:
					g.drawImage(HeadImgDown, head.x, head.y, head.width, head.height, null);
					break;

				case 2:
					g.drawImage(HeadImgLeft, head.x, head.y, head.width, head.height, null);
					break;

				case 3:
					g.drawImage(HeadImgRight, head.x, head.y, head.width, head.height, null);
					break;
				}
			} else {
				g.drawImage(BodyImg, temp.x, temp.y, temp.width, temp.height, null);
			}

		}

		// g.drawString(Integer.toString(temp.x) + " ," +
		// Integer.toString(temp.y), 650, 400 + (i * 10));
		// g.drawString(Integer.toString(tempint), 750,400+(i*10));
		// g.drawString(Boolean.toString(over), 700,290);
		// g.drawString(Boolean.toString(paused), 700,300);
	}

	// Draw coordinate of head; For debugging purpose.
	// g2.drawString(Integer.toString(head.x), 300, 300);
	// g2.drawString(" , ", 350, 300);
	// g2.drawString(Integer.toString(head.y), 400, 300);

	public void drawLine(Graphics2D g) {
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

	public void drawGUI(Graphics2D g) {
		String Info1 = "Speed inscreases every 8";
		String Info2 = "foods eaten.";
		String Info6 = "Next Level in: ";
		String Info3 = "Current Speed: ";
		String Info4 = "Press key 'r' to start.";
		String Info5 = "Current Score: ";
		Font TitleFont = new Font(Font.SERIF, Font.BOLD, 17);

		g.setColor(Color.RED);
		g.setFont(TitleFont);
		if (LevelUpEnabled) {
			g.drawString(Info1, 615, 180);
			g.drawString(Info2, 615, 200);
			g.drawString(Info6 + Integer.toString(8 - Eaten), 615, 215);
		}
		g.drawString(Info3 + Integer.toString(CurrentGameSpeed), 615, 230);
		g.drawString(Info5 + Integer.toString(Score), 615, 250);
		// g.drawString(Boolean.toString(over), 615, 245);
		// g.drawString(Boolean.toString(paused), 615, 260);
		// g.drawString(Boolean.toString(GameStarted), 615, 275);
		g.drawString(Info4, 615, 300);

	}

	public void drawGUIDead(Graphics2D g) {
		String Title1 = "Game Over";
		String Title2 = "press 'r' to restart";
		Font TitleFont = new Font(Font.SERIF, Font.BOLD, 40);

		g.setColor(Color.RED);
		g.setFont(TitleFont);
		g.drawString(Title1, 150, 300);
		g.drawString(Title2, 150, 350);
	}

	public void drawMenuScreen(Graphics2D g) {
		g.drawImage(BackGroundImg, 0, 0, 800, 600, 0, 0, 1500, 1500, null);
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
		g.setColor(Color.RED);
		g.drawString(Instruction1, 200, 480);
		g.drawString(Instruction2, 200, 510);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Render.
		repaint();
	}
}
