package com.example.budgetmanager;

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

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;



//Adapter, der das Listview der Homepage befüllt
//für jeden Eintrag wird das Layout list_layout_double_line_delete_button geladen, die übergebenen Daten aus der Arrayliste in die
//jeweiligen felder geschrieben und die funktionalität des löschen knopfes hinzugefügt

public class BudgetListAdapter extends ArrayAdapter<Expense> {

    private static SharedPreferences sharedPreferences;
    private final Context context;

    //formater, um floats auf zwei nachkommastellen zu runden
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");


    public BudgetListAdapter(@NonNull Context context, ArrayList<Expense> arraylist){

        super(context, 0, arraylist);
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View currentItemView = convertView;

        if (currentItemView == null){

            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_double_line_delete_button, parent, false);

        }

        //initialisiert sharedReferences um Persistente Daten zu lesen
        sharedPreferences = context.getSharedPreferences("prefBudgetManager", 0);

        Expense currentPosition = getItem(position);

        TextView name = currentItemView.findViewById(R.id.budget_list_item_name);
        name.setText(currentPosition.getName());

        TextView category = currentItemView.findViewById(R.id.budget_list_item_category);
        category.setText(currentPosition.getCategory());

        TextView amount = currentItemView.findViewById(R.id.budget_list_item_amount);
        amount.setText(decimalFormat.format(currentPosition.getAmount()) + sharedPreferences.getString("Currency", null));

        TextView date = currentItemView.findViewById(R.id.budget_list_item_date);
        date.setText(currentPosition.getDate());

        ImageButton delete = currentItemView.findViewById(R.id.budget_list_delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HomeFragment.getInstance().deleteItem(currentPosition.getId());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return currentItemView;

    }

}
