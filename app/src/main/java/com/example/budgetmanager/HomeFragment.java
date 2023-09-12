package com.example.budgetmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.LocaleList;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    private static HomeFragment instance;

    //formater, um floats auf zwei nachkommastellen zu runden
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        instance = this;

        //initialisiert sharedReferences um Persistente Daten zu lesen
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("prefBudgetManager", Context.MODE_PRIVATE);
        SharedPreferences.Editor referenceEditor = sharedPreferences.edit();

        //jetzigen Monat mit Überschriftsformatierung laden
        textViewMonth = view.findViewById(R.id.header_home);
        textViewMonth.setText(getCurrentMonth("MMMM yyyy"));



        //verhinder Ladeversuch aus dateien, solange das setup noch nicht beendet ist
        if(sharedPreferences.contains("SetupDone")){

            //Test, ob neuer Monat begonnen hat
            //abgleich lastLogin aus sharedPreferneces mit jetzigem Datum
            //wenn neuer monat, dann läd Monatszusammenfassung vom letzten monat
            if (!getCurrentMonth("MMMM_yyyy").equals(sharedPreferences.getString("LastLogin", null))){

                //liest Daten aus der Monatsdatei
                ArrayList <Expense> expenses = new ArrayList<Expense>();
                try {
                    expenses = readMonthlyExpenseFile(sharedPreferences.getString("LastLogin", null) + ".json");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //daten aus der Arraylist in ein Objekt gespeichert
                ReturnValues returnValues = getValues(expenses);

                //läd Monatszusammenfassung verspätet, um Zeit zum laden zu geben
                ArrayList<Expense> finalExpenses = expenses;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        loadPopupSummary(returnValues, finalExpenses);

                    }
                }, 300);

                //speichert das Budget, welches für den letzten Monat eingegeben wurde für die Monatszusammenfassung in den shared Preferences
                referenceEditor.putFloat(sharedPreferences.getString("LastLogin", null), returnValues.getBudgetGesamt());

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

                try {
                    writeFixedCostsInMonthFile(lastMonth + ".json");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }







            //setze LastLogin auf jetzigen Monat
            referenceEditor.putString("LastLogin", getCurrentMonth("MMMM_yyyy"));
            referenceEditor.commit();


            //liest Daten aus der Monatsdatei
            ArrayList <Expense> expenses = new ArrayList<Expense>();
            try {
                expenses = readMonthlyExpenseFile(getCurrentMonth("MMMM_yyyy") + ".json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //daten aus der Arraylist in ein Objekt gespeichert
            ReturnValues returnValues = getValues(expenses);


            //Views mit fragment_ids verknüpfen
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

                    ArrayList <Expense> expensesSorted = new ArrayList<Expense>();
                    try {
                        expensesSorted = readMonthlyExpenseFile(getCurrentMonth("MMMM_yyyy") + ".json");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //daten aus der Arraylist in ein Objekt gespeichert
                    ReturnValues returnValues = getValues(expensesSorted);

                    switch (position) {
                        case 0:
                            sortByDate(expensesSorted);
                            Collections.reverse(expensesSorted);
                            break;
                        case 1:
                            sortByName(expensesSorted);
                            break;
                        case 2:
                            sortByAmount(expensesSorted);
                            Collections.reverse(expensesSorted);
                            break;
                        case 3:
                            sortByCategory(expensesSorted);
                            break;
                    }

                    //aktualisieren der Daten
                    setData(returnValues, expensesSorted);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
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
                    popupWindowAddEntries = new PopupWindow(container, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    popupWindowAddEntries.showAtLocation(homeFragment, Gravity.CENTER, 0, 0);

                    logicPopupWindow();

                }
            });

            //Standardsotierung nach Datum
            sortByDate(expenses);
            Collections.reverse(expenses);

            setData(returnValues, expenses);

        }

        return view;

    }


    public static HomeFragment getInstance() {
        return instance;
    }

