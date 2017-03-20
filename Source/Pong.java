package pongGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Pong implements ActionListener, KeyListener {

	public static Pong pong;
	public Renderer renderer;
	public Ball ball;
	public Random random;
	public JFrame jframe;
	
	// Window Size
	public int width = 800, height = 450;

	// Player 1 Controls
	public Paddle player1;
	public boolean w, s;
	
	// Player 2 Controls
	public Paddle player2;
	public boolean up, down;

	public boolean bot = false, selectingDifficulty;

	// 0 = Menu, 1 = Paused, 2 = Playing, 3 = Over
	public int gameStatus = 0;
	public int scoreLimit = 7, playerWon;

	public int botDifficulty, botMoves, botCooldown = 0;

	public Pong() {
		Timer timer = new Timer(20, this);
		random = new Random();

		jframe = new JFrame("Pong");

		renderer = new Renderer();

		jframe.setSize(width + 15, height + 35);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(renderer);
		jframe.addKeyListener(this);

		timer.start();
	}

	public void start() {
		gameStatus = 2;
		player1 = new Paddle(this, 1);
		player2 = new Paddle(this, 2);
		ball = new Ball(this);
	}

	public void update() {
		if (player1.score >= scoreLimit) {
			playerWon = 1;
			gameStatus = 3;
		}
		
		if (player2.score >= scoreLimit) {
			gameStatus = 3;
			playerWon = 2;
		}
		
		// Player 1
		if (w) {
			player1.move(true);
		}
		if (s) {
			player1.move(false);
		}
		
		// Player 2 or Bot
		if (!bot) {
			if (up) {
				player2.move(true);
			}
			if (down) {
				player2.move(false);
			}
		} else {
			if (botCooldown > 0) {
				botCooldown--;
				if (botCooldown == 0) {
					botMoves = 0;
				}
			}
			if (botMoves < 10) {
				if (player2.y + player2.height / 2 < ball.y) {
					player2.move(false);
					botMoves++;
				}
				if (player2.y + player2.height / 2 > ball.y) {
					player2.move(true);
					botMoves++;
				}
				if (botDifficulty == 0) {
					botCooldown = 20;
				}
				if (botDifficulty == 1) {
					botCooldown = 15;
				}
				if (botDifficulty == 2) {
					botCooldown = 10;
				}
			}
		}
		ball.update(player1, player2);
	}

	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (gameStatus == 0) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));
			g.drawString("PONG", width / 2 - 75, height - 8*height/9); // Center = Width/2 - Word Length

			if (!selectingDifficulty) {
				g.setFont(new Font("Arial", 1, 30));
				g.drawString("1 Player - Press Shift", width / 2 - 150, height / 2 - 25);
				g.drawString("2 Players - Press Space", width / 2 - 170, height / 2 + 25);
				g.drawString("<< Score Limit: " + scoreLimit + " >>", width / 2 - 145, height / 2 + 75);
				g.drawString("github.com/pedroramos3225", width/2 - 41*width/160, height - height/9);
			}
		}

		if (selectingDifficulty) {
			String string = botDifficulty == 0 ? "Easy" : (botDifficulty == 1 ? "Medium" : "Hard");

			g.setFont(new Font("Arial", 1, 30));

			g.drawString("<< Bot Difficulty: " + string + " >>", width / 2 - 180, height / 2 - 25);
			g.drawString("Press Space to Play", width / 2 - 150, height / 2 + 25);
		}

		if (gameStatus == 1) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));
			g.drawString("PAUSED", width / 2 - 103, height / 2 - 25);
		}

		if (gameStatus == 1 || gameStatus == 2) {
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(5f));
			g.drawLine(width/2, 0, width / 2, height); // Centro
			
			g.setStroke(new BasicStroke(8f));
			g.drawLine(0, height, width, height); // Sup
			g.drawLine(0, 0, width, 0); // Sup
			g.drawLine(0, 0, 0, height); // Left
			g.drawLine(width, 0, width, height); // Right
			
			g.setStroke(new BasicStroke(2f));
			g.drawOval(width/2 - 100, height/2 - 100, 200, 200);
			g.fillOval(width/2 - 10, height/2 - 10, 20, 20);
			g.setFont(new Font("Arial", 1, 50));

			g.drawString(String.valueOf(player1.score), width / 2 - 90, 75);
			g.drawString(String.valueOf(player2.score), width / 2 + 65, 75);

			player1.render(g);
			player2.render(g);
			ball.render(g);
		}

		if (gameStatus == 3) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));

			g.drawString("PONG", width / 2 - 75, height - 8*height/9);

			if (bot && playerWon == 2) {
				g.drawString("The Bot Wins!", width / 2 - 170, height - height/9);
			} else {
				g.drawString("Player " + playerWon + " Wins!", width / 2 - 165, height - height/9);
			}

			g.setFont(new Font("Arial", 1, 30));

			g.drawString("Press Space to Play Again", width / 2 - 185, height / 2 - 25);
			g.drawString("Press ESC for Menu", width / 2 - 140, height / 2 + 25);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (gameStatus == 2) {
			update();
		}
		renderer.repaint();
	}

	public static void main(String[] args) {
		pong = new Pong();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int id = e.getKeyCode();

		// Movements
		if (id == KeyEvent.VK_W) {
			w = true;
		} else if (id == KeyEvent.VK_S) {
			s = true;
		} else if (id == KeyEvent.VK_UP) {
			up = true;
		} else if (id == KeyEvent.VK_DOWN) {
			down = true;
		} else if (id == KeyEvent.VK_RIGHT) {
			if (selectingDifficulty) {
				if (botDifficulty < 2) {
					botDifficulty++;
				} else {
					botDifficulty = 0;
				}
			} else if (gameStatus == 0) {
				scoreLimit++;
			}
		} else if (id == KeyEvent.VK_LEFT) {
			if (selectingDifficulty) {
				if (botDifficulty > 0) {
					botDifficulty--;
				} else {
					botDifficulty = 2;
				}
			} else if (gameStatus == 0 && scoreLimit > 1) {
				scoreLimit--;
			}
		} else if (id == KeyEvent.VK_ESCAPE && (gameStatus == 2 || gameStatus == 3)) {
			gameStatus = 0;
		} else if (id == KeyEvent.VK_SHIFT && gameStatus == 0) {
			bot = true;
			selectingDifficulty = true;
		} else if (id == KeyEvent.VK_SPACE) {
			if (gameStatus == 0 || gameStatus == 3) {
				if (!selectingDifficulty) {
					bot = false;
				} else {
					selectingDifficulty = false;
				}
				start();
			} else if (gameStatus == 1) {
				gameStatus = 2;
			} else if (gameStatus == 2) {
				gameStatus = 1;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int id = e.getKeyCode();

		if (id == KeyEvent.VK_W) {
			w = false;
		} else if (id == KeyEvent.VK_S) {
			s = false;
		} else if (id == KeyEvent.VK_UP) {
			up = false;
		} else if (id == KeyEvent.VK_DOWN) {
			down = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
