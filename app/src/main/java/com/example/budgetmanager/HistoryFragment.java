package com.example.budgetmanager;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    float fixausgabenGesamt = 900;
    float lebensmittelGesamt = 300;
    float gebrauchsgegenstaendeGesamt = 150;
    float unterhaltungGesamt = 199;
    float transportGesamt = 47;
    float sonstigesGesamt = 69;
    float budgetGesamt = 2000;
    float ausgaben =    fixausgabenGesamt
                        + lebensmittelGesamt
                        + gebrauchsgegenstaendeGesamt
                        + unterhaltungGesamt
                        + transportGesamt
                        + sonstigesGesamt;
    float budgetUebrigGraph = budgetGesamt - ausgaben;
    float budgetUebrig = budgetGesamt - ausgaben;
    float[] chartData = new float[] {   fixausgabenGesamt,
                                        lebensmittelGesamt,
                                        gebrauchsgegenstaendeGesamt,
                                        unterhaltungGesamt,
                                        transportGesamt,
                                        sonstigesGesamt,
                                        budgetUebrigGraph};

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

    String währung = "€";

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





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyFragment = view.findViewById(R.id.history_fragment);


        ArrayList <String> history = new ArrayList<String>();

        history.add("May 2023");
        history.add("Juny 2023");
        history.add("July 2023");
        history.add("August 2023");
        history.add("September 2023");


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
                loadPopupHistory();
            }
        });

        return view;
    }







    private void loadPopupHistory(){

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

        setHistoryData();

    }





    public void setHistoryData(){

        textViewMonthHistory.setText("Month");

        styleHistoryChart();
        setHistoryChartData();

        textViewBudgetHistory.setText("Budget: " + "\n" + budgetGesamt + währung);
        textViewRemainingBudgetHistory.setText("Monatsausgaben: " + "\n" + ausgaben + währung);
        textViewDifferenceHistory.setText("Differenz: " + budgetUebrig + währung);

        ArrayList <Expense> summaryHistoryExpenses = new ArrayList<Expense>();

        summaryHistoryExpenses.add(new Expense(1, "Rewe", "Lebensmittel", "2. 5. 2023", 12));
        summaryHistoryExpenses.add(new Expense(2, "Edeka", "Lebensmittel", "7. 5. 2023", 19));
        summaryHistoryExpenses.add(new Expense(3, "GPU", "Gebrauchsgegenstände", "9. 8. 2023", 499));
        summaryHistoryExpenses.add(new Expense(4, "Bus", "Transport", "8. 6. 2023", 2));
        summaryHistoryExpenses.add(new Expense(5, "Kino", "Unterhaltung", "6. 5. 2023", 30));

        SummaryListAdapter summaryAdapter = new SummaryListAdapter(getContext(), summaryHistoryExpenses);
        listHistoryPopup.setAdapter(summaryAdapter);

    }







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




    private void setHistoryChartData(){
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

}