package amazons;

import java.util.regex.Matcher;

import static amazons.Move.MOVE_PATTERN;
import static amazons.Move.isGrammaticalMove;

/**
 * A Player that takes input as text commands from the standard input.
 *
 * @author Roshan Lodha
 */
class TextPlayer extends Player {

    /**
     * A new TextPlayer with no piece or controller (intended to produce
     * a template).
     */
    TextPlayer() {
        this(null, null);
    }

    /**
     * A new TextPlayer playing PIECE under control of CONTROLLER.
     */
    private TextPlayer(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new TextPlayer(piece, controller);
    }

    @Override
    String myMove() {
        while (true) {
            String line = _controller.readLine();
            if (line == null) {
                return "quit";
            }
            Matcher match = MOVE_PATTERN.matcher(line);
            String from, to, spear;
            Move move;
            if (match.matches()) {
                if (match.group(1) == null) {
                    from = match.group(4);
                    to = match.group(5);
                    spear = match.group(6);
                } else {
                    from = match.group(1);
                    to = match.group(2);
                    spear = match.group(3);
                }
                move = Move.mv(Square.sq(from),
                        Square.sq(to), Square.sq(spear));
            } else {
                return line;
            }
            if (line == null) {
                return "quit";
            } else if (!board().isLegal(move) || !isGrammaticalMove(line)) {
                _controller.reportError("Invalid move. "
                        + "Please try  again.");
                continue;
            } else {
                return move.toString();
            }
        }
    }
}
