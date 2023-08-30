package com.example.budgetmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    //Menüeinträge für Filter der Ausgaben auf Homepage
    String[] dropdownFilter = new String[] {"Datum",
                                            "Bezeichnung",
                                            "Betrag",
                                            "Kategorie"};



    //Farbcodes, welche für Graphen nutzbar sind (nicht aus color.xml in bibliothek importierbar)
    int graph_red = 0xFFE60049;
    int graph_blue = 0xFF0BB4FF;
    int graph_green = 0xFF50E991;
    int graph_orange = 0xFFFFA300;
    int graph_purple = 0xFF9B19F5;
    int graph_lightblue = 0xFFB3D4FF;
    int graph_gray = 0xFF989797;

    String[] kategorien = new String[]{ "Fixkosten",
                                        "Lebensmittel",
                                        "Gebrauchsgegenstände",
                                        "Unterhaltung",
                                        "Transport",
                                        "Sonstiges",
                                        "übriges Budget"};







    private ListView listHistory;
    private FrameLayout historyFragment;
    private PopupWindow popupWindowHistory;
    private LayoutInflater loadHistoryPopupWindow;
    private PieChart chartHistory;
    private ListView listHistoryPopup;
    private TextView textViewMonthHistory;
    private TextView textViewBudgetHistory;
    private TextView textViewRemainingBudgetHistory;
    private TextView textViewDifferenceHistory;
    private Spinner dropdownMenuHistory;


    //formater, um floats auf zwei nachkommastellen zu runden
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);


        //initialisiert sharedReferences um Persistente Daten zu lesen
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("prefBudgetManager", Context.MODE_PRIVATE);

        historyFragment = view.findViewById(R.id.history_fragment);

        //erstellt Liste von Monaten
        ArrayList <String> history;
        history = getHistoryList();


        //befüllen Historyliste
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , history);
        listHistory=view.findViewById(R.id.list_history);
        listHistory.setAdapter(adapter);

        //clicklistener für Listenitems
        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = (String) parent.getItemAtPosition(position);
                selectedItem = selectedItem.replace(" ", "_");


                //checkt, ob die zusammenfassung die des jetzigen oder eines zurückliegenden monats ist und wählt je nachdem die
                //die quelle für das Monatsbudget aus
                float budgetGesamt = 0F;
                if(getCurrentMonth("MMMM_yyyy").equals(sharedPreferences.getString("LastLogin", null))){

                    budgetGesamt = sharedPreferences.getFloat("Budget", 0.00f);

                }else{

                    budgetGesamt = sharedPreferences.getFloat(selectedItem, 0.00f);

                }


                String currency =  sharedPreferences.getString("Currency", null);


                //liest Daten aus der Monatsdatei
                ArrayList <Expense> expenses = new ArrayList<Expense>();
                try {
                    expenses = readSelectedExpenseFile(selectedItem+ ".json");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //daten aus der Arraylist in ein Objekt gespeichert
                ReturnValues returnValues = getValues(expenses, budgetGesamt, currency);

                loadPopupHistory(returnValues, expenses, selectedItem);
            }
        });

        return view;
    }


/**---------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------**/



    //läd das Popup fenster, welches die Monatszusammenfassung anzeigt
    private void loadPopupHistory(ReturnValues returnValues, ArrayList<Expense> arrayList, String month){

        loadHistoryPopupWindow = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) loadHistoryPopupWindow.inflate(R.layout.popup_summary, null);


        //läd oben erstellten Container in ein Popup Window
        //true lässt  es zu, das Fenster zu schliessen, wenn auserhalb des Fensters gedrückt wird
        popupWindowHistory = new PopupWindow(container, 900, 2000, true);
        popupWindowHistory.showAtLocation(historyFragment, Gravity.CENTER, 0, 0);



        //Variablen mit IDs verknüpfen Popupfenster Monatszusammenfassung
        textViewMonthHistory = popupWindowHistory.getContentView().findViewById(R.id.header_summary);
        chartHistory = popupWindowHistory.getContentView().findViewById(R.id.chart_summary);

        textViewBudgetHistory = popupWindowHistory.getContentView().findViewById(R.id.summary_budget);
        textViewRemainingBudgetHistory = popupWindowHistory.getContentView().findViewById(R.id.summary_remaining);
        textViewDifferenceHistory = popupWindowHistory.getContentView().findViewById(R.id.summary_difference);

        listHistoryPopup = popupWindowHistory.getContentView().findViewById(R.id.list_summary);

        //Standardsotierung nach Datum
        sortByDate(arrayList);

        setHistoryData(returnValues, arrayList, month);

    }


/**----------------------------------------------------------------------------------------**/


    public void setHistoryData(ReturnValues returnValues, ArrayList<Expense> arraylist, String month){

        month = month.replace("_", " ");
        textViewMonthHistory.setText(month);

        styleHistoryChart();
        setHistoryChartData(returnValues.getChartData());

        textViewBudgetHistory.setText("Budget: " + "\n" + decimalFormat.format(returnValues.getBudgetGesamt()) + returnValues.getCurency());
        textViewRemainingBudgetHistory.setText("Monatsausgaben: " + "\n" + decimalFormat.format(returnValues.getAusgaben()) + returnValues.getCurency());
        textViewDifferenceHistory.setText("Differenz: " + decimalFormat.format(returnValues.getBudgetUebrig()) + returnValues.getCurency());

        SummaryListAdapter summaryAdapter = new SummaryListAdapter(getContext(), arraylist);
        listHistoryPopup.setAdapter(summaryAdapter);

    }



