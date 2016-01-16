import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.*;


/* Henrys TODO
 * 
 * pick best direction in Enemy.move instead of just the first one we find
 * 
 * make the other enemy move differently
 * better board
 * pacman shouldn't be able to go in the enemy gate
 * draw something for DESTINATION_DOT
 * pacman should face the direction he's going
 * pacman's mouth should not animate if he's not moving
 * sounds
 * multiple levels
 * 
 * eating a Destination Dot should turn you into an enemy-eating machine
 * 
 */

public class PacmanGame extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

	int mouse_x;
	int mouse_y;
	int Pmouse_x;
	int Pmouse_y;
	
	static int PAUSE_BUTTON_X=565;
	static int PAUSE_BUTTON_Y=5;
	static int PAUSE_SIZE=30;
	
	boolean is_first_draw = true;
	
	boolean mouse_is_down = false;
	
	boolean draw_spiral_next_time = false;
	static int SQUARE_SIZE = 40;
	static int PACMAN_SPEED = 8;
	static int ENEMY_SPEED = 5;
	static int score = 0;
	long timeSinceRestart;
	
	Pacman pacman;
	
	Organism enemy1;
	Organism enemy2;
	Organism enemy3;
	// Organism[] enemies;
	
	int pendingKeyCode = -1; // no pending key at the beginning
	
	boolean paused = false;
	
	enum Things {
		CHERRY,
		DOT,
		WALL,
		DESTINATION_DOT,
		ENEMY_SPAWNING_PLACE,
		EMPTY,
		ENEMY_SPAWNING_GATE,
	}
	
	static Things original_board[][];		
	static Things[][] board;
	public class MyRunnable implements Runnable {

        public void run() {
        	while (true) {
        		try {
        			Thread.sleep(1000/60);
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
        		repaint();
        	}
        }
    }

    MyRunnable repaint_thread = new MyRunnable();
    
    public PacmanGame() {
    	restart();
    }

    public void restart(){
    	pacman = new Pacman(7*SQUARE_SIZE, 6*SQUARE_SIZE);
    	enemy1 = new Enemy(-1, -1, pacman);
    	enemy2 = new Enemy(-1, -1, pacman);
    	enemy3 = new Enemy(-1, -1, pacman);
    	score = 0;
    	pendingKeyCode = -1;
    	timeSinceRestart = System.currentTimeMillis();
    	System.out.println("Restart -- current time is " + timeSinceRestart);
    	
    	board = original_board.clone();
    	for (int i = 0; i < board.length; i++) {
    		board[i] = original_board[i].clone();
    	}
    }
    
    public static void loadBoard(String level) {
    	InputStream    fis;
    	BufferedReader br;
    	String         line;
    	
    	Map<Character, Things> decode_map = new HashMap<Character, Things>();
    	decode_map.put('#', Things.WALL);
    	decode_map.put(' ', Things.EMPTY);
    	decode_map.put('C', Things.CHERRY);
    	decode_map.put('.', Things.DOT);
    	decode_map.put('-', Things.ENEMY_SPAWNING_PLACE);
    	decode_map.put('=', Things.ENEMY_SPAWNING_GATE);
    	decode_map.put('D', Things.DESTINATION_DOT);

    	try
    	{
	    	fis = new FileInputStream(level);
	    	br = new BufferedReader(new InputStreamReader(fis));
	    	
	    	int num_rows = -1;
	    	int num_cols = -1;
	    	int i = 0;
	    	while ((line = br.readLine()) != null) {
	    	    if (num_rows < 0) {
	    	    	num_rows = Integer.parseInt(line);
	    	    	System.out.println("board has " + num_rows + " rows.");
	    	    }
	    	    else if (num_cols < 0) {
	    	    	num_cols = Integer.parseInt(line);
	    	    	System.out.println("board has " + num_cols + " columns.");
	    	    	original_board = new Things[num_rows][num_cols];
	    	    } else {
	    	    	for (int j = 0; j < line.length(); j++) {
	    	    		original_board[i][j] = decode_map.get(line.charAt(j));
	    	    	}
	    	    	i++;
	    	    }
	    	}
	    	br.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}

    	// Done with the file
    	br = null;
    	fis = null;
    }
    
	public static void main(String[] args) { 
		
		String level = "level.txt";
		if(args.length != 0) {
			level = args[0];
			System.out.print("Using specified level: ");
			System.out.println(level);
		}
		else {
			
			System.out.print("Using default level: ");
			System.out.println(level);
		}
		
		loadBoard(level);
		
		JFrame frame = new JFrame();
		frame.setSize(original_board[0].length * SQUARE_SIZE, original_board.length * SQUARE_SIZE+20);
		
		
		// frame.add(new JButton("Click me"));
		PacmanGame m = new PacmanGame();
		m.addMouseMotionListener(m);
		m.addMouseListener(m);
		frame.addKeyListener(m);
		frame.add(m);	
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		try {
		    File yourFile;
		    AudioInputStream stream;
		    AudioFormat format;
		    DataLine.Info info;
		    Clip clip;

		    stream = AudioSystem.getAudioInputStream(new File("pacman_beginning.wav"));
		    format = stream.getFormat();
		    info = new DataLine.Info(Clip.class, format);
		    clip = (Clip) AudioSystem.getLine(info);
		    clip.open(stream);
		    clip.start();
		}
		catch (Exception e) {
		    //whatevers
		}
		JLabel myOutput = new JLabel("Score:"+score);
		frame.add(myOutput);
		myOutput.setForeground(Color.white);
		
		new Thread(m.repaint_thread).run();
	}
	
	public void mouseMovedOrDragged(MouseEvent e)
	{
	     mouse_x = e.getX();
	     mouse_y = e.getY();
	}
	
	public void mouseMoved(MouseEvent e) {
		mouseMovedOrDragged(e);
	  }

	public void mouseDragged(MouseEvent e) {
		mouseMovedOrDragged(e);
	}
	
	@Override
	public void paintComponent(Graphics oldg)
	{
		//System.out.println("paintComponent, paused = " + paused);
		Graphics2D g = (Graphics2D)oldg;
		if (is_first_draw)
		{
			setup();
			
			is_first_draw = false;
		}
		else 
		{
			if (!paused)
			{
				// switch pacman direction
				if (pendingKeyCode != -1) {
					tryToSwitchDirection();
				}
				
				pacman.move(board, PACMAN_SPEED);
				boolean isGameOver = true;
				for (int a = 0; a < board.length; a++) {
					for (int b = 0; b < board[a].length; b++) {
						if (board[a][b] == PacmanGame.Things.DOT) {
							isGameOver = false;
						}
					}
				}
				if (isGameOver==true){
					restart();
				}
				// if enemy doesn't exist, spawn it at the spawning place
				if (enemy1.x < 0) {
					for (int i = 0; i < board.length; i++) {
						for (int j = 0; j < board[i].length; j++) {
							if (board[i][j] == Things.ENEMY_SPAWNING_PLACE) {
								enemy1.x = j * SQUARE_SIZE;
								enemy1.y = i * SQUARE_SIZE;
								enemy1.dir = Organism.Direction.UP;
							}
						}
					}
				} 
				else {
					enemy1.move(board, ENEMY_SPEED);
				}
				if ((pacman.x==enemy1.x && Math.abs(pacman.y-enemy1.y)<SQUARE_SIZE) || 	
					(pacman.y==enemy1.y && Math.abs(pacman.x-enemy1.x)<SQUARE_SIZE)){
					restart();
				}
				
				if (System.currentTimeMillis() - timeSinceRestart > 5000) {
					if (enemy2.x < 0) {
						System.out.println("Spawning enemy 2 -- time is " + System.currentTimeMillis());
						for (int i = 0; i < board.length; i++) {
							for (int j = 0; j < board[i].length; j++) {
								if (board[i][j] == Things.ENEMY_SPAWNING_PLACE) {
									enemy2.x = j * SQUARE_SIZE;
									enemy2.y = i * SQUARE_SIZE;
									enemy2.dir = Organism.Direction.UP;
								}
							}
						}
					} 
					else {
						enemy2.move(board, ENEMY_SPEED);
					}
					if ((pacman.x==enemy2.x && Math.abs(pacman.y-enemy2.y)<SQUARE_SIZE) || 	
						(pacman.y==enemy2.y && Math.abs(pacman.x-enemy2.x)<SQUARE_SIZE)){
						restart();
					}

				}

				if (System.currentTimeMillis() - timeSinceRestart > 10000) {
					if (enemy3.x < 0) {
						System.out.println("Spawning enemy 3 -- time is " + System.currentTimeMillis());
						for (int i = 0; i < board.length; i++) {
							for (int j = 0; j < board[i].length; j++) {
								if (board[i][j] == Things.ENEMY_SPAWNING_PLACE) {
									enemy3.x = j * SQUARE_SIZE;
									enemy3.y = i * SQUARE_SIZE;
									enemy3.dir = Organism.Direction.UP;
								}
							}
						}
					} 
					else {
						enemy3.move(board, ENEMY_SPEED);
					}
					if ((pacman.x==enemy3.x && Math.abs(pacman.y-enemy3.y)<SQUARE_SIZE) || 	
						(pacman.y==enemy3.y && Math.abs(pacman.x-enemy3.x)<SQUARE_SIZE)){
						restart();
					}

				}

			}
		}
		
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0,  0, 2000, 2000);
		
		// Draw the board things
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == Things.WALL) {
					g.setColor(Color.black);
					g.fillRect(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
				} else if (board[i][j] == Things.DOT) {
					g.setColor(new Color(255, 200, 200));
					g.fillOval(j * SQUARE_SIZE+5, i * SQUARE_SIZE+5, SQUARE_SIZE-10, SQUARE_SIZE-10);
				}else if (board[i][j] == Things.CHERRY) {
					g.setColor(new Color(255,0,0));
					g.fillOval(j * SQUARE_SIZE+5, i * SQUARE_SIZE+5, SQUARE_SIZE-10, SQUARE_SIZE-10);
				}
				else if (board[i][j]== Things.ENEMY_SPAWNING_GATE){
					g.setColor(Color.black);
					g.fillRect(j * SQUARE_SIZE, i * SQUARE_SIZE+10, SQUARE_SIZE, SQUARE_SIZE-20);
				}
			}
		}
		
		g.setColor(Color.WHITE);
		g.drawString("Score:"+score, 10, 10);
		
		pacman.drawPacman(g);
		// Draw enemy if it is on the board
		if (enemy1.x >= 0) {
			g.setColor(new Color(200, 100, 200));
			g.fillRect(enemy1.x, enemy1.y, SQUARE_SIZE, SQUARE_SIZE);
		}
		
		if (enemy2.x >= 0) {
			g.setColor(new Color(100, 200, 100));
			g.fillRect(enemy2.x, enemy2.y, SQUARE_SIZE, SQUARE_SIZE);
		}

		if (enemy3.x >= 0) {
			g.setColor(new Color(200, 200, 100));
			g.fillRect(enemy3.x, enemy3.y, SQUARE_SIZE, SQUARE_SIZE);
		}
		
		// Draw the pause button
		g.setColor(new Color(255, 0, 0));
		g.fillRect(PAUSE_BUTTON_X, PAUSE_BUTTON_Y, PAUSE_SIZE, PAUSE_SIZE);
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// TODO Auto-generated method stub
		if (event.getButton() == 1) {
			mouse_is_down = true;
			Pmouse_x = mouse_x;
			Pmouse_y = mouse_y;
		} else {
			draw_spiral_next_time = true;
		}
		
		mouseClicked();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mouse_is_down = false;
	}

	
	
	//ArrayList<dot> dotArray = new ArrayList();
	public void setup()
	{
	  for(int i = 0; i < 16; i ++)
	  {
	   
	   //dotArray.add(new dot(30*i+30, 30)); 
	    
	  }
	}
	

	public void mouseClicked(){
	  if(mouse_x>PAUSE_BUTTON_X && mouse_x <PAUSE_BUTTON_X+PAUSE_SIZE &&mouse_y>PAUSE_BUTTON_Y &&mouse_y<PAUSE_BUTTON_Y+PAUSE_SIZE) 
	  {
			//System.out.println("toggling pause state");
	    paused=!paused;
	  } 
	}
	

	public void tryToSwitchDirection()
	{
		if(pendingKeyCode == KeyEvent.VK_UP && (pacman.x % SQUARE_SIZE == 0) && !Organism.isWallAtPoint(board, pacman.x, pacman.y-SQUARE_SIZE))
		{
			pacman.dir = Organism.Direction.UP;
			pendingKeyCode = -1;
			
		}
		if(pendingKeyCode == KeyEvent.VK_DOWN && (pacman.x % SQUARE_SIZE == 0) && !Organism.isWallAtPoint(board, pacman.x, pacman.y+SQUARE_SIZE))
		{
			pacman.dir = Organism.Direction.DOWN;
			pendingKeyCode = -1;
		}
		if(pendingKeyCode == KeyEvent.VK_LEFT && (pacman.y % SQUARE_SIZE == 0) && !Organism.isWallAtPoint(board, pacman.x-SQUARE_SIZE, pacman.y))
		{
			pacman.dir = Organism.Direction.LEFT;
			pendingKeyCode = -1;
		}
		if(pendingKeyCode == KeyEvent.VK_RIGHT && (pacman.y % SQUARE_SIZE == 0) && !Organism.isWallAtPoint(board, pacman.x+SQUARE_SIZE, pacman.y))
		{
			pacman.dir = Organism.Direction.RIGHT;
			pendingKeyCode = -1;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		pendingKeyCode = key.getKeyCode();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}