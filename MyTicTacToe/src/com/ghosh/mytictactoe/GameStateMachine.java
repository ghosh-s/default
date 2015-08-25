package com.ghosh.mytictactoe;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GameStateMachine {

    static final int BOXES = 9;

    static enum Box {
        BLANK, CROSS, CIRCLE
    }

    static enum Player {
        PLAYER1, PLAYER2
    }

    static enum GameStatus {
        ONGOING, PLAYER1_WINS, PLAYER2_WINS, DRAW

    }

    static enum WinningLine {

        TOP_HORZ(new int[] { 0, 1, 2 }), //
        MIDDLE_HORZ(new int[] { 3, 4, 5 }), //
        BOTTOM_HORZ(new int[] { 6, 7, 8 }), //

        LEFT_VERT(new int[] { 0, 3, 6 }), //
        MIDDLE_VERT(new int[] { 1, 4, 7 }), //
        RIGHT_VERT(new int[] { 2, 5, 8 }), //

        PRINCIPAL_DIAG(new int[] { 0, 4, 8 }), //
        OTHER_DIAG(new int[] { 2, 4, 6 })//
        ;

        private int[] boxes;

        WinningLine(final int[] boxes) {
            this.boxes = boxes;
        }

        int[] getBoxes() {
            return this.boxes;
        }
    }

    private final Player crossPlayer;

    private final Player circlePlayer;

    private final Box[] boxes;

    private Player turn;

    private GameStatus gameStatus;

    private WinningLine winningLine;

    // Getters
    public Box getBox(final int i) {
        if (i < 0 || i >= BOXES)
            throw new IllegalArgumentException(
                    "getBox() called with invalid argument.");
        return this.boxes[i];
    }

    public GameStatus getGameStatus() {
        return this.gameStatus;
    }

    public WinningLine getWinningLine() {
        return this.winningLine;
    }

    public Player getTurn() {
        return this.turn;
    }

    public Player getCrossPlayer() {
        return this.crossPlayer;
    }

    // Constructor
    GameStateMachine(final Player crossPlayer) {
        this.boxes = new Box[BOXES];
        for (int i = 0; i < BOXES; i++)
            this.boxes[i] = Box.BLANK;
        this.crossPlayer = crossPlayer;
        this.circlePlayer = this.crossPlayer == Player.PLAYER1 ? Player.PLAYER2
                : Player.PLAYER1;
        this.turn = crossPlayer;
        this.gameStatus = GameStatus.ONGOING;
        this.winningLine = null;
    }

    public void processMove(final int box, final SharedPreferences sp) {
        // If game has ended, do nothing
        if (this.gameStatus != GameStatus.ONGOING)
            return;

        // Validate input, and update boxes
        if (box < 0 || box >= BOXES)
            return;
        this.boxes[box] = this.turn == this.crossPlayer ? Box.CROSS
                : Box.CIRCLE;

        // Check for a winning line
        this.checkWinningLine(WinningLine.TOP_HORZ);
        this.checkWinningLine(WinningLine.MIDDLE_HORZ);
        this.checkWinningLine(WinningLine.BOTTOM_HORZ);
        this.checkWinningLine(WinningLine.LEFT_VERT);
        this.checkWinningLine(WinningLine.MIDDLE_VERT);
        this.checkWinningLine(WinningLine.RIGHT_VERT);
        this.checkWinningLine(WinningLine.PRINCIPAL_DIAG);
        this.checkWinningLine(WinningLine.OTHER_DIAG);

        // If a player has won
        if (this.gameStatus == GameStatus.PLAYER1_WINS
                || this.gameStatus == GameStatus.PLAYER2_WINS) {

            // Update scores
            final int[] scores = readScores(sp);
            if (this.gameStatus == GameStatus.PLAYER1_WINS)
                scores[0]++;
            else if (this.gameStatus == GameStatus.PLAYER2_WINS)
                scores[1]++;
            writeScores(sp, scores[0], scores[1], scores[2]);

        } else {
            // Either a draw has happened, or the game must continue
            boolean blankBoxFound = false;
            for (int i = 0; i < BOXES; i++)
                if (this.boxes[i] == Box.BLANK) {
                    blankBoxFound = true;
                    break;
                }

            if (blankBoxFound)
                this.gameStatus = GameStatus.ONGOING;

            else {
                this.gameStatus = GameStatus.DRAW;

                // Update scores
                final int[] scores = readScores(sp);
                scores[2]++;
                writeScores(sp, scores[0], scores[1], scores[2]);
            }
        }

        if (this.gameStatus == GameStatus.ONGOING)
            this.turn = this.turn == Player.PLAYER1 ? Player.PLAYER2
                    : Player.PLAYER1;
    }

    // Returns true if a WinningLine is found
    private boolean checkWinningLine(final WinningLine wl) {
        if (wl == null)
            return false;

        final int[] boxes = wl.getBoxes();
        final int first = boxes[0];
        final int second = boxes[1];
        final int third = boxes[2];

        final Player result = this.allThreeBoxesSame(first, second, third);
        if (result != null) {
            this.winningLine = wl;
            if (result == Player.PLAYER1) {
                this.gameStatus = GameStatus.PLAYER1_WINS;
                return true;
            } else if (result == Player.PLAYER2) {
                this.gameStatus = GameStatus.PLAYER2_WINS;
                return true;
            }
        }
        return false;
    }

    // If all 3 boxes are not the same, method return nulls
    // Otherwise, method returns either crossPlayer or circlePlayer
    private Player allThreeBoxesSame(final int first, final int second,
            final int third) {
        if (this.boxes[first] == this.boxes[second]
                && this.boxes[second] == this.boxes[third])
            if (this.boxes[first] == Box.CROSS)
                return this.crossPlayer;
            else if (this.boxes[first] == Box.CIRCLE)
                return this.circlePlayer;
        return null;
    }

    // Reads scores from a SharedPreferences object
    public static int[] readScores(final SharedPreferences sp) {
        if (sp == null)
            throw new NullPointerException("sp cannot be null!");
        final int p1Wins = sp.getInt("P1Wins", 0);
        final int p2Wins = sp.getInt("P2Wins", 0);
        final int draws = sp.getInt("Draws", 0);
        return new int[] { p1Wins, p2Wins, draws };
    }

    // Saves scores to a SharedPreferences object
    public static void writeScores(final SharedPreferences sp,
            final int p1Wins, final int p2Wins, final int draws) {
        if (sp == null)
            throw new NullPointerException("sp cannot be null!");
        final Editor editor = sp.edit();
        editor.putInt("P1Wins", p1Wins);
        editor.putInt("P2Wins", p2Wins);
        editor.putInt("Draws", draws);
        editor.apply();
    }
}
