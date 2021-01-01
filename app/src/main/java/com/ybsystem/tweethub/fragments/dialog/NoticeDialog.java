package com.ybsystem.tweethub.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.utils.CalcUtils;

public class NoticeDialog extends DialogFragment {

    private DialogInterface.OnClickListener mPositiveClickListener;

    public NoticeDialog newInstance(String title, String description) {
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);
        bundle.putString("DESCRIPTION", description);
        setArguments(bundle);
        return this;
    }

    public NoticeDialog newInstance(String description) {
        Bundle bundle = new Bundle();
        bundle.putString("DESCRIPTION", description);
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
        String title = bundle.getString("TITLE");
        String description = bundle.getString("DESCRIPTION");
        TextView titleText = view.findViewById(R.id.text_title);
        TextView descText = view.findViewById(R.id.text_description);

        if (title == null) {
            titleText.setVisibility(View.GONE);
            descText.setText(description);
            descText.setTextSize(CalcUtils.convertDp2Sp(18));
            descText.setGravity(Gravity.CENTER);
        } else {
            titleText.setText(title);
            descText.setText(description);
        }

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
