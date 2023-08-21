package com.example.budgetmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FixedListAdapter extends ArrayAdapter<FixedExpense> {

    public FixedListAdapter(@NonNull Context context, ArrayList<FixedExpense> arrayList){

        super(context, 0, arrayList);

    }

    @SuppressLint("SetTextI18n")
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
        amount.setText(currentPosition.getAmount() + "€");

        ImageButton delete = currentItemView.findViewById(R.id.fixed_list_delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText("deleted");
            }
        });


        return currentItemView;

    }

}
