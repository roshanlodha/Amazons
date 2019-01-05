package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;

import ucb.junit.textui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The suite of all JUnit tests for the amazons package.
 *
 * @author Roshan Lodha & CS 61B Staff
 */
public class UnitTest {

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * Tests basic correctness of put and get on the initialized board.
     */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, 3, 5);
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /**
     * Tests proper identification of legal/illegal queen moves.
     */
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

    @Test
    public void testDirection() {
        assertEquals(0, Square.sq(1, 1).direction(Square.sq(1, 2)));
        assertEquals(1, Square.sq(1, 1).direction(Square.sq(2, 2)));
        assertEquals(2, Square.sq(1, 1).direction(Square.sq(2, 1)));
        assertEquals(3, Square.sq(1, 1).direction(Square.sq(2, 0)));
        assertEquals(4, Square.sq(1, 1).direction(Square.sq(1, 0)));
        assertEquals(5, Square.sq(1, 1).direction(Square.sq(0, 0)));
        assertEquals(6, Square.sq(1, 1).direction(Square.sq(0, 1)));
        assertEquals(7, Square.sq(1, 1).direction(Square.sq(0, 2)));
    }

    @Test
    public void testSquare() {
        assertEquals("a4", Square.sq("a4").toString());
        assertNotEquals("d5", Square.sq("d6").toString());
        assertEquals("g1", Square.sq("g1").toString());
    }

    @Test
    public void testIsUnblockedMove() {
        Board b = new Board();
        assertTrue(b.isUnblockedMove(Square.sq(0, 3),
                Square.sq(0, 4), null));
        assertFalse(b.isUnblockedMove(Square.sq(0, 3),
                Square.sq(0, 6), null));
        assertTrue(b.isUnblockedMove(Square.sq(0, 3),
                Square.sq(0, 6), Square.sq(0, 6)));
        assertFalse(b.isUnblockedMove(Square.sq(0, 3),
                Square.sq(1, 5), null));
    }

    @Test
    public void testIsLegals() {
        Board b = new Board();
        assertFalse(b.isLegal(Square.sq(0, 0)));
        assertTrue(b.isLegal(Square.sq(0, 3)));
        assertTrue(b.isLegal(Square.sq(0, 3), Square.sq(1, 4)));
        assertFalse(b.isLegal(Square.sq(0, 3), Square.sq(1, 5)));
        assertTrue(b.isLegal(Square.sq(0, 3),
                Square.sq(1, 4), Square.sq(0, 3)));
        assertFalse(b.isLegal(Square.sq("d10"),
                Square.sq("a10"), Square.sq("d10")));
    }

    @Test
    public void testMoves() {
        Board b = new Board();
        b.makeMove(Square.sq(0, 3), Square.sq(1, 4), Square.sq(2, 5));
        assertEquals(EMPTY, b.get(0, 3));
        assertEquals(WHITE, b.get(1, 4));
        assertEquals(SPEAR, b.get(2, 5));
        assertEquals(1, b.numMoves());
        assertEquals(BLACK, b.turn());
        b.undo();
        assertEquals(WHITE, b.get(0, 3));
        assertEquals(0, b.numMoves());
        assertEquals(WHITE, b.turn());
    }

    /**
     * Tests toString for initial board state and a smiling board state. :)
     */
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

    /** Tests reachableFromIterator to make sure it returns all reachable
     *  Squares. This method may need to be changed based on
     *   your implementation. */
    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, REACHABLEFROMTESTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMTESTSQUARES.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMTESTSQUARES.size(), numSquares);
        assertEquals(REACHABLEFROMTESTSQUARES.size(), squares.size());
    }

    @Test
    public void testReachableFromRobust() {
        Board b = new Board();
        buildBoard(b, REACHABLEFROMROBUSTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(1, 0), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMROBUSTSQUARES.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMROBUSTSQUARES.size(), numSquares);
        assertEquals(REACHABLEFROMROBUSTSQUARES.size(), squares.size());
    }

    /** Tests legalMovesIterator to make sure it returns all legal Moves.
     *  This method needs to be finished and may need to be changed
     *  based on your implementation. */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        buildBoard(b, LEGALMOVESTESTBOARD);
        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            assertTrue(LEGALMOVESTESTMOVES.contains(m));
            numMoves += 1;
            moves.add(m);
        }
        assertEquals(LEGALMOVESTESTMOVES.size(), numMoves);
        assertEquals(LEGALMOVESTESTMOVES.size(), moves.size());
    }

    @Test
    public void testLegalMovesInit() {
        Board b = new Board();
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            moves.add(m);
        }
        assertEquals(2176, moves.size());
    }

    @Test
    public void testWinner() {
        Board b = new Board();
        buildBoard(b, WINNERBOARD);
        b.makeMove(Square.sq("a7"), Square.sq("a6"), Square.sq("a7"));
        assertEquals(WHITE, b.winner());
    }

    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
    }

    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] REACHABLEFROMROBUSTBOARD =
    {
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, W, E, E, S, S, S, S, S, S },
    };

    static final Set<Square> REACHABLEFROMROBUSTSQUARES =
            new HashSet<>(Arrays.asList(
                    Square.sq(2, 0),
                    Square.sq(3, 0)));

    static final Piece[][] REACHABLEFROMTESTBOARD =
    {
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, W, W },
        { E, E, E, E, E, E, E, S, E, S },
        { E, E, E, S, S, S, S, E, E, S },
        { E, E, E, S, E, E, E, E, B, E },
        { E, E, E, S, E, W, E, E, B, E },
        { E, E, E, S, S, S, B, W, B, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
    };

    static final Set<Square> REACHABLEFROMTESTSQUARES =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));

    static final Piece[][] LEGALMOVESTESTBOARD =
    {
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
        { S, E, W, E, S, S, S, S, S, S },
        { S, S, S, S, S, S, S, S, S, S },
    };

    static final Piece[][] WINNERBOARD =
    {
        { S, S, S, S, E, E, E, W, S, S },
        { S, S, S, E, E, E, E, S, S, B },
        { B, S, S, E, S, S, S, S, S, S },
        { B, S, S, E, S, W, S, E, S, S },
        { E, S, S, E, E, E, S, S, S, S },
        { S, S, E, S, E, S, E, S, S, W },
        { W, S, E, E, E, E, E, S, S, B },
        { E, E, E, E, E, E, E, S, S, S },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
    };

    static final Set<Move> LEGALMOVESTESTMOVES =
            new HashSet<>(Arrays.asList(
                    Move.mv(Square.sq(2, 1),
                            Square.sq(3, 1), Square.sq(1, 1)),
                    Move.mv(Square.sq(2, 1),
                            Square.sq(3, 1), Square.sq(2, 1)),
                    Move.mv(Square.sq(2, 1),
                            Square.sq(1, 1), Square.sq(3, 1)),
                    Move.mv(Square.sq(2, 1),
                            Square.sq(1, 1), Square.sq(2, 1))));


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

}
