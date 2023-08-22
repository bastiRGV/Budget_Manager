package com.example.budgetmanager;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    String[] dropdownCurrency = new String[] {"€  Euro", "$  Dollar", "￡  Pfund"};

    String fixedIdentifier = "";
    float fixedAmount = 0;
    String name = "";
    float budget = 0;

    private ListView listFixedInput;
    private Button fixedInputButton;
    private Button nameButton;
    private Button budgetButton;
    private EditText fixedInputIdentifier;
    private EditText fixedInputAmount;
    private EditText nameInput;
    private EditText budgetInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Spinner dropdownMenuSettings = view.findViewById(R.id.currency_dropdown);

        //läd items aus Array in das Dropdown Menü
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dropdownCurrency);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenuSettings.setAdapter(currencyAdapter);

        //initialisiert sharedReferences um Persistente Daten zu speichern
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("prefBudgetManager", 0);
        SharedPreferences.Editor referenceEditor = sharedPreferences.edit();

        //änderung Währung, je nachdem, welcher Menüpunkt im Dropdown ausgewählt wurde
        dropdownMenuSettings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView,View view,int position, long id){

                switch (position){
                    case 0:
                        //Setzt Appwährung in den sharedPreferences zu Euro
                        referenceEditor.putString("Currency", "€");
                        referenceEditor.commit();
                        break;
                    case 1:
                        //Setzt Appwährung in den sharedPreferences zu Dollar
                        referenceEditor.putString("Currency", "$");
                        referenceEditor.commit();
                        break;
                    case 2:
                        //Setzt Appwährung in den sharedPreferences zu Pfund
                        referenceEditor.putString("Currency", "£");
                        referenceEditor.commit();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
                //default Euro
                return;
            }

        });



        //Eingabe Name in sharedPrefferences
        nameButton = view.findViewById(R.id.name_button);
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameInput = view.findViewById(R.id.name_input);

                //check, ob Feld leer
                if(TextUtils.isEmpty(nameInput.getText().toString())){

                    Toast.makeText(getActivity().getBaseContext(), "Bitte Feld ausfüllen", Toast.LENGTH_SHORT).show();

                }else{

                    referenceEditor.putString("Username", nameInput.getText().toString());
                    referenceEditor.commit();
                    nameInput.setText("");

                }

            }
        });




        //Eingabe Budget in sharedPrefferences
        budgetButton = view.findViewById(R.id.budget_button);
        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                budgetInput = view.findViewById(R.id.budget_input);

                //check, ob Feld leer
                if(TextUtils.isEmpty(budgetInput.getText().toString())){

                    Toast.makeText(getActivity().getBaseContext(), "Bitte Feld ausfüllen", Toast.LENGTH_SHORT).show();

                }else{

                    referenceEditor.putFloat("Budget", Float.valueOf(budgetInput.getText().toString()));
                    referenceEditor.commit();
                    budgetInput.setText("");

                }

            }
        });


        //Test Liste
        ArrayList <FixedExpense> fixedInput = new ArrayList<FixedExpense>();
        fixedInput.add(new FixedExpense(1, "Miete", 300));
        fixedInput.add(new FixedExpense(2, "Auto", 70));

        FixedListAdapter fixedListAdapter = new FixedListAdapter(getContext(), fixedInput);
        listFixedInput = view.findViewById(R.id.list_fixed_input);
        listFixedInput.setAdapter(fixedListAdapter);

        fixedInputButton = view.findViewById(R.id.fixed_button);

        //Eingabe Fixkosten
        fixedInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                fixedInputIdentifier = view.findViewById(R.id.fixed_name_input);
                fixedInputAmount = view.findViewById(R.id.fixed_amount_input);

                //check,ob alle felder ausgefüllt sind
                if(TextUtils.isEmpty(fixedInputIdentifier.getText().toString()) || TextUtils.isEmpty(fixedInputAmount.getText().toString())){

                    Toast.makeText(getActivity().getBaseContext(), "Bitte alle benötigten Felder ausfüllen", Toast.LENGTH_SHORT).show();

                }else{

                    //liest Eingabe name aus Namensfeld und löscht eingabe
                    fixedIdentifier = fixedInputIdentifier.getText().toString();
                    fixedInputIdentifier.setText("");

                    //liest Betrag aus Betragsfeld und löscht eingabe
                    fixedAmount = Float.valueOf(fixedInputAmount.getText().toString());
                    fixedInputAmount.setText("");

                    Toast.makeText(getActivity().getBaseContext(), fixedIdentifier + " " + fixedAmount, Toast.LENGTH_SHORT).show();

                }

            }
        });

        return view;
    }

}