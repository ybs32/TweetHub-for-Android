package com.ybsystem.tweethub.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

public class ListDialog extends DialogFragment {

    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    public ListDialog newInstance(String[] items) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("ITEMS", items);
        setArguments(bundle);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create
        ListView listView = new ListView(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (getContext(), android.R.layout.simple_list_item_1);

        // Add item
        String[] items = getArguments().getStringArray("ITEMS");
        for (String item : items) {
            adapter.add(item);
        }

        listView.setOnItemClickListener(mOnItemClickListener);
        listView.setOnItemLongClickListener(mOnItemLongClickListener);
        listView.setDivider(null);
        listView.setAdapter(adapter);

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(listView);

        return builder.create();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

}
