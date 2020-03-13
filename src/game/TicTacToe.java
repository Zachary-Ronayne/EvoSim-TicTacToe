package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import menu.Main;

public class TicTacToe{
	
	/**
	 * The width of a single tile to be rendered for the game board
	 */
	public static final int SIZE = 100;
	
	public enum Type{
		X, O, EMPTY;
	}
	
	public enum State{
		PLAY, X_WIN, O_WIN, DRAW;
	}
	
	private Type[][] board;
	
	private boolean xTurn;
	
	private State state;
	
	public TicTacToe(){
		reset();
	}
	
	/**
	 * Bring the game back to the default state
	 */
	public void reset(){
		xTurn = true;
		state = State.PLAY;
		board = new Type[3][3];
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[i].length; j++){
				board[i][j] = Type.EMPTY;
			}
		}
	}
	
	/**
	 * Draws the board of this game to the given graphics object at the given coordinates
	 * @param g
	 * @param x
	 * @param y
	 */
	public void render(Graphics2D g, int x, int y){
		g.setColor(Color.BLACK);
		int w = SIZE * board.length + 1;
		g.fillRect(x - 1 , y - 1, w, w);
		for(int j = 0; j < board.length; j++){
			for(int i = 0; i < board[j].length; i++){
				g.setColor(Color.WHITE);
				int xx = x + j * SIZE;
				int yy = y + i * SIZE;
				Type t = board[i][j];
				g.fillRect(xx, yy, SIZE - 1, SIZE - 1);
				if(t != Type.EMPTY){
					g.setColor(Color.BLACK);
					g.setFont(new Font(Main.FONT, Font.PLAIN, (int)(SIZE * .9)));
					if(t == Type.X) g.drawString("X", xx + 10, yy + (int)(SIZE * .95));
					if(t == Type.O) g.drawString("O", xx + 10, yy + (int)(SIZE * .95));
				}
			}
		}
		if(state != State.PLAY){
			g.setColor(new Color(255, 255, 255, 127));
			g.fillRect(x - 1, y - 1, w, w);
		}
		
		g.setColor(Color.BLACK);
		g.setFont(new Font(Main.FONT, Font.PLAIN, 40));
		
		String s;
		if(state == State.X_WIN) s = "x wins";
		else if(state == State.O_WIN) s = "o wins";
		else if(state == State.PLAY){
			if(xTurn) s = "x turn";
			else s = "o turn";
		}
		else s = "draw";
		
		g.drawString(s, x + 10, y + w + 45);
	}
	
	/**
	 * Attempt to make a move at the specified position. If the move succeeds, the state of the game is reevaluated, and someone may win or the turn may change
	 * @param i the row
	 * @param j the column
	 */
	public void makeMove(int i, int j){
		if(state != State.PLAY) return;
		if(board[i][j] == Type.EMPTY){
			if(xTurn) board[i][j] = Type.X;
			else board[i][j] = Type.O;
			xTurn = !xTurn;
			calculateState();
		}
	}
	
	/**
	 * Determines if the game is over or if the game should continue playing
	 */
	public void calculateState(){
		if(winner(Type.X)) state = State.X_WIN;
		else if(winner(Type.O)) state = State.O_WIN;
		else{
			boolean full = true;
			for(int i = 0; i < board.length && full; i++){
				for(int j = 0; j < board[i].length && full; j++){
					full = board[i][j] != Type.EMPTY;
				}
			}
			if(full) state = State.DRAW;
			else state = State.PLAY;
		}
	}
	
	/**
	 * @param t
	 * @return true if the given type has a winning 3 in a row/column/diag
	 */
	public boolean winner(Type t){
		boolean found;
		for(int i = 0; i < board.length; i++){
			found = true;
			for(int j = 0; j < board[i].length && found; j++) found = board[i][j] == t;
			if(found) return true;
			found = true;
			for(int j = 0; j < board[i].length && found; j++) found = board[j][i] == t;
			if(found) return true;
		}

		found = true;
		for(int i = 0; i < board.length && found; i++) found = board[i][i] == t;
		if(found) return true;
		found = true;
		for(int i = 0; i < board.length && found; i++) found = board[i][board.length - 1 - i] == t;
		if(found) return true;
		
		return false;
	}
	
	/**
	 * Randomly picks a valid index to play at, and makes a move there in the given game. The move is based on the given Random object. 
	 * No move is made if no valid moves are found
	 * @param game
	 * @param rand the seed for the move
	 */
	public void makeRandomMove(Random rand){
		Type[][] b = getBoard();
		ArrayList<Point> valid = new ArrayList<Point>();
		for(int i = 0; i < b.length; i++){
			for(int j = 0; j < b[i].length; j++){
				if(b[i][j] == Type.EMPTY) valid.add(new Point(i, j));
			}
		}
		if(valid.size() == 0) return;
		
		Point randIndex = valid.get((int)(rand.nextDouble() * valid.size()));
		makeMove(randIndex.x, randIndex.y);
	}

	/**
	 * Makes a move for the current player. If there is a way for them to win in this move, then that move is made, 
	 * if there is not a way for the player to win, then it tries to find a move to block the opponent, 
	 * if there is no blocking move a random move is played
	 */
	public void makeSmartMove(){
		makeSmartMove(new Random());
	}
	
	/**
	 * Makes a move for the current player. If there is a way for them to win in this move, then that move is made, 
	 * if there is not a way for the player to win, then it tries to find a move to block the opponent, 
	 * if there is no blocking move a random move is played
	 * @param r the Random object for picking a random move if applicable
	 * 
	 */
	public void makeSmartMove(Random r){
		//first see if the current player can make a winning move
		Point move = findWinningMove(xTurn);
		if(move != null){
			makeMove(move.x, move.y);
			return;
		}
		
		//if the can't make a winning move, see if they can block the opponent
		move = findWinningMove(!xTurn);
		if(move != null){
			makeMove(move.x, move.y);
			return;
		}
		
		//otherwise just pick a random move
		makeRandomMove(r);
	}
	
	/**
	 * @param turn
	 * @return the point on the board where the first winning move can be made for the given turn. Returns null if no valid move is found
	 */
	public Point findWinningMove(boolean turn){
		int playI;
		int empty;
		for(int i = 0; i < board.length; i++){
			//check columns for being able to win
			empty = 0;
			playI = -1;
			for(int j = 0; j < board[i].length && empty <= 1; j++){
				Type b = board[i][j];
				if(b == Type.EMPTY){
					empty++;
					playI = j;
				}
				else if(!isThisTurn(b, turn)) empty = 2;
			}
			if(playI != -1 && empty == 1){
				return new Point(i, playI);
			}
			
			//check rows for being able to win
			empty = 0;
			playI = -1;
			for(int j = 0; j < board[i].length && empty <= 1; j++){
				Type b = board[j][i];
				if(b == Type.EMPTY){
					empty++;
					playI = j;
				}
				else if(!isThisTurn(b, turn)) empty = 2;
			}
			if(playI != -1 && empty == 1){
				return new Point(playI, i);
			}
		}
		
		//check the upper left to lower right diagonal
		empty = 0;
		playI = -1;
		for(int j = 0; j < board[0].length && empty <= 1; j++){
			Type b = board[j][j];
			if(b == Type.EMPTY){
				empty++;
				playI = j;
			}
			else if(!isThisTurn(b, turn)) empty = 2;
		}
		if(playI != -1 && empty == 1){
			return new Point(playI, playI);
		}
		
		//check the lower left to upper right diagonal
		empty = 0;
		playI = -1;
		for(int j = 0; j < board[0].length && empty <= 1; j++){
			Type b = board[j][board.length - 1 - j];
			if(b == Type.EMPTY){
				empty++;
				playI = j;
			}
			else if(!isThisTurn(b, turn)) empty = 2;
		}
		if(playI != -1 && empty == 1){
			return new Point(playI, board.length - 1 - playI);
		}
		
		return null;
	}
	
	/**
	 * @return the board of this game
	 */
	public Type[][] getBoard(){
		return board;
	}
	
	public State getState(){
		return state;
	}
	
	/**
	 * @param t
	 * @param turn true for X, false for O
	 * @return true if the given type is of the same turn given
	 */
	public boolean isThisTurn(Type t, boolean turn){
		return turn && t == Type.X || !turn && t == Type.O;
	}
	
}