/**----------------------------------------------------------**/


    //Anpassung Aussehen des Charts in der Zusammenfassung
    private void styleHistoryChart(){

        chartHistory.setBackgroundColor(Color.WHITE);

        chartHistory.getDescription().setEnabled(false);
        chartHistory.setDrawHoleEnabled(true);
        chartHistory.setEntryLabelTextSize(0f);

        chartHistory.setMaxAngle(180);
        chartHistory.setRotationAngle(180);
        chartHistory.setRotationEnabled(false);

        chartHistory.animateY(1500, Easing.EaseInOutCubic);

        //Anpassung Legende
        Legend legend = chartHistory.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setDrawInside(true);
        legend.setYOffset(55f);

    }



/**----------------------------------------------------------------------------------------**/

    //Daten in den Chart schreiben
    private void setHistoryChartData(float[] chartData){
        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i < chartData.length; i++){
            values.add(new PieEntry(chartData[i], kategorien[i]));
        }

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setColors(new int[]{graph_red, graph_blue, graph_green, graph_orange, graph_purple, graph_lightblue, graph_gray});

        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);

        chartHistory.setData(data);

        //refresh des Graphen
        chartHistory.invalidate();

    }

/**---------------------------------------------------------------------------------------**/

    //liest Dateien aus dem Appspeicher und passt den Namen an
    public ArrayList<String> getHistoryList(){

        ArrayList<String> historyList = new ArrayList<String>();

        File fileList[] = getContext().getFilesDir().listFiles();
        for(int i = 0; i < fileList.length; i++){

            //Filtert die Fixkostendatei aus den Ergebnissen
            if(!(fileList[i].getName()).equals("fixedCosts.json")){
                if(!(fileList[i].getName()).equals("rList")) {

                    //Anpassung des Namens der Datei auf den Anzeigenamen
                    String name = fileList[i].getName();
                    name = name.replace("_", " ");
                    name = name.replace(".json", "");
                    historyList.add(name);

                }

            }

        }

        return historyList;

    }


/**--------------------------------------------------------------------------------**/



    //Liest Arraylist Expenses aus JSON Datei für ausgewählten Monat
    public ArrayList<Expense> readSelectedExpenseFile(String file) throws IOException{

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

    /**------------------------------------------------------------------------------------------**/



    //Extrahiert Budgetwerte der verschiedenen Kategorien aus der Arraylist
    public ReturnValues getValues(ArrayList<Expense> list, float budgetGesamt, String currency){

        float fixausgabenGesamt = 0F;
        float lebensmittelGesamt = 0F;
        float gebrauchsgegenstaendeGesamt = 0F;
        float unterhaltungGesamt = 0F;
        float transportGesamt = 0F;
        float sonstigesGesamt = 0F;

        for(int i = 0; i < list.size(); i++){

            switch (list.get(i).getCategory()){
                case "Lebensmittel":
                    lebensmittelGesamt = lebensmittelGesamt + list.get(i).getAmount();
                    break;
                case "Gebrauchsgegenstände":
                    gebrauchsgegenstaendeGesamt = gebrauchsgegenstaendeGesamt + list.get(i).getAmount();
                    break;
                case "Unterhaltung":
                    unterhaltungGesamt = unterhaltungGesamt + list.get(i).getAmount();
                    break;
                case "Transport":
                    transportGesamt = transportGesamt + list.get(i).getAmount();
                    break;
                case "Sonstiges":
                    sonstigesGesamt = sonstigesGesamt + list.get(i).getAmount();
                    break;
                case "Fixkosten":
                    fixausgabenGesamt = fixausgabenGesamt + list.get(i).getAmount();
                    break;
            }

        }


        float ausgaben =    fixausgabenGesamt
                            + lebensmittelGesamt
                            + gebrauchsgegenstaendeGesamt
                            + unterhaltungGesamt
                            + transportGesamt
                            + sonstigesGesamt;

        float budgetUebrigGraph = budgetGesamt - ausgaben;

        if(budgetUebrigGraph < 0){
            budgetUebrigGraph = 0.00f;
        }

        float budgetUebrig = budgetGesamt - ausgaben;

        float[] chartData = new float[] {   fixausgabenGesamt,
                                            lebensmittelGesamt,
                                            gebrauchsgegenstaendeGesamt,
                                            unterhaltungGesamt,
                                            transportGesamt,
                                            sonstigesGesamt,
                                            budgetUebrigGraph};

        return new ReturnValues(budgetGesamt, budgetUebrig, ausgaben, currency, chartData);

    }

/**---------------------------------------------------------------------------------------**/

    //aktuellen Monat vom System abfragen
    public String getCurrentMonth(String patern){

        //abfrage und formatierung des Datums
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(patern, Locale.getDefault());

        return dateFormat.format(date);
    }


/**-----------------------------------------------------------------------------------------------**/

    //Sotiert Liste nach Datum
    public static void sortByDate(ArrayList<Expense> list) {

        list.sort(Comparator.comparing(Expense::getDate));

    }


}