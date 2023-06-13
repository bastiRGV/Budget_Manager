package com.example.budgetmanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ExportFragment extends Fragment {

    private ListView listExport;
    private Button exportButton;
    String[] export = {"May 2023", "April 2023", "June 2023", "July 2023", "August 2023"};
    String[] exportItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_export, container, false);

        //befüllen Exportliste
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, export);
        listExport=view.findViewById(R.id.list_export);
        listExport.setAdapter(adapter);

        exportButton = view.findViewById(R.id.export_button);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selected = "selected: \n";

                //Exportliste wird durchlaufen und ausgewählte Items werden gespeichert
                for(int i = 0; i < listExport.getCount(); i++){

                    if(listExport.isItemChecked(i)){
                        selected += listExport.getItemAtPosition(i) + "\n";
                    }

                }

                Toast.makeText(getActivity().getBaseContext(), selected, Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }
}