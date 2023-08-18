package com.example.budgetmanager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

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






    //Kategorien im Graphen
    String[] kategorien = new String[]{ "Fixkosten",
                                        "Lebensmittel",
                                        "Gebrauchsgegenstände",
                                        "Unterhaltung",
                                        "Transport",
                                        "Sonstiges",
                                        "übriges Budget"};

    //Farbcodes, welche für Graphen nutzbar sind (nicht aus color.xml in bibliothek importierbar)
     int graph_red = 0xFFE60049;
    int graph_blue = 0xFF0BB4FF;
    int graph_green = 0xFF50E991;
    int graph_orange = 0xFFFFA300;
    int graph_purple = 0xFF9B19F5;
    int graph_lightblue = 0xFFB3D4FF;
    int graph_gray = 0xFF989797;

    String währung = "€";

    //Name, Kategorie, Datum, Betrag,
    String[] listAusgaben = {"A, a, 01.01.23, 50€", "B, b, 01.01.23, 50€", "C, c, 01.01.23, 50€", "D, d, 01.01.23, 50€"};




    //Menüeinträge für Filter der Ausgaben auf Homepage
    String[] dropdownFilter = new String[] {"Datum",
                                            "Bezeichnung",
                                            "Betrag",
                                            "Kategorie"};

    //Menüeinträge für Kategorien auf im Hinzufügen Popupfenster
    String[] kategorienAddEntries = new String[] {  "Lebensmittel",
                                                    "Gebrauchsgegenstände",
                                                    "Unterhaltung",
                                                    "Transport",
                                                    "Sonstiges"};





    //Ausgewählte Kategorie im Eintragspopup
    String chosenCategory;
    //Ausgewählte Bezeichnung im Eintragspopup
    String chosenIdentifier = "";
    //Betrag der Ausgabe im Eintragspopup
    float chosenAmount = 0;
    //Datum der Ausgabe im Eintragspopup
    String chosenDate = "";




    //Views anlegen
    private ListView listHome;
    private PieChart chartHome;
    private TextView textViewMonth;
    private TextView textViewBudget;
    private TextView textViewRemainingBudget;
    private TextView textViewDifference;
    private Spinner dropdownMenuHome;
    private FloatingActionButton actionButton;
    private PopupWindow popupWindowAddEntries;
    private LayoutInflater loadAddPopupWindow;
    private PopupWindow popupWindowSummary;
    private LayoutInflater loadSummaryPopupWindow;
    private FrameLayout homeFragment;
    private Button popupAddButton;
    private EditText popupAddIdentifier;
    private EditText popupAddAmount;
    private DatePicker popupAddDate;
    private PieChart chartSummary;
    private ListView listSummary;
    private TextView textViewMonthSummary;
    private TextView textViewBudgetSummary;
    private TextView textViewRemainingBudgetSummary;
    private TextView textViewDifferenceSummary;
    private Spinner dropdownMenuSummary;











    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if(budgetUebrigGraph < 0){
            budgetUebrigGraph = 0;
        }

        //Variablen mit fragment_ids verknüpfen
        textViewMonth = view.findViewById(R.id.header_home);
        chartHome = view.findViewById(R.id.chart);

        textViewBudget = view.findViewById(R.id.home_budget);
        textViewRemainingBudget = view.findViewById(R.id.home_remaining);
        textViewDifference = view.findViewById(R.id.home_difference);

        listHome = view.findViewById(R.id.list_home);


        //filtermenü befüllen
        dropdownMenuHome = view.findViewById(R.id.ausgaben_sort);

        //läd items aus Array in das Dropdown Menü
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dropdownFilter);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenuHome.setAdapter(filterAdapter);

        //änderung Sortierung, je nachdem, welcher menüpunkt ausgewählt
        dropdownMenuHome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 0:
                        //Datum
                        break;
                    case 1:
                        //Name
                        loadPopupSummary();
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



        actionButton = view.findViewById(R.id.action_button);
        homeFragment = view.findViewById(R.id.home_fragment);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //läd die popup_add_text in einen Container
                loadAddPopupWindow = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) loadAddPopupWindow.inflate(R.layout.popup_add_entries, null);

                //läd oben erstellten Container in ein Popup Window
                //true lässt  es zu, das Fenster zu schliessen, wenn auserhalb des Fensters gedrückt wird
                popupWindowAddEntries = new PopupWindow(container, 1200, 2200, true);
                popupWindowAddEntries.showAtLocation(homeFragment, Gravity.CENTER, 0, 0);

                //fenster Schliesst, wenn außerhalb des Fensters berührt wird
                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindowAddEntries.dismiss();
                        return true;
                    }
                });

                logicPopupWindow();

            }
        });

        setData();

        return view;
    }









    //Daten des Homefragments aktualisieren
    public void setData(){

        textViewMonth.setText(getMonth());

        styleChart(chartHome);
        setChartData(chartHome);

        textViewBudget.setText("Budget: " + "\n" + budgetGesamt + währung);
        textViewRemainingBudget.setText("Monatsausgaben: " + "\n" + ausgaben + währung);
        textViewDifference.setText("Differenz: " + budgetUebrig + währung);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , listAusgaben);
        listHome.setAdapter(adapter);

    }








    //aktuellen Monat vom System abfragen
    public String getMonth(){

        //abfrage und formatierung des Datums
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        return df.format(c);
    }









    //Daten ins Chart einfügen, stylen
    private void setChartData(PieChart chart){
        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i < chartData.length; i++){
            values.add(new PieEntry(chartData[i], kategorien[i]));
        }

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setColors(new int[]{graph_red, graph_blue, graph_green, graph_orange, graph_purple, graph_lightblue, graph_gray});

        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);

        //refresh des Graphen
        chart.invalidate();
    }








    //Anpassungen für den Chart
    private void styleChart(PieChart chart){

        chart.setBackgroundColor(Color.WHITE);

        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setEntryLabelTextSize(0f);

        chart.setMaxAngle(180);
        chart.setRotationAngle(180);
        chart.setRotationEnabled(false);

        chartHome.animateY(1500, Easing.EaseInOutCubic);

        //Anpassung Legende
        Legend legend = chart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setDrawInside(true);
        legend.setYOffset(55f);

    }











    //PopupWindow Eingabenlogik
    private void logicPopupWindow(){

        //Eingabeknopf für Eingabe neuer Ausgaben
        popupAddButton = popupWindowAddEntries.getContentView().findViewById(R.id.popup_add_button);
        popupAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupAddIdentifier = popupWindowAddEntries.getContentView().findViewById(R.id.popup_add_bezeichnung_input);
                popupAddAmount = popupWindowAddEntries.getContentView().findViewById(R.id.popup_add_betrag_input);
                popupAddDate = popupWindowAddEntries.getContentView().findViewById(R.id.popup_add_datum_input);

                //check, ob Eingabefelder leer sind
                if(TextUtils.isEmpty(popupAddIdentifier.getText().toString()) || TextUtils.isEmpty(popupAddAmount.getText().toString())){

                    Toast.makeText(getActivity().getBaseContext(), "Bitte alle benötigten Felder ausfüllen", Toast.LENGTH_SHORT).show();

                }else{

                    //Liest Bezeichner aus der Eingabe im Popup window
                    chosenIdentifier = popupAddIdentifier.getText().toString();


                    //Liest Betrag aus der Eingabe im Popup window
                    chosenAmount = Float.valueOf(popupAddAmount.getText().toString());


                    //Liest Datum aus dem Datepicker im Popup window un konvertiert zu String
                    chosenDate = popupAddDate.getDayOfMonth() +  "." + popupAddDate.getMonth() +  "." + popupAddDate.getYear();


                    //schreibt Eintrag und schließt Popup Window
                    Toast.makeText(getActivity().getBaseContext(), chosenCategory + " " + chosenIdentifier + " " + chosenAmount +  " " + chosenDate, Toast.LENGTH_SHORT).show();
                    popupWindowAddEntries.dismiss();

                }

            }
        });


        //Kategoriemenü des Popupwindows befüllen
        Spinner dropdownMenuAddKategorie = popupWindowAddEntries.getContentView().findViewById(R.id.popup_add_kategorie_dropdown);

        //läd items aus Array in das Dropdown Menü
        ArrayAdapter<String> kategorieAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, kategorienAddEntries);
        kategorieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenuAddKategorie.setAdapter(kategorieAdapter);

        //Auswahl der kategorien
        dropdownMenuAddKategorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 0:
                        //Lebensmittel
                        chosenCategory = "Lebensmittel";
                        break;
                    case 1:
                        //Gebrauchsgegenstände
                        chosenCategory = "Gebrauchsgegenstände";
                        break;
                    case 2:
                        //Unterhaltung
                        chosenCategory = "Unterhaltung";
                        break;
                    case 3:
                        //Transport
                        chosenCategory = "Transport";
                        break;
                    case 4:
                        //Sonstiges
                        chosenCategory = "Sonstiges";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                chosenCategory = "Lebensmittel";
                return;
            }

        });

    }




    private void loadPopupSummary(){

        loadSummaryPopupWindow = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) loadSummaryPopupWindow.inflate(R.layout.popup_summary, null);


        //läd oben erstellten Container in ein Popup Window
        //true lässt  es zu, das Fenster zu schliessen, wenn auserhalb des Fensters gedrückt wird
        popupWindowSummary = new PopupWindow(container, 1200, 2500, true);
        popupWindowSummary.showAtLocation(homeFragment, Gravity.CENTER, 0, 0);



        //Variablen mit IDs verknüpfen Popupfenster Monatszusammenfassung
        textViewMonthSummary = popupWindowSummary.getContentView().findViewById(R.id.header_summary);
        chartSummary = popupWindowSummary.getContentView().findViewById(R.id.chart_summary);

        textViewBudgetSummary = popupWindowSummary.getContentView().findViewById(R.id.summary_budget);
        textViewRemainingBudgetSummary = popupWindowSummary.getContentView().findViewById(R.id.summary_remaining);
        textViewDifferenceSummary = popupWindowSummary.getContentView().findViewById(R.id.summary_difference);

        listSummary = popupWindowSummary.getContentView().findViewById(R.id.list_summary);

        dropdownMenuSummary = popupWindowSummary.getContentView().findViewById(R.id.summary_ausgaben_sort);

        setSummaryData();

    }





    //Daten der Monatszusammenfassunf aktualisieren
    public void setSummaryData(){

        textViewMonthSummary.setText("letzter Monat");

        styleChart(chartSummary);
        setChartData(chartSummary);

        textViewBudgetSummary.setText("Budget: " + "\n" + budgetGesamt + währung);
        textViewRemainingBudgetSummary.setText("Monatsausgaben: " + "\n" + ausgaben + währung);
        textViewDifferenceSummary.setText("Differenz: " + budgetUebrig + währung);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , listAusgaben);
        listSummary.setAdapter(adapter);

    }

}