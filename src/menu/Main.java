package menu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import game.NetPlayer;
import game.TicTacToe;
import neuralNet.Node;

public class Main{
	
	/*
	 * TODO
	 * let the AI learn how to play as both X and O depending on which they are training as
	 */
	
	//settings
	/**
	 * The number of games to play when the brain trains
	 */
	public static final int TEST_TIMES = 1000;
	/**
	 * The number of mutated brains to make when training
	 */
	public static final int TEST_OFFSPRING = 100;
	
	/**
	 * The nodes in each layer of the neural net, there needs to be at least 2 numbers, the first and last must both be 9
	 */
	public static final int[] LAYERS = new int[]{9, 9, 9};
	
	public static final String FONT = "Ariel";
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 1000;
	
	/**
	 * The main variable keeping track of this simulation
	 */
	private static Main instance;
	
	/**
	 * true if looping training
	 */
	private boolean loopingTrain;
	
	/**
	 * The main frame to display the simulation
	 */
	private JFrame frame;
	/**
	 * The image that keeps track of the graphics that should be drawn to the frame
	 */
	private BufferedImage screen;
	/**
	 * True if the loop for the simulation is running
	 */
	private boolean running;
	
	/**
	 * The game currently being displayed
	 */
	private TicTacToe game;
	
	/**
	 * The brain that is playing the game
	 */
	private NetPlayer brain;
	
	/**
	 * the object that keeps track of the frame displaying graphs
	 */
	private GraphDisplay graphFrame;
	
