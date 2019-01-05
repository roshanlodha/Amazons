package amazons;

import java.util.Collections;
import java.util.Stack;
import java.util.Iterator;

import static amazons.Piece.*;

/**
 * The state of an Amazons Game.
 *
 * @author Roshan Lodha
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        init();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                _model[i][j] = model._model[i][j];
            }
        }
        _turn = model._turn;
        _winner = model._winner;
        _moves = model._moves;
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        _model = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                setElem(i * SIZE + j, i, j);
            }
        }
        _turn = WHITE;
        _winner = EMPTY;
        _moves = new Stack<>();
    }

    /**
     * Helper method to initialize board.
     *
     * @param index the index
     * @param col the column
     * @param row the row
     */
    void setElem(int index, int row, int col) {
        if (index == 3 * 10 || index == 3 || index == 6
                || index == 3 * 10 + 9) {
            _model[row][col] = WHITE;
        } else if (index == 6 * 10 || index == 9 * 10 + 3
                || index == 9 * 10 + 6 || index == 6 * 10 + 9) {
            _model[row][col] = BLACK;
        } else {
            _model[row][col] = EMPTY;
        }
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return _moves.size();
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        if (_winner == EMPTY) {
            return null;
        }
        return _winner;
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW < 9.
     */

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return _model[row][col];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {
        _model[row][col] = p;
        _winner = EMPTY;
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to) || from == null || to ==  null) {
            return false;
        }
        int dir = from.direction(to);
        while (from != to) {
            from = from.queenMove(dir, 1);
            if (_model[from.row()][from.col()] != EMPTY && from != asEmpty) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        return _model[from.row()][from.col()] == turn();
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && isUnblockedMove(from, to, null);
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to)
                && isUnblockedMove(to, spear, from);
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(Square from, Square to, Square spear) {
        if (_turn == WHITE) {
            _model[to.row()][to.col()] = WHITE;
        } else {
            _model[to.row()][to.col()] = BLACK;
        }
        _model[from.row()][from.col()] = EMPTY;
        _model[spear.row()][spear.col()] = SPEAR;
        _moves.push(Move.mv(from, to, spear));
        checkWinner();
        _turn = _turn.opponent();
        if (winner() == null) {
            checkWinner();
        }
    }

    /**
     * Helper method that checks is someone has won the game.
     */
    public void checkWinner() {
        Iterator<Move> temp = legalMoves();
        if (!temp.hasNext()) {
            _winner = _turn.opponent();
        }
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /**
     * Undo one move.  H as no effect on the initial board.
     */
    void undo() {
        Move last = _moves.pop();
        Square from = last.from();
        Square to = last.to();
        Square spear = last.spear();
        _turn = _turn.opponent();
        if (_turn == WHITE) {
            _model[from.row()][from.col()] = WHITE;
        } else {
            _model[from.row()][from.col()] = BLACK;
        }
        _model[to.row()][to.col()] = EMPTY;
        _model[spear.row()][spear.col()] = EMPTY;
    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.)
     */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<Square> {

        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
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
            Square temp = _to;
            toNext();
            return temp;
        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
            _steps += 1;
            if (_dir == 8) {
                return;
            }
            if (_from.isRealSquare(_dir, _steps)) {
                _to = _from.queenMove(_dir, _steps);
                if (!isUnblockedMove(_from, _to, _asEmpty)) {
                    _dir += 1;
                    _steps = 0;
                    toNext();
                }
            } else {
                _dir += 1;
                _steps = 0;
                toNext();
            }
        }

        /**
         * Starting square.
         */
        private Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private Square _asEmpty;

        /**
         * Return square.
         */
        private Square _to;
    }

    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<Move> {

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
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
            Move temp = _move;
            toNext();
            return temp;
        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            if (!hasNext()) {
                return;
            }
            if (!_spearThrows.hasNext() && !_pieceMoves.hasNext()) {
                _start = _startingSquares.next();
                while (_model[_start.row()][_start.col()] != _fromPiece) {
                    if (!hasNext()) {
                        return;
                    }
                    _start = _startingSquares.next();
                }
                _pieceMoves = reachableFrom(_start, null);
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
            } else if (!_spearThrows.hasNext()) {
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
            } else if (!_pieceMoves.hasNext() && !_spearThrows.hasNext()) {
                _start = _startingSquares.next();
                while (_model[_start.row()][_start.col()] != _fromPiece) {
                    if (!hasNext()) {
                        return;
                    }
                    _start = _startingSquares.next();
                }
                _pieceMoves = reachableFrom(_start, null);
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
            }
            Square temp = _spearThrows.next();
            _move = Move.mv(_start, _nextSquare, temp);
            if (!isUnblockedMove(_start, _nextSquare, temp)) {
                toNext();
            }
        }

        /**
         * Color of side whose moves we are iterating.
         */
        private Piece _fromPiece;
        /**
         * Current starting square.
         */
        private Square _start;
        /**
         * Remaining starting squares to consider.
         */
        private Iterator<Square> _startingSquares;
        /**
         * Current piece's new position.
         */
        private Square _nextSquare;
        /**
         * Remaining moves from _start to consider.
         */
        private Iterator<Square> _pieceMoves;
        /**
         * Remaining spear throws from _piece to consider.
         */
        private Iterator<Square> _spearThrows;
        /**
         * The next move to return.
         * */
        private Move _move;
    }

    @Override
    public String toString() {
        String string = "";
        for (int i = SIZE - 1; i >= 0; i--) {
            String gather = "   ";
            for (int j = 0; j < SIZE - 1; j++) {
                gather += _model[i][j] + " ";
            }
            string += gather + _model[i][SIZE - 1] + "\n";
        }
        return string;
    }

    /**
     * An empty iterator for initialization.
     */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private Piece _winner;
    /**
     * A representation of the board.
     * */
    private Piece[][] _model;
    /**
     * A stack containing all the moves made.
     * */
    private Stack<Move> _moves;
}
