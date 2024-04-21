package packageForChessEngine;

/**
 * A game of type: engine playing against engine.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class EngineVsEngine extends Game {
	/**
	 * Default constructor.
	 */
	public EngineVsEngine() {
		super();
	}
	
	/**
	 * Starts the engineVsEngine game. This game will end automatically when a game ending 
	 * situation is reached.
	 */
	public void play() {
		startGame();
		while(inGame) {
			letEngineMakeMove();
			System.out.println(this);
		}
	}
}
