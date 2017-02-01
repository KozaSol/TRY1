package com.jobsearchtry.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;

import java.util.ArrayList;
import java.util.Locale;

public class ResponsesAutoCompleteAdapter extends BaseAdapter {
    private ArrayList<String> suggestionsList = new ArrayList<>();
    private ArrayList<String> arraylist;
    private Activity activity;

    public ResponsesAutoCompleteAdapter(Activity a,
                                        ArrayList<String> suggestionsList) {
        activity = a;
        this.suggestionsList = suggestionsList;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(suggestionsList);
    }

    public int getCount() {
        return suggestionsList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView keyword;
        ImageView suggimage;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.autocomplete_layout, parent,
                    false);
            holder = new ViewHolder();
            holder.keyword = (TextView) convertView
                    .findViewById(R.id.spinneritemqualification);
            holder.suggimage = (ImageView) convertView.findViewById(R.id.suggimage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if ((GlobalData.responsesuggestion.size() > 0) && GlobalData.responsesuggestion.contains(suggestionsList.get(position))) {
            holder.suggimage.setImageResource(R.drawable.ic_history);
        } else {
            holder.suggimage.setImageResource(android.R.drawable.ic_menu_search);
        }
        holder.keyword.setText(suggestionsList.get(position));
        return convertView;
    }

    public void clearallitems() {
        suggestionsList.clear();
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        suggestionsList.clear();
        if (charText.length() == 0) {
            if (arraylist.size() != 0) {
                suggestionsList.addAll(arraylist);
            }
        } else {
            if (arraylist.size() != 0) {
                for (String wp : arraylist) {
                    if (wp.toUpperCase().startsWith(charText.toUpperCase())) {
                        suggestionsList.add(wp);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}


