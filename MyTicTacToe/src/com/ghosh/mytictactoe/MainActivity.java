package com.ghosh.mytictactoe;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ghosh.mytictactoe.GameStateMachine.Box;
import com.ghosh.mytictactoe.GameStateMachine.GameStatus;
import com.ghosh.mytictactoe.GameStateMachine.Player;
import com.ghosh.mytictactoe.NewGameDialogFragment.NewGameDialogListener;

public class MainActivity extends Activity implements NewGameDialogListener {

    // Widget references
    private TextView playersText;
    private TextView statusText;
    private ImageButton[] ticTacToeButtons;

    // Game State Machine
    private GameStateMachine gameStateMachine;

    // SharedPrefs
    private SharedPreferences sp;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // Get SharedPrefs object
        this.sp = this.getPreferences(MODE_PRIVATE);

        // Get references to widgets
        this.playersText = (TextView) this.findViewById(R.id.playersText);
        this.statusText = (TextView) this.findViewById(R.id.statusText);
        this.ticTacToeButtons = new ImageButton[GameStateMachine.BOXES];
        this.ticTacToeButtons[0] = (ImageButton) this
                .findViewById(R.id.button0);
        this.ticTacToeButtons[1] = (ImageButton) this
                .findViewById(R.id.button1);
        this.ticTacToeButtons[2] = (ImageButton) this
                .findViewById(R.id.button2);
        this.ticTacToeButtons[3] = (ImageButton) this
                .findViewById(R.id.button3);
        this.ticTacToeButtons[4] = (ImageButton) this
                .findViewById(R.id.button4);
        this.ticTacToeButtons[5] = (ImageButton) this
                .findViewById(R.id.button5);
        this.ticTacToeButtons[6] = (ImageButton) this
                .findViewById(R.id.button6);
        this.ticTacToeButtons[7] = (ImageButton) this
                .findViewById(R.id.button7);
        this.ticTacToeButtons[8] = (ImageButton) this
                .findViewById(R.id.button8);

        // Create Listeners
        final OnClickListener buttonListener = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ImageButton ibutton = (ImageButton) v;
                final int button = Integer.valueOf(ibutton.getTag().toString());
                // Process and update GameState
                MainActivity.this.gameStateMachine.processMove(button,
                        MainActivity.this.sp);
                // Update views
                MainActivity.this
                        .updateViews(MainActivity.this.gameStateMachine);
            }
        };

        // Set Listeners
        for (int i = 0; i < GameStateMachine.BOXES; i++)
            this.ticTacToeButtons[i].setOnClickListener(buttonListener);

        // Restore the retained GSM, if available
        final Object obj = this.getLastNonConfigurationInstance();
        if (obj != null && obj.getClass() == GameStateMachine.class) {
            final GameStateMachine gsm = (GameStateMachine) obj;
            this.gameStateMachine = gsm;
            this.updateViews(this.gameStateMachine);
        } else
            this.beginNewGame(this.gameStateMachine == null ? Player.PLAYER1
                    : this.gameStateMachine.getTurn());
    }

    void beginNewGame(final Player crossPlayer) {
        this.gameStateMachine = new GameStateMachine(crossPlayer);
        this.updateViews(this.gameStateMachine);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // Return GSM as the object to be retained
        return this.gameStateMachine;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        if (id == R.id.action_new_game)
            this.showNewGameDialog();
        if (id == R.id.action_scores)
            this.showScoresDialog();
        return super.onOptionsItemSelected(item);
    }

    private void showScoresDialog() {
        final ScoresDialogFragment fragment = new ScoresDialogFragment();
        fragment.show(this.getFragmentManager(), "ScoresDialog");
    }

    private void showNewGameDialog() {
        final NewGameDialogFragment fragment = new NewGameDialogFragment();
        fragment.show(this.getFragmentManager(), "NewGameDialog");
    }

    @Override
    public void onNewGameDialogItemClick(final DialogFragment dialog,
            final int which) {
        this.beginNewGame(which == 0 ? Player.PLAYER1 : Player.PLAYER2);
    }

    // This method updates the Views to match the GameStateMachine
    private void updateViews(final GameStateMachine gs) {
        String playersTextString = "";
        if (gs.getCrossPlayer() == Player.PLAYER1)
            playersTextString = "Player 1: X    Player 2: O";
        else if (gs.getCrossPlayer() == Player.PLAYER2)
            playersTextString = "Player 1: O    Player 2: X";
        this.playersText.setText(playersTextString);

        this.statusText.setText("");

        for (int i = 0; i < GameStateMachine.BOXES; i++) {
            int imageResource = R.drawable.button_blank;
            if (gs.getBox(i) == Box.CROSS)
                imageResource = R.drawable.button_cross;
            else if (gs.getBox(i) == Box.CIRCLE)
                imageResource = R.drawable.button_circle;
            this.ticTacToeButtons[i].setImageResource(imageResource);
            this.ticTacToeButtons[i].setClickable(gs.getBox(i) == Box.BLANK);
            this.ticTacToeButtons[i].getBackground().clearColorFilter();
        }

        if (gs.getGameStatus() == GameStatus.PLAYER1_WINS) {

            this.statusText.setText(R.string.status_player_1_wins);
            for (int i = 0; i < GameStateMachine.BOXES; i++)
                this.ticTacToeButtons[i].setClickable(false);
            final int[] boxes = this.gameStateMachine.getWinningLine()
                    .getBoxes();
            for (int i = 0; i < 3; i++)
                this.ticTacToeButtons[boxes[i]].getBackground().setColorFilter(
                        Color.parseColor("#00ff00"), PorterDuff.Mode.DARKEN);

        } else if (gs.getGameStatus() == GameStatus.PLAYER2_WINS) {

            this.statusText.setText(R.string.status_player_2_wins);
            for (int i = 0; i < GameStateMachine.BOXES; i++)
                this.ticTacToeButtons[i].setClickable(false);
            final int[] boxes = this.gameStateMachine.getWinningLine()
                    .getBoxes();
            for (int i = 0; i < 3; i++)
                this.ticTacToeButtons[boxes[i]].getBackground().setColorFilter(
                        Color.parseColor("#00ff00"), PorterDuff.Mode.DARKEN);

        } else if (gs.getGameStatus() == GameStatus.DRAW) {

            this.statusText.setText(R.string.status_draw);
            for (int i = 0; i < GameStateMachine.BOXES; i++)
                this.ticTacToeButtons[i].setClickable(false);

        } else if (gs.getTurn() == Player.PLAYER1)
            this.statusText.setText(R.string.status_player_1s_turn);

        else if (gs.getTurn() == Player.PLAYER2)
            this.statusText.setText(R.string.status_player_2s_turn);
    }

}
