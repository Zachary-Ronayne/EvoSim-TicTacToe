package game;

import java.util.ArrayList;
import java.util.Random;

import game.TicTacToe.State;
import game.TicTacToe.Type;
import menu.Main;
import neuralNet.Net;

/**
 * An object that keeps track of a neural net playing tic tac toe and allows for it to evolve
 */
public class NetPlayer{
	
	private Net brain;
	
	private int timesTrained;
	
	/**
	 * True if this AI learns to play as X, the one who goes first, false if this AI learns to play as O
	 */
	private boolean playAsX;
	
	/**
	 * The data for this NEtPlayer that keeps track of the fitness and mutability history
	 */
	private ArrayList<double[]> data;
	
	public NetPlayer(){
		playAsX = true;
		
		brain = new Net(Main.LAYERS);
		timesTrained = 0;
		data = new ArrayList<double[]>();
	}
	
	public void setPlayAsX(boolean x){
		playAsX = x;
	}
	public boolean isPlayingAsX(){
		return playAsX;
	}
	
	/**
	 * Makes copies number of mutated versions of this object's brain, then tests them with times number of games, where each game has randomly determined moves. 
	 * The random seed used to determine the moves for each game is the same across the testing of each brain, meaning if 2 brains make the same moves every time, 
	 * same game will happen. The highest rated brain from this method call will replace the brain of this object. 
	 * This also adds in a test with the smart AI that counts for 50% of the total end value
	 * @param times
	 * @param copies
	 * @param playX true if this test should play as X, false if it should play as O
	 */
	public void train(int times, int copies){
		//generate the new brains, the current brain of this object is index 0
		Net[] newBrains = new Net[copies + 1];
		for(int i = 1; i < newBrains.length; i++) newBrains[i] = brain.getMutatedNet();
		newBrains[0] = brain;
		
		//create a list to store all the values of the test results from each brain
		double[] values = new double[copies + 1];
		for(int i = 0; i < values.length; i++) values[i] = 0;
		
		//create a list of all the seeds for testing
		int[] seeds = new int[times];
		for(int i = 0; i < seeds.length; i++) seeds[i] = (int)(Math.random() * Integer.MAX_VALUE);
		
		//test every new brain
		for(int i = 0; i < newBrains.length; i++){
			for(int j = 0; j < seeds.length; j++){
				Random rand = new Random(seeds[j]);
				TicTacToe testGame = new TicTacToe();
				if(!playAsX) testGame.makeRandomMove(rand);
				while(testGame.getState() == State.PLAY){
					makeMove(testGame, newBrains[i]);
					testGame.makeRandomMove(rand);
				}
				//add 1 if the Brain won, add -1 if they lost, do nothing if they tied
				if(testGame.getState() == State.X_WIN){
					if(playAsX) values[i]++;
					else values[i]--;
				}
				else if(testGame.getState() == State.O_WIN){
					if(playAsX) values[i]--;
					else values[i]++;
				}
			}
		}
		
		//test the brains with the smart AI, uses the first seed in the list
		for(int i = 0; i < newBrains.length; i++){
			for(int j = 0; j < seeds.length; j++){
				Random rand = new Random(seeds[j]);
				TicTacToe testGame = new TicTacToe();
				if(!playAsX) testGame.makeSmartMove(rand);
				while(testGame.getState() == State.PLAY){
					makeMove(testGame, newBrains[i]);
					testGame.makeSmartMove(rand);
				}
				//add 1 if the Brain won, add -1 if they lost, do nothing if they tied
				if(testGame.getState() == State.X_WIN){
					if(playAsX) values[i]++;
					else values[i]--;
				}
				else if(testGame.getState() == State.O_WIN){
					if(playAsX) values[i]--;
					else values[i]++;
				}
			}
			
		}
		
		//find the averages for values based on the number of games to play
		for(int i = 0; i < values.length; i++) values[i] /= seeds.length * 2;
		
		//find the brain with the the highest value and set that brain to the brain of this object
		int high = -1;
		for(int i = 0; i < newBrains.length; i++){
			if(high == -1 || values[i] > values[high]) high = i;
		}
		brain = newBrains[high];
		
		data.add(new double[]{values[high], brain.getMutability()});
		
		timesTrained++;
	}
	
	/**
	 * Calculate the inputs for the given based on the given game
	 * @param game
	 * @param brain
	 */
	public void sendBrainInputs(TicTacToe game, Net brain){
		double[] inputs = new double[9];
		for(int k = 0; k < inputs.length; k++){
			if(game.getBoard()[k % 3][k / 3] == TicTacToe.Type.X) inputs[k] = 1;
			else if(game.getBoard()[k % 3][k / 3] == TicTacToe.Type.O) inputs[k] = -1;
			else inputs[k] = 0;
		}
		brain.feedInputs(inputs);
		brain.calculateValues();
	}
	
	/**
	 * Make the given brain make a decision as to what to play based on the given board
	 * @param game
	 * @param brain
	 */
	public void makeMove(TicTacToe game, Net brain){
		sendBrainInputs(game, brain);
		
		//get the brain outputs
		double[] out = brain.getOutputs();
		
		//make list of indexes to try to play at
		int[] index = new int[out.length];
		for(int i = 0; i < index.length; i++) index[i] = i;
		
		//sort the list so that the indexes are in descending order of the output values of the brain
		for(int i = 0; i < index.length; i++){
			int high = -1;
			for(int j = i; j < index.length; j++){
				if(high == -1 || out[high] < out[j]) high = j;
			}
			int temp = index[i];
			index[i] = index[high];
			index[high] = temp;
		}
		
		//play first move that can be played with the most preferred index
		Type[][] b = game.getBoard();
		for(int i = 0; i < index.length; i++){
			int useI = index[i];
			if(b[useI % 3][useI / 3] == Type.EMPTY){
				game.makeMove(useI % 3, useI / 3);
				break;
			}
		}
	}
	
	/**
	 * Get the current brain of this NetPlayer
	 * @return
	 */
	public Net getBrain(){
		return brain;
	}
	
	/**
	 * Get the number of times this brain has been trained
	 * @return
	 */
	public int timesTrained(){
		return timesTrained;
	}
	
	public ArrayList<double[]> getData(){
		return data;
	}
	
}
