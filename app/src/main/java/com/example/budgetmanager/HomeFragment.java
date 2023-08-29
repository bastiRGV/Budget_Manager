package com.example.budgetmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

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
    float chosenAmount = 0.00f;
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

    //formater, um floats auf zwei nachkommastellen zu runden
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //initialisiert sharedReferences um Persistente Daten zu lesen
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("prefBudgetManager", Context.MODE_PRIVATE);
        SharedPreferences.Editor referenceEditor = sharedPreferences.edit();


        //Test, ob neuer Monat begonnen hat
        //abgleich lastLogin aus sharedPreferneces mit jetzigem Datum
        //wenn neuer monat, dann läd Monatszusammenfassung vom letzten monat
        //check, ob setup abgeschlossen, um zu verhindern, das vor dem setup die leere Monatszusammenfassung geladen wird
        if (!getCurrentMonth("MMMM_yyyy").equals(sharedPreferences.getString("LastLogin", null))
                && sharedPreferences.contains("SetupDone")){

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

            ArrayList <Expense> expenses = new ArrayList<Expense>();

            expenses.add(new Expense(1, "Rewe", "Lebensmittel", "2. 5. 2023", 12.00f));
            expenses.add(new Expense(2, "Edeka", "Lebensmittel", "7. 5. 2023", 19.00f));
            expenses.add(new Expense(3, "GPU", "Gebrauchsgegenstände", "9. 8. 2023", 499.00f));
            expenses.add(new Expense(4, "Bus", "Transport", "8. 6. 2023", 2.00f));
            expenses.add(new Expense(5, "Kino", "Unterhaltung", "6. 5. 2023", 30.00f));

            //läd Monatszusammenfassung verspätet, um Zeit zum laden zu geben
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {

                    loadPopupSummary(budgetGesamt, budgetUebrig, ausgaben, currency, chartData, expenses);

                }
            }, 300);

            //speichert letzten monat und legt neue datei mit jetzigem monat an
            String lastMonth = sharedPreferences.getString("LastLogin", null);
            try {
                FileOutputStream fOut = getContext().openFileOutput(getCurrentMonth("MMMM_yyyy") + ".json", Context.MODE_PRIVATE);

                //schreibt leeres JsonArray in die Datei des jetzigen Monats
                String filePathMonth = getContext().getFilesDir() + "/" + getCurrentMonth("MMMM_yyyy") + ".json";
                File fileMonth = new File(filePathMonth);
                FileWriter writer= new FileWriter(fileMonth);
                writer.write("[]");
                writer.close();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        //setze LastLogin auf jetzigen Monat
        referenceEditor.putString("LastLogin", getCurrentMonth("MMMM_yyyy"));
        referenceEditor.commit();





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

        ArrayList <Expense> expenses = new ArrayList<Expense>();

        expenses.add(new Expense(1, "Rewe", "Lebensmittel", "2. 5. 2023", 12.00f));
        expenses.add(new Expense(2, "Edeka", "Lebensmittel", "7. 5. 2023", 19.00f));
        expenses.add(new Expense(3, "GPU", "Gebrauchsgegenstände", "9. 8. 2023", 499.00f));
        expenses.add(new Expense(4, "Bus", "Transport", "8. 6. 2023", 2.00f));
        expenses.add(new Expense(5, "Kino", "Unterhaltung", "6. 5. 2023", 30.00f));


        //Views mit fragment_ids verknüpfen
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
                        loadPopupSummary(budgetGesamt, budgetUebrig, ausgaben, currency, chartData, expenses);
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

        setData(budgetGesamt, budgetUebrig, ausgaben, currency, chartData, expenses);

        return view;
    }




/**------------------------------------------------------------------------------------------
 ------------------------------------------------------------------------------------------**/




    //Daten des Homefragments aktualisieren
    public void setData(float budgetGesamt, float budgetUebrig, float ausgaben, String currency, float[] chartData, ArrayList<Expense> arrayList){

        //letzigen Monat mit Überschriftsformatierung laden
        textViewMonth.setText(getCurrentMonth("MMMM yyyy"));

        styleChart(chartHome);
        setChartData(chartHome, chartData);


        //Daten formatieren und darstellen
        textViewBudget.setText("Budget: " + "\n" + decimalFormat.format(budgetGesamt) + currency);
        textViewRemainingBudget.setText("Monatsausgaben: " + "\n" + decimalFormat.format(ausgaben) + currency);
        textViewDifference.setText("Differenz: " + decimalFormat.format(budgetUebrig) + currency);

        BudgetListAdapter budgetAdapter = new BudgetListAdapter(getContext(), arrayList);
        listHome.setAdapter(budgetAdapter);

    }



/**-------------------------------------------------------------------**/



    //aktuellen Monat vom System abfragen
    public String getCurrentMonth(String patern){

        //abfrage und formatierung des Datums
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(patern, Locale.getDefault());

        return dateFormat.format(date);
    }




/**-------------------------------------------------------------------**/




    //Daten ins Chart einfügen, stylen
    private void setChartData(PieChart chart, float[] chartData){
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



/**-------------------------------------------------------------------**/




    //Anpassungen für den Chart
    private void styleChart(PieChart chart){

        chart.setBackgroundColor(Color.WHITE);

        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setEntryLabelTextSize(0f);

        chart.setMaxAngle(180);
        chart.setRotationAngle(180);
        chart.setRotationEnabled(false);

        chart.animateY(1500, Easing.EaseInOutCubic);

        //Anpassung Legende
        Legend legend = chart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setDrawInside(true);
        legend.setYOffset(55f);

    }






/**---------------------------------------------------------------------------------**/




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
                    Toast.makeText(getActivity().getBaseContext(), chosenCategory + " " + chosenIdentifier + " " + decimalFormat.format(chosenAmount) +  " " + chosenDate, Toast.LENGTH_SHORT).show();
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


/**-------------------------------------------------------------------------------**/


    private void loadPopupSummary(float budgetGesamt, float budgetUebrig, float ausgaben, String currency, float[] chartData, ArrayList<Expense> arraylist){

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

        //läd items aus Array in das Dropdown Menü
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dropdownFilter);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenuSummary.setAdapter(filterAdapter);

        //änderung Sortierung, je nachdem, welcher menüpunkt ausgewählt
        dropdownMenuSummary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

        setSummaryData(budgetGesamt, budgetUebrig, ausgaben, currency, chartData, arraylist);

    }


/**----------------------------------------------------------------**/


    //Daten der Monatszusammenfassung aktualisieren
    public void setSummaryData(float budgetGesamt, float budgetUebrig, float ausgaben, String currency, float[] chartData, ArrayList<Expense> arraylist){

        textViewMonthSummary.setText("letzter Monat");

        styleChart(chartSummary);
        setChartData(chartSummary, chartData);

        //Daten formatieren und darstellen
        textViewBudgetSummary.setText("Budget: " + "\n" + decimalFormat.format(budgetGesamt) + currency);
        textViewRemainingBudgetSummary.setText("Monatsausgaben: " + "\n" + decimalFormat.format(ausgaben) + currency);
        textViewDifferenceSummary.setText("Differenz: " + decimalFormat.format(budgetUebrig) + currency);

        //läd Custom adapter, welcher die liste im Popup erstellt
        SummaryListAdapter summaryAdapter = new SummaryListAdapter(getContext(), arraylist);
        listSummary.setAdapter(summaryAdapter);

    }

}