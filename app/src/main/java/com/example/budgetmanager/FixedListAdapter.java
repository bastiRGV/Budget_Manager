package com.example.budgetmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FixedListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mResource;

    public FixedListAdapter(Context context, int resource, ArrayList<String> list){
        super(context, resource, list);

        mContext = context;
        mResource = resource;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        String name = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView listItemOne = convertView.findViewById(R.id.list_item_one);
        TextView listItemTwo = convertView.findViewById(R.id.list_item_two);
        Button delete = convertView.findViewById(R.id.delete_button_settings);

        listItemOne.setText(name);

        return convertView;

    }

}
