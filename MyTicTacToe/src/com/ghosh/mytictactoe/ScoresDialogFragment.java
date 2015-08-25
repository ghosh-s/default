package com.ghosh.mytictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ScoresDialogFragment extends DialogFragment {

    private int[] readScores() {
        final SharedPreferences sp = this.getActivity().getPreferences(
                Context.MODE_PRIVATE);
        return GameStateMachine.readScores(sp);
    }

    private void writeScores(final int p1Wins, final int p2Wins, final int draws) {
        final SharedPreferences sp = this.getActivity().getPreferences(
                Context.MODE_PRIVATE);
        GameStateMachine.writeScores(sp, p1Wins, p2Wins, draws);
    }

    private void clearScores() {
        this.writeScores(0, 0, 0);
    }

    private String makeScoresString() {
        final int[] scores = this.readScores();
        final int p1Wins = scores[0];
        final int p2Wins = scores[1];
        final int draws = scores[2];
        final String retVal = "Player 1: " + p1Wins + "\nPlayer 2: " + p2Wins
                + "\nDraws: " + draws;
        return retVal;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                this.getActivity());
        builder.setCancelable(true);
        builder.setTitle(R.string.scores_title_text);
        builder.setMessage(this.makeScoresString());
        builder.setNegativeButton(R.string.button_close, null);
        builder.setNeutralButton(R.string.button_reset_scores,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                            final int which) {
                        ScoresDialogFragment.this.clearScores();
                    }
                });
        return builder.create();
    }
}