/**------------------------------------------------------------------------------------------
 ------------------------------------------------------------------------------------------**/




    //Daten des Homefragments aktualisieren
    public void setData(ReturnValues returnValues, ArrayList<Expense> arrayList){

        styleChart(chartHome);
        setChartData(chartHome, returnValues.getChartData());


        //Daten formatieren und darstellen
        textViewBudget.setText("Budget: " + "\n" + decimalFormat.format(returnValues.getBudgetGesamt()) + returnValues.getCurency());
        textViewRemainingBudget.setText("Monatsausgaben: " + "\n" + decimalFormat.format(returnValues.getAusgaben()) + returnValues.getCurency());
        textViewDifference.setText("Differenz: " + decimalFormat.format(returnValues.getBudgetUebrig()) + returnValues.getCurency());

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


                //liest Daten aus der Datei des jetzigen Monats und speichert sie in die Liste
                ArrayList<Expense> expensesAdd = new ArrayList<>();
                expensesAdd.clear();
                try {
                    expensesAdd = readMonthlyExpenseFile(getCurrentMonth("MMMM_yyyy") + ".json");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                popupAddIdentifier = popupWindowAddEntries.getContentView().findViewById(R.id.popup_add_bezeichnung_input);
                popupAddAmount = popupWindowAddEntries.getContentView().findViewById(R.id.popup_add_betrag_input);
                popupAddDate = popupWindowAddEntries.getContentView().findViewById(R.id.popup_add_datum_input);

                //liest sich die nächste freie ID aus der liste
                int availableId = getFirstAvailableId(expensesAdd);

                //check, ob Eingabefelder leer sind
                if(TextUtils.isEmpty(popupAddIdentifier.getText().toString()) || TextUtils.isEmpty(popupAddAmount.getText().toString())){

                    Toast.makeText(getActivity().getBaseContext(), "Bitte alle benötigten Felder ausfüllen", Toast.LENGTH_SHORT).show();

                }else{

                    //Liest Bezeichner aus der Eingabe im Popup window
                    chosenIdentifier = popupAddIdentifier.getText().toString();


                    //Liest Betrag aus der Eingabe im Popup window
                    chosenAmount = Float.valueOf(popupAddAmount.getText().toString());


                    //Liest Datum aus dem Datepicker im Popup window und konvertiert zu String
                    int day = popupAddDate.getDayOfMonth();
                    int month = popupAddDate.getMonth() + 1;
                    String dayString = "";
                    String monthString = "";

                    if(day < 10){
                        dayString  = "0" + String.valueOf(day);
                    }else{
                        dayString = String.valueOf(day);
                    }

                    if(month < 10){
                        monthString  = "0" + String.valueOf(month);
                    }else{
                        monthString = String.valueOf(month);
                    }

                    chosenDate = dayString +  "." + monthString +  "." + popupAddDate.getYear();


                    //schreibt Eintrag und schließt Popup Window
                    expensesAdd.add(new Expense(availableId, chosenIdentifier, chosenCategory, chosenDate, chosenAmount));

                    //schreibt Daten in Datei
                    try {
                        writeMonthlyExpenseFile(expensesAdd, getCurrentMonth("MMMM_yyyy") + ".json");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    //daten aus der Arraylist in ein Objekt gespeichert
                    ReturnValues returnValues = getValues(expensesAdd);

                    sortByDate(expensesAdd);
                    Collections.reverse(expensesAdd);

                    setData(returnValues, expensesAdd);

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


    private void loadPopupSummary(ReturnValues returnValues, ArrayList<Expense> arraylist){

        loadSummaryPopupWindow = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) loadSummaryPopupWindow.inflate(R.layout.popup_summary, null);


        //läd oben erstellten Container in ein Popup Window
        //true lässt  es zu, das Fenster zu schliessen, wenn auserhalb des Fensters gedrückt wird
        popupWindowSummary = new PopupWindow(container, 900, 2000, true);
        popupWindowSummary.showAtLocation(homeFragment, Gravity.CENTER, 0, 0);



        //Variablen mit IDs verknüpfen Popupfenster Monatszusammenfassung
        textViewMonthSummary = popupWindowSummary.getContentView().findViewById(R.id.header_summary);
        chartSummary = popupWindowSummary.getContentView().findViewById(R.id.chart_summary);

        textViewBudgetSummary = popupWindowSummary.getContentView().findViewById(R.id.summary_budget);
        textViewRemainingBudgetSummary = popupWindowSummary.getContentView().findViewById(R.id.summary_remaining);
        textViewDifferenceSummary = popupWindowSummary.getContentView().findViewById(R.id.summary_difference);

        listSummary = popupWindowSummary.getContentView().findViewById(R.id.list_summary);

        //Standardsotierung nach Datum
        sortByDate(arraylist);

        setSummaryData(returnValues, arraylist);

    }


/**----------------------------------------------------------------**/


    //Daten der Monatszusammenfassung aktualisieren
    public void setSummaryData(ReturnValues returnValues, ArrayList<Expense> arraylist){

        textViewMonthSummary.setText("letzter Monat");

        styleChart(chartSummary);
        setChartData(chartSummary, returnValues.getChartData());

        //Daten formatieren und darstellen
        textViewBudgetSummary.setText("Budget: " + "\n" + decimalFormat.format(returnValues.getBudgetGesamt()) + returnValues.getCurency());
        textViewRemainingBudgetSummary.setText("Monatsausgaben: " + "\n" + decimalFormat.format(returnValues.getAusgaben()) + returnValues.getCurency());
        textViewDifferenceSummary.setText("Differenz: " + decimalFormat.format(returnValues.getBudgetUebrig()) + returnValues.getCurency());

        //läd Custom adapter, welcher die liste im Popup erstellt
        SummaryListAdapter summaryAdapter = new SummaryListAdapter(getContext(), arraylist);
        listSummary.setAdapter(summaryAdapter);

    }



/**------------------------------------------------------------------------------------------------------**/



    //Liest Arraylist Expenses aus JSON Datei für jetzigen/vergangenen Monat
    public ArrayList<Expense> readMonthlyExpenseFile(String file) throws IOException{

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



    //Liest Arraylist FixedExpenses aus JSON Datei
    public ArrayList<FixedExpense> readFixedExpensesFile() throws IOException{

        String returnString = "";

        InputStream inputStream = getContext().openFileInput("fixedCosts.json");

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

        Type type = new TypeToken<ArrayList<FixedExpense>>(){}.getType();

        ArrayList<FixedExpense> list = gson.fromJson(returnString, type);

        return list;

    }


/**------------------------------------------------------------------------------------------**/



    //Extrahiert Budgetwerte der verschiedenen Kategorien aus der Arraylist
    public ReturnValues getValues(ArrayList<Expense> list){

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("prefBudgetManager", Context.MODE_PRIVATE);

        float budgetGesamt = sharedPreferences.getFloat("Budget", 0.00f);
        String currency =  sharedPreferences.getString("Currency", null);

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
            }

        }

        ArrayList<FixedExpense> fixedExpenses = new ArrayList<>();
        try {
            fixedExpenses = readFixedExpensesFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i < fixedExpenses.size(); i++){

            fixausgabenGesamt = fixausgabenGesamt + fixedExpenses.get(i).getAmount();

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


/**------------------------------------------------------------------------------------------**/

    //liest die erste verfügbare id aus der Arraylist
    public int getFirstAvailableId(ArrayList<Expense> list){

        int[] usedIds = new int[list.size()];

        for(int i = 0; i < list.size(); i++){

            usedIds[i] = list.get(i).getId();

        }

        Arrays.sort(usedIds, 0, usedIds.length);

        int unusedId = 1;

        for(int i = 0; i < usedIds.length; i++){

            if(usedIds[i] == unusedId){
                unusedId++;
            }else{
                return unusedId;
            }

        }

        return unusedId;

    }




/**---------------------------------------------------------------------------------------------**/



    //Schreibt übergeben Arraylist in die angegebene JSON Datei
    public void writeMonthlyExpenseFile(ArrayList<Expense> arrayList, String fileToWrite) throws JSONException {

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Expense>>() {}.getType();
        JSONArray jsonArray = new JSONArray(gson.toJson(arrayList,type));

        try {
            String filePath = getContext().getFilesDir() + "/" + fileToWrite;
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            writer.write(jsonArray.toString(4));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



/**-------------------------------------------------------------------------------------------------**/

    //Schreibt am Monatsende die Fixkosten in die Normale Monatsdatei
    private void writeFixedCostsInMonthFile(String lastMonth) throws IOException, JSONException {

        ArrayList<Expense> expenses = new ArrayList<>();
        expenses = readMonthlyExpenseFile(lastMonth);

        ArrayList<FixedExpense> fixedExpenses = new ArrayList<>();
        fixedExpenses = readFixedExpensesFile();

        for(int i = 0; i < fixedExpenses.size(); i++){

            int id = 0;
            String name = fixedExpenses.get(i).getName();
            float amount = fixedExpenses.get(i).getAmount();

            expenses.add(new Expense(id, name, "Fixkosten", "Monatsanfang", amount));

        }

        try {
            writeMonthlyExpenseFile(expenses, lastMonth);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

/**-----------------------------------------------------------------------------------------------**/

    //Sortierungsfunktionen für übergebene Liste
    public static void sortByDate(ArrayList<Expense> list) {

        list.sort(Comparator.comparing(Expense::getDate));

    }

    public static void sortByName(ArrayList<Expense> list) {

        list.sort(Comparator.comparing(Expense::getName, String.CASE_INSENSITIVE_ORDER));

    }

    public static void sortByAmount(ArrayList<Expense> list) {

        list.sort(Comparator.comparing(Expense::getAmount));

    }

    public static void sortByCategory(ArrayList<Expense> list) {

        list.sort(Comparator.comparing(Expense::getCategory));

    }


/**------------------------------------------------------------------------------------**/

    //wird aus dem Listadapter aufgerufen und übergibt die ID des zu löschenden Eintrags
    public void deleteItem(int id) throws JSONException {

        ArrayList<Expense> deleteExpense = new ArrayList<>();
        try {
            deleteExpense = readMonthlyExpenseFile(getCurrentMonth("MMMM_yyyy") + ".json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i < deleteExpense.size(); i++){

            if(deleteExpense.get(i).getId() == id){

                deleteExpense.remove(i);

            }

        }

        writeMonthlyExpenseFile(deleteExpense, getCurrentMonth("MMMM_yyyy") + ".json");

        //daten aus der Arraylist in ein Objekt gespeichert
        ReturnValues returnValues = getValues(deleteExpense);

        //Standardsotierung nach Datum
        sortByDate(deleteExpense);
        Collections.reverse(deleteExpense);

        setData(returnValues, deleteExpense);

    }


}