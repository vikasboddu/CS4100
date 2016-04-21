package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import java.util.Random;

public class Min_Max_Controller_Vikas_Boddu extends Controller<MOVE> {
    private static final MOVE[] allMoves = MOVE.values();
    private static StarterGhosts ghosts = new StarterGhosts();
    private static final double infinity = Double.POSITIVE_INFINITY;
    private static final Random random = new Random();

    @Override
    public MOVE getMove(Game game, long timeDue) {
        MOVE[] currentMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
        int highScore = 0;
        MOVE highMove = MOVE.NEUTRAL;

        for (MOVE move : currentMoves) {
            Game gameCopy = game.copy();
            gameCopy.advanceGame(move, ghosts.getMove(gameCopy, timeDue));
            int potentialHighScore = minMax(game, 5, true, timeDue);
            if (potentialHighScore >= highScore) {
                highScore = potentialHighScore;
                highMove = move;
            }
        }
        return highMove;
    }

    public int minMax(Game game, int depth, Boolean pacMan, long timeDue) {
        double bestValue;

        if (depth == 0) {
            return getHeuristic(game);
        }

        if (pacMan) {
            bestValue = -infinity;
            Game gameCopy = game.copy();
            for (MOVE move : allMoves) {
                gameCopy.advanceGame(move, ghosts.getMove(gameCopy, timeDue));
                int temp = minMax(gameCopy, depth - 1, false, timeDue);
                bestValue = Math.max(temp, bestValue);
            }
            return (int) bestValue;
        } else {
            bestValue = infinity;
            Game gameCopy = game.copy();
            for (MOVE move : allMoves) {
                gameCopy.advanceGame(move, ghosts.getMove(gameCopy, timeDue));
                int temp = minMax(gameCopy, depth - 1, true, timeDue);
                bestValue = Math.min(temp, bestValue);
            }
            return (int) bestValue;
        }

    }

    private int getHeuristic(Game game) {
        int currentScore = game.getScore();
        int nodeIndex = game.getPacmanCurrentNodeIndex();
        Constants.GHOST[] ghosts = GHOST.values();

        int finalScore = currentScore +
                game.getManhattanDistance(nodeIndex,
                        game.getGhostCurrentNodeIndex(ghosts[random.nextInt(ghosts.length)]));

        return finalScore * 100;
    }
}