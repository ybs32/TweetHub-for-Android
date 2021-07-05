package com.ybsystem.tweethub.adapters.array;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.models.entities.Column;

public class ColumnArrayAdapter extends ArrayAdapter<Column> {

    public ColumnArrayAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Inflate
        View cv = convertView;
        if (cv == null) {
            cv = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_common, parent, false);
        }

        // Find
        ImageView image = cv.findViewById(R.id.image_item);
        TextView text = cv.findViewById(R.id.text_item);

        // Set icon on boot column
        Column column = getItem(position);
        if (column.isBootColumn()) {
            image.setImageResource(R.drawable.ic_check);
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.INVISIBLE);
        }

        // Set column name
        text.setText(column.getName());

        return cv;
    }

}
