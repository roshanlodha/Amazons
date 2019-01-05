package amazons;

import java.util.Iterator;

import static amazons.Piece.*;

/**
 * A Player that automatically generates moves.
 *
 * @author Roshan Lodha
 */
class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth,
                         boolean saveMove, int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        Iterator<Move> legalmoves = board.legalMoves();
        while (legalmoves.hasNext()) {
            Move tempMove = legalmoves.next();
            board.makeMove(tempMove);
            if (sense == 1) {
                int bestVal = findMove(board, depth - 1,
                        false, -1, alpha, beta);
                if (bestVal > alpha) {
                    alpha = bestVal;
                    if (saveMove) {
                        _lastFoundMove = tempMove;
                    }
                }
                board.undo();
                if (beta <= alpha) {
                    break;
                }
            } else {
                int bestVal = findMove(board, depth - 1,
                        false, 1, alpha, beta);
                if (bestVal < beta) {
                    beta = bestVal;
                    if (saveMove) {
                        _lastFoundMove = tempMove;
                    }
                }
                board.undo();
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return staticScore(board);
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        return (int) Math.pow(N / 12 + 1, CRITICALVALUE);
    }

    /**
     * An important integer.
     * */
    private static final double CRITICALVALUE = 1.5;

    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        Iterator<Move> allMyMoves = board.legalMoves();
        int myMoves = 0;
        while (allMyMoves.hasNext()) {
            myMoves += 1;
            allMyMoves.next();
        }
        Iterator<Move> allOppMoves = board.legalMoves(board.turn().opponent());
        int oppMoves = 0;
        while (allOppMoves.hasNext()) {
            oppMoves += 1;
            allOppMoves.next();
        }
        if (board.turn() == WHITE) {
            return myMoves - oppMoves;
        } else {
            return oppMoves - myMoves;
        }
    }

}
