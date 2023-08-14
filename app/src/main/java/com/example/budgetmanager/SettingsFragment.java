package com.example.budgetmanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    String[] dropdownCurrency = new String[] {"€  Euro", "$  Dollar", "￡  Pfund"};
    String[] fixedInput = new String[] {"Miete: 900€", "Auto: 200€"};

    private ListView listFixedInput;

    private Button fixedInputButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Spinner dropdownMenuSettings = view.findViewById(R.id.currency_dropdown);

        //läd items aus Array in das Dropdown Menü
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dropdownCurrency);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenuSettings.setAdapter(currencyAdapter);

        //änderung Währung, je nachdem, welcher Menüpunkt im Dropdown ausgewählt wurde
        dropdownMenuSettings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView,View view,int position, long id){

                switch (position){
                    case 0:
                        //Euro
                        break;
                    case 1:
                        //Dollar
                        break;
                    case 2:
                        //Pfund
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
                //default Euro
                return;
            }

        });

        //Test Fixkostenliste
        ArrayAdapter fixedAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , fixedInput);
        listFixedInput=view.findViewById(R.id.list_fixed_input);
        listFixedInput.setAdapter(fixedAdapter);

        fixedInputButton = view.findViewById(R.id.fixed_button);

        fixedInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Toast.makeText(getActivity().getBaseContext(), "input", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }




}