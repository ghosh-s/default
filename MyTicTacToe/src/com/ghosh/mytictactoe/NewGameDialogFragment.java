package com.ghosh.mytictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class NewGameDialogFragment extends DialogFragment {

    // Listener Interface
    interface NewGameDialogListener {
        public void onNewGameDialogItemClick(DialogFragment dialog, int which);
    }

    private NewGameDialogListener listener;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (NewGameDialogListener) activity;
        } catch (final ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NewGameDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                this.getActivity());
        builder.setCancelable(true);
        builder.setTitle(R.string.new_game_title_text);
        builder.setItems(new String[] { "Player 1", "Player 2" },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                            final int which) {
                        NewGameDialogFragment.this.listener
                                .onNewGameDialogItemClick(
                                        NewGameDialogFragment.this, which);
                    }
                });
        builder.setNegativeButton(R.string.button_cancel, null);
        return builder.create();
    }
}
