package com.ybsystem.tweetmate.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ybsystem.tweetmate.R;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class ConfirmDialog extends DialogFragment {

    private DialogInterface.OnClickListener mPositiveClickListener;
    private DialogInterface.OnClickListener mNegativeClickListener;

    public ConfirmDialog newInstance(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("MESSAGE", message);
        setArguments(bundle);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_confirm, null);

        // Set message
        TextView messageText = view.findViewById(R.id.text_message);
        messageText.setText(getArguments().getString("MESSAGE"));

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(STR_OK, mPositiveClickListener);
        builder.setNegativeButton(STR_CANCEL, mNegativeClickListener);
        AlertDialog dialog = builder.create();

        // Adjust button position to center
        dialog.show();
        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);

        return dialog;
    }

    public void setOnPositiveClickListener(DialogInterface.OnClickListener listener) {
        this.mPositiveClickListener = listener;
    }

    public void setOnNegativeClickListener(DialogInterface.OnClickListener listener) {
        this.mNegativeClickListener = listener;
    }

}
