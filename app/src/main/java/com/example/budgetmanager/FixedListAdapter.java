package com.example.budgetmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;


//Adapter, der das Listview der Fixausgaben der Settingspage befüllt
//für jeden Eintrag wird das Layout list_layout_single_line_delete_button geladen, die übergebenen Daten aus der Arrayliste in die
//jeweiligen felder geschrieben und die funktionalität des löschen knopfes hinzugefügt

public class FixedListAdapter extends ArrayAdapter<FixedExpense> {

    private static SharedPreferences sharedPreferences;
    private final Context context;

    //formater, um floats auf zwei nachkommastellen zu runden
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");


    public FixedListAdapter(@NonNull Context context, ArrayList<FixedExpense> arrayList){

        super(context, 0, arrayList);
        this.context = context;

    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View currentItemView = convertView;

        if (currentItemView == null){

            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_single_line_delete_button, parent, false);

        }

        //initialisiert sharedReferences um Persistente Daten zu lesen
        sharedPreferences = context.getSharedPreferences("prefBudgetManager", 0);

        FixedExpense currentPosition = getItem(position);

        TextView name = currentItemView.findViewById(R.id.fixed_list_item_name);
        name.setText(currentPosition.getName());

        TextView amount = currentItemView.findViewById(R.id.fixed_list_item_amount);
        amount.setText(decimalFormat.format(currentPosition.getAmount()) + sharedPreferences.getString("Currency", null));

        ImageButton delete = currentItemView.findViewById(R.id.fixed_list_delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText(currentPosition.getId() + " deleted");
            }
        });


        return currentItemView;

    }

}
