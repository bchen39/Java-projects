package amazons;

import static java.lang.Math.*;

import static amazons.Piece.BLACK;
import static amazons.Piece.WHITE;

import java.util.HashSet;
import java.util.Iterator;

/** A Player that automatically generates moves.
 *  @author Ben Chen
 */
class AI extends Player {

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
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

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        Move bestm = null;
        Iterator<Move> moves = board.legalMoves();
        HashSet<Move> moveorg = orgMoves(moves);
        if (sense == 1) {
            int bestVal = Integer.MIN_VALUE;
            for (Move m: moveorg) {
                Board c = new Board(board);
                c.makeMove(m);
                int temp = bestVal;
                bestVal = Math.max(bestVal,
                        findMove(c, depth - 1,
                                false, 0 - sense, alpha, beta));
                if (temp < bestVal) {
                    bestm = m;
                }
                if (bestVal > beta) {
                    if (saveMove) {
                        _lastFoundMove = bestm;
                    }
                    return bestVal;
                }
                alpha = Math.max(alpha, bestVal);
            }
            if (saveMove) {
                _lastFoundMove = bestm;
            }
            return bestVal;
        } else {
            int bestVal = Integer.MAX_VALUE;
            for (Move m: moveorg) {
                Board c = new Board(board);
                c.makeMove(m);
                int temp = bestVal;
                bestVal = Math.min(bestVal,
                        findMove(c, depth - 1,
                                false, 0 - sense, alpha, beta));
                if (temp > bestVal) {
                    bestm = m;
                }
                if (bestVal < alpha) {
                    if (saveMove) {
                        _lastFoundMove = bestm;
                    }
                    return bestVal;
                }
                beta = Math.min(beta, bestVal);
            }
            if (saveMove) {
                _lastFoundMove = bestm;
            }
            return bestVal;
        }
    }

    /**@param mv iterator of all moves current piece can make.
     * @return a list of all moves that have at least one move
     * (spear or to) blocking the enemy. Otherwise the rest of
     * the moves. */
    private HashSet<Move> orgMoves(Iterator<Move> mv) {
        HashSet<Square> opp = new HashSet<>();
        HashSet<Square> oppqueen = new HashSet<>();
        HashSet<Move> oneblock = new HashSet<>();
        HashSet<Move> noblock = new HashSet<>();
        for (int i = 0; i <= 9; i += 1) {
            for (int j = 0; j <= 9; j += 1) {
                if (board().get(Square.sq(i, j))
                        .equals(myPiece().opponent())) {
                    opp.add(Square.sq(i, j));
                }
            }
        }
        for (Square sq: opp) {
            Iterator<Square> iter =
                    board().reachableFrom(sq, null);
            while (iter.hasNext()) {
                oppqueen.add(iter.next());
            }
        }
        while (mv.hasNext()) {
            Move curr = mv.next();
            if (oppqueen.contains(curr.to())
                    || oppqueen.contains(curr.spear())) {
                oneblock.add(curr);
            } else {
                noblock.add(curr);
            }
        }
        if (oneblock.size() > 0) {
            return oneblock;
        } else {
            return noblock;
        }
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        if (N < Integer.parseInt("22")) {
            return 1;
        } else if (N < Integer.parseInt("44")) {
            return 2;
        } else if (N < Integer.parseInt("55")) {
            return 3;
        } else if (N < Integer.parseInt("66")) {
            return 4;
        } else {
            return 5;
        }
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        Iterator<Move> legalself = board.legalMoves(WHITE);
        Iterator<Move> legalopp = board.legalMoves(BLACK);
        int self = 0, opp = 0;
        while (legalself.hasNext()) {
            self += 1;
            legalself.next();
        }
        while (legalopp.hasNext()) {
            opp += 1;
            legalopp.next();
        }
        return self - opp;
    }
}
