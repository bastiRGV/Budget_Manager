package com.example.budgetmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FixedListAdapter extends ArrayAdapter<FixedExpense> {

    public FixedListAdapter(@NonNull Context context, ArrayList<FixedExpense> arrayList){

        super(context, 0, arrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View currentItemView = convertView;

        if (currentItemView == null){

            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_single_line_delete_button, parent, false);

        }

        FixedExpense currentPosition = getItem(position);

        TextView name = currentItemView.findViewById(R.id.fixed_list_item_name);
        name.setText(currentPosition.getName());

        TextView amount = currentItemView.findViewById(R.id.fixed_list_item_amount);
        amount.setText(String.valueOf(currentPosition.getAmount()) + "€");

        return currentItemView;

    }

}
