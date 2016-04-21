package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;

import java.util.HashSet;

public class Q_Learning_Controller_Vikas_Boddu extends Controller<MOVE> {
    private static final StarterGhosts ghosts = new StarterGhosts();

    private static final double discountFactor = 1;
    private static final double learningRate = 1;

    private double q = 0;
    private State currentState;
    private HashSet<State> states = new HashSet<>();

    @Override
    public MOVE getMove(Game game, long timeDue) {

        int currIndex = game.getPacmanCurrentNodeIndex();
        currentState= new State(game);

        MOVE[] allMoves = game.getPossibleMoves(currIndex);

        double highScore = Double.MIN_VALUE;
        MOVE highMove = MOVE.NEUTRAL;

        for (MOVE m : allMoves) {
            Game gameCopy = game.copy();

            gameCopy.advanceGame(m, ghosts.getMove(gameCopy, timeDue));
            State nextState = new State(gameCopy);

            updateQValue(currentState, nextState);
        }

        for (State possibleState : states) {
            if (possibleState.game.getScore() > highScore) {
                highScore = possibleState.game.getScore();
                highMove = possibleState.game.getPacmanLastMoveMade();
            }
        }

        return highMove;
    }

    public void updateQValue(State current, State next) {
        int livesLeft = next.game.getPacmanNumberOfLivesRemaining() - current.game.getPacmanNumberOfLivesRemaining();
        int reward = (next.game.wasPillEaten() ? 100 : 0) +
                    3000 * livesLeft;

        if (states.isEmpty() || !states.contains(next)) {
            next.score = reward;
        } else {
            for (State possibleState : states) {
                if (currentState.equals(possibleState)) {
                    double temp = 0;
                    if (temp > currentState.score) {
                        temp = currentState.score;
                    }

                    q = q + learningRate * (reward + discountFactor * temp - next.score);
                    states.remove(next);
                    break;
                }
            }
        }
        states.add(next);
    }

    class State {

        public Game game;
        private int distPill, distPowerPill, distGhost;
        public double score;

        public State(Game game) {
            this.game = game;
            int currIndex = game.getPacmanCurrentNodeIndex();

            int[] pills = game.getActivePillsIndices();
            this.distPill = Integer.MAX_VALUE;
            for (int pill : pills) {
                int temp = game.getShortestPathDistance(currIndex, pill);
                if (temp < this.distPill) {
                    distPill = temp;
                }
            }

            int[] powerPills = game.getActivePillsIndices();
            this.distPowerPill = Integer.MAX_VALUE;
            for (int powerPill : powerPills) {
                int temp = game.getShortestPathDistance(currIndex, powerPill);
                if (temp < this.distPowerPill) {
                    distPowerPill = temp;
                }
            }

            GHOST[] ghosts = GHOST.values();
            this.distGhost = Integer.MAX_VALUE;
            for (GHOST ghost : ghosts) {
                int temp = game.getShortestPathDistance(currIndex, game.getGhostCurrentNodeIndex(ghost));
                if (temp < distGhost) {
                    this.distGhost = temp;
                }
            }
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            State node = (State) other;
            return this.distGhost == node.distGhost &&
                    this.distPill == node.distPill &&
                    this.distPowerPill == node.distPowerPill;
        }
    }
}
