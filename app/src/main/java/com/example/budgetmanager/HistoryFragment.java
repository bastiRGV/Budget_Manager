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

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

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

        float fixausgabenGesamt = 900.00f;
        float lebensmittelGesamt = 300.00f;
        float gebrauchsgegenstaendeGesamt = 150.00f;
        float unterhaltungGesamt = 199.00f;
        float transportGesamt = 47.00f;
        float sonstigesGesamt = 69.00f;
        float budgetGesamt = sharedPreferences.getFloat("Budget", 0.00f);
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

        String currency =  sharedPreferences.getString("Currency", null);


        //Testeingaben für monatszusammenfassung
        ArrayList <Expense> expenses = new ArrayList<Expense>();

        expenses.add(new Expense(1, "Rewe", "Lebensmittel", "2. 5. 2023", 12.00f));
        expenses.add(new Expense(2, "Edeka", "Lebensmittel", "7. 5. 2023", 19.00f));
        expenses.add(new Expense(3, "GPU", "Gebrauchsgegenstände", "9. 8. 2023", 499.00f));
        expenses.add(new Expense(4, "Bus", "Transport", "8. 6. 2023", 2.00f));
        expenses.add(new Expense(5, "Kino", "Unterhaltung", "6. 5. 2023", 30.00f));


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
                Toast.makeText(getActivity().getBaseContext(), selectedItem, Toast.LENGTH_SHORT).show();
                loadPopupHistory(budgetGesamt, budgetUebrig, ausgaben, currency, chartData, expenses);
            }
        });

        return view;
    }


/**---------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------**/



    //läd das Popup fenster, welches die Monatszusammenfassung anzeigt
    private void loadPopupHistory(float budgetGesamt, float budgetUebrig, float ausgaben, String currency, float[] chartData, ArrayList<Expense> arrayList){

        loadHistoryPopupWindow = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) loadHistoryPopupWindow.inflate(R.layout.popup_summary, null);


        //läd oben erstellten Container in ein Popup Window
        //true lässt  es zu, das Fenster zu schliessen, wenn auserhalb des Fensters gedrückt wird
        popupWindowHistory = new PopupWindow(container, 1200, 2500, true);
        popupWindowHistory.showAtLocation(historyFragment, Gravity.CENTER, 0, 0);



        //Variablen mit IDs verknüpfen Popupfenster Monatszusammenfassung
        textViewMonthHistory = popupWindowHistory.getContentView().findViewById(R.id.header_summary);
        chartHistory = popupWindowHistory.getContentView().findViewById(R.id.chart_summary);

        textViewBudgetHistory = popupWindowHistory.getContentView().findViewById(R.id.summary_budget);
        textViewRemainingBudgetHistory = popupWindowHistory.getContentView().findViewById(R.id.summary_remaining);
        textViewDifferenceHistory = popupWindowHistory.getContentView().findViewById(R.id.summary_difference);

        listHistoryPopup = popupWindowHistory.getContentView().findViewById(R.id.list_summary);

        dropdownMenuHistory = popupWindowHistory.getContentView().findViewById(R.id.summary_ausgaben_sort);

        //läd items aus Array in das Dropdown Menü
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dropdownFilter);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenuHistory.setAdapter(filterAdapter);

        //änderung Sortierung, je nachdem, welcher menüpunkt ausgewählt
        dropdownMenuHistory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 0:
                        //Datum
                        break;
                    case 1:
                        //Name
                        break;
                    case 2:
                        //Betrag
                        break;
                    case 3:
                        //Kategorie
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //default nach Datum sotiert
                return;
            }

        });

        setHistoryData(budgetGesamt, budgetUebrig, ausgaben, currency, chartData, arrayList);

    }


/**----------------------------------------------------------------------------------------**/


    public void setHistoryData(float budgetGesamt, float budgetUebrig, float ausgaben, String currency, float[] chartData, ArrayList<Expense> arraylist){

        textViewMonthHistory.setText("Month");

        styleHistoryChart();
        setHistoryChartData(chartData);

        textViewBudgetHistory.setText("Budget: " + "\n" + decimalFormat.format(budgetGesamt) + currency);
        textViewRemainingBudgetHistory.setText("Monatsausgaben: " + "\n" + decimalFormat.format(ausgaben) + currency);
        textViewDifferenceHistory.setText("Differenz: " + decimalFormat.format(budgetUebrig) + currency);

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
            if(!(fileList[i].getName()).equals("fixedCosts.xml")){

                //Anpassung des Namens der Datei auf den Anzeigenamen
                String name = fileList[i].getName();
                name = name.replace("_", " ");
                name = name.replace(".xml", "");
                historyList.add(name);

            }

        }

        return historyList;

    }

}