package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.util.LinkedList;
import java.util.Stack;

public class DFS_Controller_Vikas_Boddu extends Controller<MOVE> {
    private static final MOVE[] allMoves = MOVE.values();
    private static StarterGhosts ghosts = new StarterGhosts();
    private static boolean traverseBack = false;
    private static Stack<MOVE> traverseBackMoves = new Stack<>();
    private static int lastNode = -1;
    private static LinkedList<Integer> nodeIndices= new LinkedList<Integer>();

    @Override
    public MOVE getMove(Game game, long timeDue) {
        int highScore = -1;
        MOVE highMove = null;
        int currentNode = game.getPacmanCurrentNodeIndex();

        //resets our back tracking of the tree
        if (game.wasPacManEaten()) {
            traverseBack = false;
            traverseBackMoves.clear();
        }

        //back tracks the tree should our conditions be met
        //skips entire computation of DFS
        if (traverseBack) {
            if (traverseBackMoves.empty())
                traverseBack = false;
            else
                return traverseBackMoves.pop().opposite();
        }

        //checks which move has the highest DFS score
        for (MOVE move : allMoves) {
            Game gameCopy = game.copy();
            gameCopy.advanceGame(move, ghosts.getMove(gameCopy, timeDue));
            int potentialHighScore = dfs_vikas(new PacManNode(gameCopy, 0), 9);
            if (potentialHighScore > highScore) {
                highScore = potentialHighScore;
                highMove = move;
            }
        }

        //if last node seen is also the current node, then we add it to a list of indices we have seen
        if (lastNode == currentNode)
            nodeIndices.add(currentNode);
        else
            nodeIndices.clear();

        //if we have seen the same node 30 amount of times then we will want to traverse back
        if (nodeIndices.size() > 30)
            traverseBack = true;

        lastNode = currentNode;
        game.getPacmanCurrentNodeIndex();
        traverseBackMoves.push(highMove);
        return highMove;
    }

    public int dfs_vikas(PacManNode rootGameState, int maxDepth) {
        int highScore = -1;
        Stack<PacManNode> stack = new Stack<>();
        stack.push(rootGameState);

        while (!stack.empty()) {
            PacManNode firstNode = stack.pop();
            /**
             * if we have a highscore, return it
             * else find the single best move from the firstNode and then add that to the stack
             * reiterate this process until we return a high score
             * this if...else... statement essentially traverses to a single leaf
             * it goes after whichever next move has the highest score
             */
            if (firstNode.depth >= maxDepth) {
                int score = firstNode.gameState.getScore();
                if (score > highScore)
                    highScore = score;
            } else {
                MOVE topMove = null;
                int nextHighScore = -1;
                for (MOVE move : allMoves) {
                    Game gameCopy = firstNode.gameState.copy();
                    gameCopy.advanceGame(move, ghosts.getMove(gameCopy, 0));
                    int potentialHighScore = gameCopy.getScore();
                    if (potentialHighScore > nextHighScore) {
                        topMove = move;
                        nextHighScore = potentialHighScore;
                    }
                }
                Game gameCopy = firstNode.gameState.copy();
                gameCopy.advanceGame(topMove, ghosts.getMove(gameCopy, 0));
                PacManNode node = new PacManNode(gameCopy, firstNode.depth + 1);
                stack.push(node);
            }
        }
        return highScore;
    }
}
