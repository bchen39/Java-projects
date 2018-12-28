package amazons;
import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** Junit tests for our Board iterators.
 *  @author Ben Chen
 */
public class IteratorTests {

    /** Run the JUnit tests in this package. */
    public static void main(String[] ignored) {
        textui.runClasses(IteratorTests.class);
    }

    /** Tests reachableFromIterator to make sure it returns all reachable
     *  Squares. This method may need to be changed based on
     *   your implementation. */
    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, REACHABLE);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLESQ.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLESQ.size(), numSquares);
        assertEquals(REACHABLESQ.size(), squares.size());
    }

    /** Tests legalMovesIterator to make sure it returns all legal Moves.
     *  This method needs to be finished and may need to be changed
     *  based on your implementation. */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        buildBoard(b, LEGAL);
        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            assertTrue(LEGALMOVES.contains(m));
            numMoves += 1;
            moves.add(m);
        }
        assertEquals(LEGALMOVES.size(), numMoves);
        assertEquals(LEGALMOVES.size(), moves.size());
        Board c = new Board();
        buildBoard(c, LEGAL2);
        numMoves = 0;
        moves = new HashSet<>();
        HashSet<Square> white = new HashSet<>();
        white.add(Square.sq("f5"));
        white.add(Square.sq("h4"));
        white.add(Square.sq("j9"));
        white.add(Square.sq("i9"));
        HashSet<Move> legalTestMoves2 = new HashSet<>();
        for (Square fr: white) {
            Iterator<Square> iter = c.reachableFrom(fr, null);
            HashSet<Square> temp = new HashSet<>();
            while (iter.hasNext()) {
                temp.add(iter.next());
            }
            for (Square sq: temp) {
                Iterator<Square> spear = c.reachableFrom(sq, fr);
                while (spear.hasNext()) {
                    Square sp = spear.next();
                    Move curr = Move.mv(fr, sq, sp);
                    if (c.isLegal(curr)) {
                        legalTestMoves2.add(curr);
                    }
                }
            }
        }
        Iterator<Move> legalMoves2 = c.legalMoves(Piece.WHITE);
        while (legalMoves2.hasNext()) {
            Move m = legalMoves2.next();
            assertTrue(String.format("%s", m.toString()),
                    legalTestMoves2.contains(m));
            numMoves += 1;
            moves.add(m);
        }
        assertEquals(legalTestMoves2.size(), numMoves);
        assertEquals(legalTestMoves2.size(), moves.size());
    }


    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] REACHABLE =
        {{ E, E, E, E, E, E, E, E, E, E },
                { E, E, E, E, E, E, E, E, W, W },
                { E, E, E, E, E, E, E, S, E, S },
                { E, E, E, S, S, S, S, E, E, S },
                { E, E, E, S, E, E, E, E, B, E },
                { E, E, E, S, E, W, E, E, B, E },
                { E, E, E, S, S, S, B, W, B, E },
                { E, E, E, E, E, E, E, E, E, E },
                { E, E, E, E, E, E, E, E, E, E },
                { E, E, E, E, E, E, E, E, E, E }
        };

    static final Set<Square> REACHABLESQ =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));

    static final Piece[][] LEGAL =
        {{ S, S, S, S, S, S, S, S, S, S },
                { S, S, S, S, S, S, S, S, W, W },
                { S, S, S, S, S, S, S, S, S, S },
                { S, S, S, S, S, S, S, S, S, S },
                { S, S, S, S, E, S, E, S, B, S },
                { S, S, S, S, E, W, E, S, B, S },
                { S, S, S, S, S, S, B, W, B, S },
                { S, S, S, S, S, S, S, S, S, S },
                { S, S, S, S, S, S, S, S, S, S },
                { S, S, S, S, S, S, S, S, S, S },
        };

    static final Set<Move> LEGALMOVES =
            new HashSet<>(Arrays.asList(
                    Move.mv(Square.sq("f5"), Square.sq("e5"), Square.sq("f5")),
                    Move.mv(Square.sq("f5"), Square.sq("e5"), Square.sq("e6")),
                    Move.mv(Square.sq("f5"), Square.sq("e5"), Square.sq("g5")),
                    Move.mv(Square.sq("f5"), Square.sq("e6"), Square.sq("f5")),
                    Move.mv(Square.sq("f5"), Square.sq("e6"), Square.sq("e5")),
                    Move.mv(Square.sq("f5"), Square.sq("g6"), Square.sq("f5")),
                    Move.mv(Square.sq("f5"), Square.sq("g6"), Square.sq("g5")),
                    Move.mv(Square.sq("f5"), Square.sq("g5"), Square.sq("f5")),
                    Move.mv(Square.sq("f5"), Square.sq("g5"), Square.sq("e5")),
                    Move.mv(Square.sq("f5"), Square.sq("g5"), Square.sq("g6")),
                    Move.mv(Square.sq("h4"), Square.sq("g5"), Square.sq("h4")),
                    Move.mv(Square.sq("h4"), Square.sq("g5"), Square.sq("g6"))
            ));

    static final Piece[][] LEGAL2 =
        {{ E, E, E, E, E, E, E, E, S, B },
                { E, E, E, E, E, E, E, E, W, W },
                { E, E, E, E, E, E, E, S, S, S },
                { E, E, E, S, S, S, S, E, E, S },
                { E, E, E, S, E, E, E, E, B, E },
                { E, E, E, S, E, W, E, E, B, E },
                { E, E, E, S, S, S, B, W, B, E },
                { E, E, E, E, E, E, E, E, E, E },
                { E, E, E, E, E, E, E, E, E, E },
                { E, E, E, E, E, E, E, E, E, E },
        };

}
