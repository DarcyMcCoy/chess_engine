package packageForChessEngine;

import java.util.Scanner;

/**
 * <code>abstract</code> class for any type of chess game. 
 * To play a game, this class must be extended and the <code>play()</code> method be implemented.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public abstract class Game {
	/** The current position in this game. */
	private Position currentPosition;
	/** The moves that have previously been made in this game. */
	private int[] movesMade;
	/** Whether this game is currently being played. */
	protected boolean inGame;
	/** <code>Scanner</code> to get user input. */
	private static Scanner scanner = new Scanner(System.in);
	/** The chess engine which can play against a user or against itself. */
	private static Engine engine = new Engine();

	/**
	 * Default constructor (Standard starting position, 
	 * no moves have been made and the game hasn't been started.
	 */
	public Game() {
		this(new Position(), new int[0], false);
	}

	/**
	 * Parameterized constructor to set the current position, the moves that have been made and whether 
	 * the game is currently.
	 * 
	 * @param currentPosition the <code>Position</code> that the game will start from
	 * @param movesMade integer array for the moves that have already been made
	 * @param inGame <code>boolean</code> whether the game is currently being played
	 */
	public Game(Position currentPosition, int[] movesMade, boolean inGame) {
		this.currentPosition = currentPosition;
		this.movesMade = movesMade;
		this.inGame = inGame;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param otherGame the <code>Game</code> to be copied.
	 */
	public Game(Game otherGame) {
		this(otherGame.currentPosition, otherGame.movesMade, otherGame.inGame);
	}
	
	/**
	 * Starts and plays whichever game is extending this class.
	 */
	public abstract void play();

	/**
	 * Sets inGame to true.
	 */
	public void startGame() {
		inGame = true;
	}

	/**
	 * Sets inGame to false.
	 */
	public void stopGame() {
		inGame = false;
	}
	
	/**
	 * Finds the best move according to the engine and 
	 * makes that move on the current position.
	 * If there are no legal moves then the game is ended.
	 */
	public void letEngineMakeMove() {
		try {
			int engineMove = engine.findTopMoveDepth3(currentPosition);
			currentPosition.makeMove(engineMove);
			addMoveToMovesMade(engineMove);
		} catch (NoLegalMovesException e) {
			stopGame();
		}
	}
	
	/**
	 * Prompts the user for a move and makes that move on the currentPosition.
	 */
	public void letUserMakeMove() {
		int userMove = 0;
		
		while (inGame) {
			System.out.print("Enter your move: ");
			try {
				userMove = scanner.nextInt();
			} catch(RuntimeException e) {
				scanner.nextLine();
				System.out.println("This isn't a legal move. Moves must be a 4 digit integer. Try again.");
				continue;
			}
			
			if (currentPosition.isLegalMove(userMove)) {
				currentPosition.makeMove(userMove);
				addMoveToMovesMade(userMove);
				break;
			} else {
				System.out.println("This isn't a legal move. Try again.");
			}
		}
	}
	
	/**
	 * Allows the user to choose whether to play white or black against the engine.
	 * 
	 * @return <code>true</code> if the user chooses to play white; <code>false</code> otherwise
	 */
	public boolean userChoosesToPlayWhite() {
		boolean userToPlayWhite = true;
		
		while (true) {
			System.out.print("Enter \"true\" to play as white or \"false\" to play as black: ");
			try {
				userToPlayWhite = scanner.nextBoolean();
			} catch(RuntimeException e) {
				scanner.nextLine();
				System.out.println("This isn't one of the choices. Try again.");
				continue;
			}
			return userToPlayWhite;
		}
	}
	
	/**
	 * Adds a move to the end of movesMade.
	 * 
	 * @param move the <code>int</code> move to be added to the moves that have been made
	 */
	public void addMoveToMovesMade(int move) {
		int[] newMovesMade = new int[movesMade.length + 1];
		
		for (int i = 0; i < movesMade.length; i++) {
			newMovesMade[i] = movesMade[i];
		}
		newMovesMade[newMovesMade.length - 1] = move;
		movesMade = newMovesMade;
	}
	
	/**
	 * Closes the scanner object.
	 */
	public void closeScanner() {
		scanner.close();
	}
	
	/**
	 * Returns a string with the current position and the moves made.
	 * 
	 * @return String representation of the current position and the moves made
	 */
	public String toString() {
		String printGame = currentPosition.toString() + "\n";

		for (int i = 0; i < movesMade.length; i++) {
			printGame += movesMade[i] + " ";
			if (((i + 1) % 2) == 0)
				printGame += "\n";
		}
		return printGame;
	}
	
}