package com.ybsystem.tweethub.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ChoiceDialog extends DialogFragment {

    private DialogInterface.OnClickListener mOnClickListener;

    public ChoiceDialog newInstance(String[] items, int choiceItem) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("ITEMS", items);
        bundle.putInt("CHOICE_ITEM", choiceItem);
        setArguments(bundle);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set choice item
        String[] items = getArguments().getStringArray("ITEMS");
        int choiceItem = getArguments().getInt("CHOICE_ITEM");
        builder.setSingleChoiceItems(items, choiceItem, mOnClickListener);

        return builder.create();
    }

    public void setOnItemClickListener(DialogInterface.OnClickListener listener) {
        this.mOnClickListener = listener;
    }

}
