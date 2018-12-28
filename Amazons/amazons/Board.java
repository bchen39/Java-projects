package amazons;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Collections;
import static amazons.Piece.EMPTY;
import static amazons.Piece.SPEAR;
import static amazons.Piece.WHITE;
import static amazons.Piece.BLACK;


/** The state of an Amazons Game.
 *  @author Ben Chen
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        init();
        _turn = model.turn();
        _winner = model.winner();
        for (Square sq: _allsquares.keySet()) {
            put(model.get(sq), sq);
        }
        _numMoves = model._numMoves;
        _moves = model._moves.clone();
    }

    /** Clears the board to the initial position. */
    void init() {
        _turn = WHITE;
        _winner = null;
        _numMoves = 0;
        _moves = new ArrayDeque<>();
        _allsquares = new HashMap<>();
        for (int i = 0; i <= 9; i += 1) {
            for (int j = 0; j <= 9; j += 1) {
                _allsquares.put(Square.sq(i, j), EMPTY);
            }
        }
        put(WHITE, Square.sq(0, 3));
        put(WHITE, Square.sq(9, 3));
        put(WHITE, Square.sq(3, 0));
        put(WHITE, Square.sq(6, 0));
        put(BLACK, Square.sq(3, 9));
        put(BLACK, Square.sq(9, 6));
        put(BLACK, Square.sq(6, 9));
        put(BLACK, Square.sq(0, 6));
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return _numMoves;
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        return _winner;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return _allsquares.get(s);
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return get(Square.sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _allsquares.put(s, p);
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        put(p, Square.sq(col, row));
        _winner = null;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from,
                            Square to, Square asEmpty) {
        assert from.isQueenMove(to);
        for (int i = 1; i <= Math.max(
                Math.abs(to.row() - from.row()),
                Math.abs(to.col() - from.col()));
             i += 1
        ) {
            Square curr = from.queenMove(from.direction(to), i);
            if (!EMPTY.equals(get(curr)) && !(curr == asEmpty)) {
                return false;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return WHITE.equals(get(from))
                || BLACK.equals(get(from));
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isLegal(from)
                && from.isQueenMove(to)
                && isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to) && to.isQueenMove(spear)
                && isUnblockedMove(to, spear, from);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        if (winner() == null) {
            _allsquares.put(from, EMPTY);
            _allsquares.put(to, _turn);
            _allsquares.put(spear, SPEAR);
            _numMoves += 1;
            _moves.addLast(Move.mv(from, to, spear));
            Piece winner = _turn;
            _turn = turn().opponent();
            if (!legalMoves().hasNext()) {
                _winner = winner;
            }
        }
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_numMoves >= 1) {
            _turn = turn().opponent();
            _allsquares.put(lastMove().spear(), EMPTY);
            _allsquares.put(lastMove().to(), EMPTY);
            _allsquares.put(lastMove().from(), _turn);
            _moves.pollLast();
            _numMoves -= 1;
        }
    }

    /** @return last move made. */
    Move lastMove() {
        return _moves.getLast();
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            if (hasNext()) {
                Square result = _from.queenMove(_dir, _steps);
                toNext();
                return result;
            }
            throw new NoSuchElementException("No more moves.");
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            Square curr = _from.queenMove(_dir, _steps + 1);
            while ((curr == null
                    || !isUnblockedMove(_from, curr, _asEmpty))
                    && hasNext()) {
                _dir += 1;
                _steps = 0;
                curr = _from.queenMove(_dir, _steps + 1);
            }
            _steps += 1;
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _startingSquares.hasNext();
        }

        @Override
        public Move next() {
            if (hasNext()) {
                Move result = Move.mv(_start, _nextSquare, _spear);
                toNext();
                return result;
            }
            throw new NoSuchElementException("No more moves.");
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            if (hasNext()) {
                if (!_pieceMoves.hasNext() && !_spearThrows.hasNext()) {
                    _start = _startingSquares.next();
                    _pieceMoves =
                            new ReachableFromIterator(_start, null);
                    while ((!_fromPiece.equals(get(_start))
                            || !_pieceMoves.hasNext())
                            && _startingSquares.hasNext()) {
                        _start = _startingSquares.next();
                        _pieceMoves =
                                new ReachableFromIterator(_start, null);
                    }
                }
                if (!_spearThrows.hasNext()) {
                    if (_pieceMoves.hasNext()) {
                        _nextSquare = _pieceMoves.next();
                    }
                    if (_nextSquare != null) {
                        _spearThrows =
                                new ReachableFromIterator(_nextSquare, _start);
                    }
                    if (_spearThrows.hasNext()) {
                        _spear = _spearThrows.next();
                    }
                } else {
                    _spear = _spearThrows.next();
                }
            }
        }

        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
        /** Spear thrown at position. */
        private Square _spear;
    }

    @Override
    public String toString() {
        String result = "";
        for (int row = 9; row >= 0; row -= 1) {
            result += "   ";
            for (int col = 0; col <= 9; col += 1) {
                Piece curr = get(col, row);
                switch (curr) {
                case BLACK: result += "B";
                    break;
                case WHITE: result += "W";
                    break;
                case SPEAR: result += "S";
                    break;
                default: result += "-";
                }
                if (col == 9) {
                    result += "\n";
                } else {
                    result += " ";
                }
            }
        }
        return result;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Squares. */
    private HashMap<Square, Piece> _allsquares;
    /** List of all moves. */
    private ArrayDeque<Move> _moves;
    /** Moves taken. */
    private int _numMoves;
}
