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


//Adapter, der das Listview der Monatszusammenfassungen befüllt
//für jeden Eintrag wird das Layout list_layout_double_line geladen, die übergebenen Daten aus der Arrayliste in die
//jeweiligen felder geschrieben
//Unterschied zu BudgetListAdapter ist der fehlende Löschenknopf
public class SummaryListAdapter extends ArrayAdapter<Expense> {

    public SummaryListAdapter(@NonNull Context context, ArrayList<Expense> arraylist){

        super(context, 0, arraylist);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View currentItemView = convertView;

        if (currentItemView == null){

            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_double_line, parent, false);

        }

        Expense currentPosition = getItem(position);

        TextView name = currentItemView.findViewById(R.id.summary_list_item_name);
        name.setText(currentPosition.getName());

        TextView category = currentItemView.findViewById(R.id.summary_list_item_category);
        category.setText(currentPosition.getCategory());

        TextView amount = currentItemView.findViewById(R.id.summary_list_item_amount);
        amount.setText(currentPosition.getAmount() + "€");

        TextView date = currentItemView.findViewById(R.id.summary_list_item_date);
        date.setText(currentPosition.getDate());

        return currentItemView;

    }

}
