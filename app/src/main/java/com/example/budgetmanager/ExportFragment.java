package com.example.budgetmanager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ExportFragment extends Fragment {

    private ListView listExport;
    private Button exportButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_export, container, false);


        //erstellt Monatsliste
        ArrayList<String> export = new ArrayList<String>();
        export = getExportList();

        //befüllen Exportliste
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, export);
        listExport=view.findViewById(R.id.list_export);
        listExport.setAdapter(adapter);

        exportButton = view.findViewById(R.id.export_button);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selected = "selected: \n";
                String file;

                //Exportliste wird durchlaufen und ausgewählte Items werden gespeichert
                for(int i = 0; i < listExport.getCount(); i++){

                    if(listExport.isItemChecked(i)){

                        //stellt zugehörigen Dateinamen wieder her
                        file = listExport.getItemAtPosition(i).toString();
                        file = file.replace(" ", "_");
                        file = file + ".json";
                        selected += file + "\n";
                    }

                }

                Toast.makeText(getActivity().getBaseContext(), selected, Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }


/**-------------------------------------------------------------------------------------**/

    //liest Dateien aus dem Appspeicher und passt den Namen an
    public ArrayList<String> getExportList(){

        ArrayList<String> exportList = new ArrayList<String>();

        File fileList[] = getContext().getFilesDir().listFiles();
        for(int i = 0; i < fileList.length; i++){

            //Filtert die Fixkostendatei aus den Ergebnissen
            if(!(fileList[i].getName()).equals("fixedCosts.json")){

                //Anpassung des Namens der Datei auf den Anzeigenamen
                String name = fileList[i].getName();
                name = name.replace("_", " ");
                name = name.replace(".json", "");
                exportList.add(name);

            }

        }

        return exportList;

    }
}