	public Main(){
		graphFrame = new GraphDisplay();
		graphFrame.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				super.keyPressed(e);
				keyUsed(e);
			}
		});
		
		running = true;
		
		loopingTrain = false;
		
		brain = new NetPlayer(){
			@Override
			public void train(int times, int copies){
				super.train(times, copies);
				graphFrame.addGraphData(getData());
			}
		};
		
		game = new TicTacToe(){
			@Override
			public void reset(){
				super.reset();
				if(brain != null && game != null) brain.sendBrainInputs(game, brain.getBrain());
			}
		};
		
		screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		frame = getFrame();
		
		brain.sendBrainInputs(game, brain.getBrain());
		
		render();
	}
	
	/**
	 * Called when every a key is pressed down
	 * @param e
	 */
	public void keyUsed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_Q) game.makeMove(0, 0);
		else if(e.getKeyCode() == KeyEvent.VK_W) game.makeMove(0, 1);
		else if(e.getKeyCode() == KeyEvent.VK_E) game.makeMove(0, 2);
		else if(e.getKeyCode() == KeyEvent.VK_A) game.makeMove(1, 0);
		else if(e.getKeyCode() == KeyEvent.VK_S) game.makeMove(1, 1);
		else if(e.getKeyCode() == KeyEvent.VK_D) game.makeMove(1, 2);
		else if(e.getKeyCode() == KeyEvent.VK_Z) game.makeMove(2, 0);
		else if(e.getKeyCode() == KeyEvent.VK_X) game.makeMove(2, 1);
		else if(e.getKeyCode() == KeyEvent.VK_C) game.makeMove(2, 2);
		else if(e.getKeyCode() == KeyEvent.VK_R) game.makeSmartMove();
		else if(e.getKeyCode() == KeyEvent.VK_G) graphFrame.toggleOn();
		else if(e.getKeyCode() == KeyEvent.VK_P) brain.setPlayAsX(!brain.isPlayingAsX());
		else if(e.getKeyCode() == KeyEvent.VK_SPACE) game.reset();
		else if(e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_2 || e.getKeyCode() == KeyEvent.VK_3){
			System.out.println("Statred Training");
			int n = 1;
			if(e.isShiftDown()) n = 10;
			if(e.isControlDown()) n = 100;
			if(e.isAltDown()) n = 1000;
			for(int i = 0; i < n; i++){
				if(e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_3) brain.train(TEST_TIMES, TEST_OFFSPRING);
				if(e.getKeyCode() == KeyEvent.VK_2 || e.getKeyCode() == KeyEvent.VK_3) brain.train(TEST_TIMES, TEST_OFFSPRING);
			}
			System.out.println("Ended Training");
		}
		else if(e.getKeyCode() == KeyEvent.VK_4){
			if(!loopingTrain){
				loopingTrain = true;
				brain.setPlayAsX(true);
			}
			else loopingTrain = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_5){
			if(!loopingTrain){
				loopingTrain = true;
				brain.setPlayAsX(false);
			}
			else loopingTrain = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER) brain.makeMove(game, brain.getBrain());
		else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) game.makeRandomMove(new Random());

		brain.sendBrainInputs(game, brain.getBrain());
		render();
	}
	
	/**
	 * Draws the current state of the simulator to the buffered image
	 */
	public void drawImage(){
		//get the graphics
		Graphics2D g = (Graphics2D)(screen.getGraphics());
		
		//background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//draw the game
		int x = 10, y = 30;
		game.render(g, x, y);
		
		//draw the neural net
		g.setStroke(new BasicStroke(2f));
		Node[][] n = brain.getBrain().getNodes();
		int layerX = 400;
		int nodeY = 50;
		int nodeSize = 40;
		int nodeXSpace = 120;
		int nodeYSpace = 50;
		//draw the lines from the board to the hidden nodes
		for(int i = 0; i < n[0].length; i++){
			int tileX = (int)(x + (i % 3 + .5) * TicTacToe.SIZE);
			int tileY = (int)(y + (i / 3 + .5) * TicTacToe.SIZE);
			
			for(int j = 0; j < n[1].length; j++){
				double v = n[1][j].getWeight(i);
				double fade = 255.0 * Math.abs(v);
				
				if(v < 0) g.setColor(new Color(255, 0, 0, (int)fade));
				else g.setColor(new Color(0, 0, 255, (int)fade));
				
				g.drawLine(tileX, tileY, layerX, nodeY + j * nodeYSpace);
			}
		}
		//draw all the other lines between layers
		for(int i = 1; i < n.length - 1; i++){
			for(int j = 0; j < n[i + 1].length; j++){
				for(int h = 0; h < n[i].length; h++){
					double v = n[i + 1][j].getWeight(h);
					double fade = 255.0 * Math.abs(v);
					
					if(v < 0) g.setColor(new Color(255, 0, 0, (int)fade));
					else g.setColor(new Color(0, 0, 255, (int)fade));
					
					g.drawLine(layerX + nodeXSpace * i, nodeY + j * nodeYSpace,
							   layerX + nodeXSpace * (i - 1), nodeY + h * nodeYSpace);
				}
			}
		}
		//draw the nodes of each hidden layer and output layer
		int nx  = 0;
		int ny = 0;
		for(int i = 1; i < n.length; i++){
			for(int j = 0; j < n[i].length; j++){
				g.setColor(Color.BLACK);
				nx = layerX - nodeSize / 2 + nodeXSpace * (i - 1);
				ny =  nodeY - nodeSize / 2 + nodeYSpace * j;
				g.fillOval(nx, ny, nodeSize, nodeSize);
				
				double v = n[i][j].getValue();
				double fade = 255.0 * (1 - Math.abs(v));
				
				if(v < 0) g.setColor(new Color(255, (int)fade, (int)fade));
				else g.setColor(new Color((int)fade, (int)fade, 255));
				
				g.fillOval(nx + 2, ny + 2, nodeSize - 4, nodeSize - 4);
				
				g.setColor(Color.BLACK);
				g.setFont(new Font(FONT, Font.PLAIN, (int)(nodeSize * .5)));
				g.drawString((int)(v * 1000) + " ", nx + 4, (int)(ny + nodeSize * .7));
			}
		}
		//draw the number of times it has been trained
		g.setColor(Color.BLACK);
		g.setFont(new Font(FONT, Font.PLAIN, 20));
		g.drawString("Trained " + brain.timesTrained() + " times", x, ny + nodeSize);
		
		//render instructions
		g.setColor(Color.BLACK);
		g.setFont(new Font(FONT, Font.PLAIN, 20));
		x = TicTacToe.SIZE * 3 + 400;
		y = 50;
		g.drawString("Space: reset game", x, y += 25);
		g.drawString("G: show/hide graph", x, y += 25);
		g.drawString("P: toggle AI training on X or O", x, y += 25);
		if(brain.isPlayingAsX()) g.drawString("Training as X", x, y += 25);
		else g.drawString("Training as O", x, y += 25);
		g.drawString("1: Train the AI with 1 set of games as X", x, y += 25);
		g.drawString("2: Train the AI with 1 set of games as O", x, y += 25);
		g.drawString("3: Train the AI with 1 set of games as X and 1 as O", x, y += 25);
		g.drawString("1, 2, or 3 + shift: train 10 sets instead of 1", x, y += 25);
		g.drawString("1, 2, or 3 + ctrl: train 100 sets instead of 1", x, y += 25);
		g.drawString("1, 2, or 3 + alt: train 1000 sets instead of 1", x, y += 25);
		g.drawString("4: Keep training as X until 4 is pressed again", x, y += 25);
		g.drawString("5: Keep training as O until 5 is pressed again", x, y += 25);
		g.drawString("Enter: make the brain make a move", x, y += 25);
		g.drawString("Backspace: make a random move", x, y += 25);
		g.drawString("R: Make a smart move", x, y += 25);
		g.drawString("Play a move: press coressponding key", x, y += 25);
		g.drawString("Q W E", x, y += 25);
		g.drawString("A S D", x, y += 25);
		g.drawString("Z X C", x, y += 25);
		
		//draw loop state
		g.drawString("Currently:", x, y += 25);
		if(loopingTrain){
			if(brain.isPlayingAsX()) g.drawString("Looping X", x, y += 25);
			else g.drawString("Looping O", x, y += 25);
		}
		else{
			g.drawString("Not looping", x, y += 25);
		}
	}
	
	/**
	 * Updates the frame to the current state of the simulation
	 */
	public void render(){
		frame.repaint();
		graphFrame.reDraw();
	}
	
	/**
	 * @return a new instance of the frame for this simulation
	 */
	public JFrame getFrame(){
		JFrame f = new JFrame("Tic Tac Toe Neural Net"){
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g){
				drawImage();
				g.drawImage(screen, 0, 0, null);
			}
		};
		f.setVisible(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(WIDTH, HEIGHT);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setVisible(true);
		
		KeyAdapter keyInput = new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				super.keyPressed(e);
				keyUsed(e);
			}
		};
		f.addKeyListener(keyInput);
		
		return f;
	}
	
	/**
	 * start a loop that updates the frame a few times a second
	 */
	private void start(){
		while(running){
			try{
				if(loopingTrain){
					brain.train(TEST_TIMES, TEST_OFFSPRING);
					render();
				}
				else{
					Thread.sleep(300);
				}
			}catch(InterruptedException e){}
		}
	}
	
	public static void main(String[] args){
		instance = new Main();
		instance.start();
	}
}
