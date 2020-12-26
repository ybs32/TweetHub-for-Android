package com.ybsystem.tweethub.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ybsystem.tweethub.R;

public class NoticeDialog extends DialogFragment {

    private DialogInterface.OnClickListener mPositiveClickListener;

    public NoticeDialog newInstance(String title, String notice) {
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);
        bundle.putString("NOTICE", notice);
        setArguments(bundle);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_notice, null);

        // Create
        Bundle bundle = getArguments();
        TextView titleText = view.findViewById(R.id.text_title);
        TextView noticeText = view.findViewById(R.id.text_notice);
        titleText.setText(bundle.getString("TITLE"));
        noticeText.setText(bundle.getString("NOTICE"));

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton("OK", mPositiveClickListener);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public void setOnPositiveClickListener(DialogInterface.OnClickListener listener) {
        this.mPositiveClickListener = listener;
    }

}
