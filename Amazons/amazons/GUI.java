package amazons;

import ucb.gui2.TopLevel;
import ucb.gui2.LayoutSpec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.concurrent.ArrayBlockingQueue;


/** The GUI controller for an Amazons board and buttons.
 *  @author Ben Chen
 */
class GUI extends TopLevel implements View, Reporter {

    /** Minimum size of board in pixels. */
    private static final int MIN_SIZE = 500;

    /** A new window with given TITLE providing a view of an Amazons board. */
    GUI(String title) {
        super(title, true);

        addMenuButton("Game->Quit", this::quit);
        addMenuButton("Game->New Game", this::newGame);
        addMenuButton("Settings->Seed", this::newSeed);
        addMenuButton("Settings->M/A Switch", this::setMA);

        _widget = new BoardWidget(_pendingCommands);
        add(_widget,
            new LayoutSpec("y", 1,
                           "height", 1,
                           "width", 3));
    }

    /** Response to "Quit" button click. */
    private void quit(String dummy) {
        _pendingCommands.offer("quit");
    }

    /** Response to "New Game" button click.
     * @param dummy unused.*/
    private void newGame(String dummy) {
        _pendingCommands.offer("new");
    }

    /** Pattern describing the 'seed' command's arguments. */
    private static final Pattern SEED_PATN =
        Pattern.compile("\\s*(-?\\d{1,18})\\s*$");

    /** Pattern describing the "Switch M/A" command's arguments. */
    private static final Pattern MA_PATN =
        Pattern.compile("(manual|auto)\\s+(black|white)");

    /** Response to "Seed" button click. */
    private void newSeed(String dummy) {
        String response =
                getTextInput("Enter new random seed.",
                        "New seed",  "plain", "");
        if (response != null) {
            Matcher mat = SEED_PATN.matcher(response);
            if (mat.matches()) {
                _pendingCommands.offer(String.format("seed %s", mat.group(1)));
            } else {
                showMessage("Enter an integral seed value.", "Error", "error");
            }
        }
    }

    /** Response to "Switch M/A" command. */
    private void setMA(String dummy) {
        String response =
                getTextInput("Manual or Auto?",
                        "M/A", "plain", "")
                        + " " + getTextInput("Enter piece to switch.",
                        "Piece",  "plain", "");
        Matcher mat = MA_PATN.matcher(response);
        if (mat.matches()) {
            _pendingCommands.offer(response);
        } else {
            showMessage("Enter correct M/A and piece value.", "Error", "error");
        }

    }

    /** Return the next command from our widget, waiting for it as necessary.
     *  The BoardWidget uses _pendingCommands to queue up moves that it
     *  receives.  Thie class uses _pendingCommands to queue up commands that
     *  are generated by clicking on menu items. */
    String readCommand() {
        try {
            _widget.setMoveCollection(true);
            String cmnd = _pendingCommands.take();
            _widget.setMoveCollection(false);
            return cmnd;
        } catch (InterruptedException excp) {
            throw new Error("unexpected interrupt");
        }
    }

    @Override
    public void update(Board board) {
        _widget.update(board);
    }

    @Override
    public void reportError(String fmt, Object... args) {
        showMessage(String.format(fmt, args), "Amazons Error", "error");
    }

    @Override
    public void reportNote(String fmt, Object... args) {
        showMessage(String.format(fmt, args), "Amazons Message", "information");
    }

    @Override
    public void reportMove(Move unused) {
    }

    /** The board widget. */
    private BoardWidget _widget;

    /** Queue of pending commands resulting from menu clicks and moves on the
     *  board.  We use a blocking queue because the responses to clicks
     *  on the board and on menus happen in parallel to the methods that
     *  call readCommand, which therefore needs to wait for clicks to happen. */
    private ArrayBlockingQueue<String> _pendingCommands =
        new ArrayBlockingQueue<>(5);

}
