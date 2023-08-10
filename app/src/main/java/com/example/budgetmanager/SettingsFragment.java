package com.example.budgetmanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsFragment extends Fragment {

    String[] dropdownCurrency = new String[] {"€  Euro", "$  Dollar", "￡  Pfund"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Spinner dropdownMenu = view.findViewById(R.id.currency_dropdown);

        //läd items aus Array in das Dropdown Menü
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dropdownCurrency);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(adapter);
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

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

        return view;
    }




}