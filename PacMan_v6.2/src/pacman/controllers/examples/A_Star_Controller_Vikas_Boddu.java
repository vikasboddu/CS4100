package pacman.controllers.examples;

import java.util.Comparator;
import java.util.PriorityQueue;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.lang.*;

public class A_Star_Controller_Vikas_Boddu extends Controller<MOVE> {
    StarterGhosts ghosts = new StarterGhosts();

    private class PacManNode {
        Game gameState;
        PacManNode parent;
        int totalCost, actualCost, estimatedCost;

        public PacManNode(Game game, PacManNode parent, int actualCost) {
            this.gameState = game;
            this.parent = parent;
            this.actualCost = actualCost;
            this.estimatedCost = getHeuristic(game);
            this.totalCost = actualCost + this.estimatedCost;
        }
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        Game gameCopy = game.copy();
        PacManNode currentNode = new PacManNode(gameCopy, null, 0);

        Comparator<PacManNode> byTotalCost = (PacManNode node1, PacManNode node2)->Integer.valueOf(node1.totalCost).
                compareTo(Integer.valueOf(node2.totalCost));

        PriorityQueue<PacManNode> openList = new PriorityQueue<>(20, byTotalCost);
        PriorityQueue<PacManNode> closedList = new PriorityQueue<>(20, byTotalCost);
        openList.add(currentNode);

        while (!openList.isEmpty()) {
            if (timeDue - System.currentTimeMillis() < 9)
                break;
            currentNode = openList.poll();

            for (MOVE m : currentNode.gameState.getPossibleMoves(currentNode.gameState.getPacmanCurrentNodeIndex())) {
                gameCopy = currentNode.gameState.copy();
                gameCopy.advanceGame(m, ghosts.getMove(gameCopy, timeDue));

                if(gameCopy.wasPacManEaten()){
                    continue;
                }

                PacManNode child = new PacManNode(gameCopy, currentNode, currentNode.actualCost + 1);

                if (child.estimatedCost == 0) {
                    openList.add(child);
                    break;
                }

                if (!betterPacManNodeExists(child, openList) && !betterPacManNodeExists(child, closedList)){
                    openList.add(child);
                }
            }
            closedList.add(currentNode);
        }

        PacManNode tempNode = currentNode;
        Game tempGame = currentNode.gameState;
        MOVE move = tempGame.getPacmanLastMoveMade();
        while (tempNode.parent != null){
            move = tempNode.gameState.getPacmanLastMoveMade();
            tempNode = tempNode.parent;
        }
        return move;
    }

    private boolean betterPacManNodeExists(PacManNode node, PriorityQueue<PacManNode> queue){
        int pacmanLocation = node.gameState.getPacmanCurrentNodeIndex();
        for(PacManNode pmn: queue){
            if(pmn.totalCost < node.totalCost && pmn.gameState.getPacmanCurrentNodeIndex() == pacmanLocation){
                return true;
            }
        }
        return false;
    }

    private int getHeuristic(Game game) {
        return game.getNumberOfActivePills() * 100;
   }
}