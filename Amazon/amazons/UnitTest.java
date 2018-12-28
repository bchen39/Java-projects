package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.HashSet;
import java.util.Iterator;

/** The suite of all JUnit tests for the amazons package.
 *  @author Ben Chen
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";

    /** Test copy(). */
    @Test
    public void testCopy() {
        Board b = new Board();
        Move m1 = Move.mv(Square.sq("d1"), Square.sq("d5"), Square.sq("h6"));
        Move m2 = Move.mv(Square.sq("a7"), Square.sq("c7"), Square.sq("a10"));
        b.makeMove(m1);
        b.makeMove(m2);
        Board c = new Board(b);
        assertEquals(WHITE, c.turn());
        assertNull(c.winner());
        assertEquals(2, c.numMoves());
        assertEquals(m2, c.lastMove());
        for (int i = 0; i <= 9; i += 1) {
            for (int j = 0; j <= 9; j += 1) {
                Square curr = Square.sq(i, j);
                assertEquals(b.get(curr), c.get(curr));
            }
        }
    }

    /** Test queenMove() for invalid and valid cases. */
    @Test
    public void testQueenMove() {
        Square t1 = Square.sq(5, 5);
        assertEquals(Square.sq(3, 7), t1.queenMove(7, 2));
        assertEquals(Square.sq(5, 7), t1.queenMove(0, 2));
        assertEquals(Square.sq(7, 7), t1.queenMove(1, 2));
        assertNull(t1.queenMove(2, 5));
        assertNull(t1.queenMove(-1, 3));
    }

    /** Test direction(). */
    @Test
    public void testDir() {
        Square t2 = Square.sq(5, 5);
        for (int i = 0; i < 8; i += 1) {
            assertEquals(i, t2.direction(t2.queenMove(i, 2)));
        }
    }

    /** Test isLegal and isUnblockedMove using smiley board. */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        makeSmile(b);
        assertTrue(b.isLegal(Square.sq("c4")));
        assertTrue(b.isLegal(Square.sq("d3")));
        assertFalse(b.isLegal(Square.sq("d4")));
        assertTrue(b.isUnblockedMove(Square.sq("c4"), Square.sq("c6"), null));
        assertFalse(b.isUnblockedMove(Square.sq("c4"), Square.sq("c7"), null));
        assertFalse(b.isLegal(Square.sq("c5"), Square.sq("c6")));
        assertTrue(b.isLegal(Square.sq("c4"), Square.sq("c6")));
        assertTrue(b.isLegal(Square.sq("d3"), Square.sq("g6")));
    }

    /** Test ReachableFromIterator using smiley board. */
    @Test
    public void testReachableIterator() {
        Board b = new Board();
        makeSmile(b);
        HashSet<Square> test1 = new HashSet<>();
        Square s1 = Square.sq("c4");
        for (int i = 0; i < 8; i += 1) {
            for (int j = 1; j < 9; j += 1) {
                Square curr = s1.queenMove(i, j);
                if (!(curr == null) && b.isUnblockedMove(s1, curr, null)) {
                    test1.add(curr);
                }
            }
        }
        Iterator<Square> t = b.reachableFrom(s1, null);
        while (t.hasNext()) {
            assertTrue(test1.contains(t.next()));
        }
    }

    /** Test LegalMovesIterator using smiley board. */
    @Test
    public void testLegalIterator() {
        Board b = new Board();
        makeSmile(b);
        HashSet<Square> white = new HashSet<>();
        white.add(Square.sq("c4"));
        white.add(Square.sq("d3"));
        white.add(Square.sq("e3"));
        white.add(Square.sq("f3"));
        white.add(Square.sq("g3"));
        white.add(Square.sq("h4"));
        HashSet<Move> test2 = new HashSet<>();
        for (Square fr: white) {
            Iterator<Square> iter = b.reachableFrom(fr, null);
            HashSet<Square> temp = new HashSet<>();
            while (iter.hasNext()) {
                temp.add(iter.next());
            }
            for (Square sq: temp) {
                Iterator<Square> spear = b.reachableFrom(sq, fr);
                while (spear.hasNext()) {
                    Square sp = spear.next();
                    Move curr = Move.mv(fr, sq, sp);
                    if (b.isLegal(curr)) {
                        test2.add(curr);
                    }
                }
            }
        }
        Iterator<Move> t2 = b.legalMoves(Piece.WHITE);
        HashSet<Move> result = new HashSet<>();
        while (t2.hasNext()) {
            Move curr = t2.next();
            assertTrue(String.format("%s length %d",
                    curr.toString(), result.size()), test2.contains(curr));
            result.add(curr);
        }
        assertEquals(test2.size(), result.size());
    }

    /** Test makeMove and undo. */
    @Test
    public void testGame() {
        Board b = new Board();
        Move m1 = Move.mv(Square.sq("d1"), Square.sq("d5"), Square.sq("h6"));
        Move m2 = Move.mv(Square.sq("a7"), Square.sq("c7"), Square.sq("a10"));
        b.makeMove(m1);
        assertEquals(m1, b.lastMove());
        b.makeMove(m2);
        assertEquals(m2, b.lastMove());
        assertEquals(2, b.numMoves());
        assertEquals(EMPTY, b.get(Square.sq("d1")));
        assertEquals(EMPTY, b.get(Square.sq("a7")));
        assertEquals(WHITE, b.get(Square.sq("d5")));
        assertEquals(BLACK, b.get(Square.sq("c7")));
        assertEquals(SPEAR, b.get(Square.sq("h6")));
        assertEquals(SPEAR, b.get(Square.sq("a10")));
        b.undo();
        assertEquals(m1, b.lastMove());
        assertEquals(BLACK, b.get(Square.sq("a7")));
        assertEquals(EMPTY, b.get(Square.sq("c7")));
        assertEquals(EMPTY, b.get(Square.sq("a10")));
    }

    /** Test if the game correctly outputs winner. */
    @Test
    public void testWinner() {
        Board b = new Board();
        for (int i = 0; i <= 9; i += 1) {
            for (int j = 0; j <= 9; j += 1) {
                b.put(EMPTY, Square.sq(i, j));
            }
        }
        b.put(SPEAR, Square.sq("a9"));
        b.put(SPEAR, Square.sq("b10"));
        b.put(BLACK, Square.sq("a10"));
        b.put(WHITE, Square.sq("j1"));
        Move m = Move.mv(Square.sq("j1"), Square.sq("j2"), Square.sq("b9"));
        b.makeMove(m);
        Move m2 = Move.mv(Square.sq("c1"), Square.sq("c2"), Square.sq("c3"));
        b.makeMove(m2);
        assertEquals(m, b.lastMove());
        assertFalse(b.legalMoves().hasNext());
        assertEquals(WHITE, b.winner());
    }
}
