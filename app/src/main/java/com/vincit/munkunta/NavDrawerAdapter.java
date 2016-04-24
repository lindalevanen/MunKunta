package com.vincit.munkunta;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Linda on 12/04/16.
 */
public class NavDrawerAdapter extends ArrayAdapter<Municipality>
{
    private final Context context;
    private final int layoutResourceId;
    private Municipality data[] = null;

    public NavDrawerAdapter(Context context, int layoutResourceId, Municipality [] data)
    {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Municipality muni = data[position];

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View v = inflater.inflate(layoutResourceId, parent, false);

        TextView textView = (TextView) v.findViewById(R.id.navDrawerTextView);

        textView.setText(muni.getName());

        return v;
    }
}