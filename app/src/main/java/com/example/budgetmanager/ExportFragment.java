package com.example.budgetmanager;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

        ArrayList<String> checkedItems = new ArrayList<>();

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

                        file = listExport.getItemAtPosition(i).toString();
                        file = file.replace(" ", "_");
                        file = file + ".json";
                        checkedItems.add(file);

                    }

                }

                try {
                    exportSelectedItems(checkedItems);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                checkedItems.clear();

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


/**------------------------------------------------------------------------------------**/

    //durchläuft Liste ausgewählter Items und exportiert die zugehörigen Dateien als CSV
    public void exportSelectedItems(ArrayList<String> selectedItems) throws IOException {

        for(int i = 0; i < selectedItems.size(); i++){

            String file = selectedItems.get(i);
            ArrayList<Expense> items = new ArrayList<>();
            items = readSelectedExpenseFile(file);

            //legt CSV datei an
            String target = file.replace(".json", ".csv");
            FileOutputStream fOut = getActivity().openFileOutput(target, Context.MODE_PRIVATE);

            //schreibt Daten in die CSV Datei
            //für Downloadordner wird Berechtigung zum Schreiben in den externen speicher benötigt
            //Berechtigung gesetzt in manifest datei
            File downloadFolder =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File fileExport = new File(downloadFolder, target);
            FileWriter writer = new FileWriter(fileExport);
            writer.write("Name,Category,Date,Amount\n");

            for(int j = 0; j < items.size(); j++){

                String name = items.get(j).getName();
                String category = items.get(j).getCategory();
                String date = items.get(j).getDate();
                String amount = Float.toString(items.get(j).getAmount());

                writer.write(name + "," + category + "," + date + "," + amount + "\n");

            }

            writer.close();

        }

    }


    /**--------------------------------------------------------------------------------**/



    //Liest Arraylist Expenses aus JSON Datei für ausgewählten Monat
    public ArrayList<Expense> readSelectedExpenseFile(String file) throws IOException {

        String returnString = "";

        InputStream inputStream = getContext().openFileInput(file);

        if ( inputStream != null ) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append("\n").append(receiveString);
            }

            inputStream.close();
            returnString = stringBuilder.toString();
        }

        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<Expense>>(){}.getType();

        ArrayList<Expense> list = gson.fromJson(returnString, type);

        return list;

    }


}