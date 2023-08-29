package com.example.budgetmanager;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    String[] dropdownCurrency = new String[] {"€  Euro", "$  Dollar", "￡  Pfund"};

    String fixedIdentifier = "";
    float fixedAmount = 0;

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

                    //ruft function in Mainactivity zum ändern des Nutzernamens auf
                    ((MainActivity)getActivity()).setUsername();

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
        fixedInput.add(new FixedExpense(1, "Miete", 300.99F));
        fixedInput.add(new FixedExpense(2, "Auto", 70.99F));
        fixedInput.add(new FixedExpense(3, "Zeug", 20.99F));

        listFixedInput = view.findViewById(R.id.list_fixed_input);

        setListData(fixedInput);

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

                    //neues FixedExpense objekt in die Arraylist eintragen
                    fixedInput.add(new FixedExpense(2, fixedIdentifier, fixedAmount));

                    //Schreibt Eingegebene Daten in Datei
                    try {
                        writeFixedInputFile(fixedInput);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    //aktualisiert Liste
                    setListData(fixedInput);

                }

            }
        });

        return view;
    }


/**--------------------------------------------------------------------------------------------**/

    private void setListData(ArrayList<FixedExpense> fixedInput) {


        try {
            writeFixedInputFile(fixedInput);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        FixedListAdapter fixedListAdapter = new FixedListAdapter(getContext(), fixedInput);
        listFixedInput.setAdapter(fixedListAdapter);

    }


/**---------------------------------------------------------------------------------------------**/

    //Schreibt übergeben Arraylist in die angegebene JSON Datei
    public void writeFixedInputFile(ArrayList<FixedExpense> arrayList) throws JSONException {

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FixedExpense>>() {}.getType();
        JSONArray jsonArray = new JSONArray(gson.toJson(arrayList,type));

        try {
            String filePath = getContext().getFilesDir() + "/" + "fixedCosts.json";
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            writer.write(jsonArray.toString(4));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}