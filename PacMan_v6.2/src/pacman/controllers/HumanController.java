package pacman.controllers;

import java.awt.event.KeyEvent;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public class HumanController extends Controller<MOVE>
{
	public KeyBoardInput input;
    
    public HumanController(KeyBoardInput input)
    {
    	this.input=input;
    }
    
    public KeyBoardInput getKeyboardInput()
    {
    	return input;
    }

    public MOVE getMove(Game game,long dueTime)
    {
    	switch(input.getKey())
    	{
	    	case KeyEvent.VK_W: 	return MOVE.UP;
	    	case KeyEvent.VK_D: 	return MOVE.RIGHT;
	    	case KeyEvent.VK_S: 	return MOVE.DOWN;
	    	case KeyEvent.VK_A: 	return MOVE.LEFT;
	    	default: 				return MOVE.NEUTRAL;
    	}
    }
}