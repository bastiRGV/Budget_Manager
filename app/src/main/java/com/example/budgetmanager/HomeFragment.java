package com.example.budgetmanager;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    float fixausgabenGesamt = 900;
    float lebensmittelGesamt = 300;
    float gebrauchsgegenstaendeGesamt = 150;
    float unterhaltungGesamt = 199;
    float transportGesamt = 47;
    float sonstigesGesamt = 69;
    float budgetGesamt = 2000;
    float budgetUebrig = budgetGesamt   - fixausgabenGesamt
                                        - lebensmittelGesamt
                                        - gebrauchsgegenstaendeGesamt
                                        - unterhaltungGesamt
                                        - transportGesamt
                                        - sonstigesGesamt;
    /**
    if(budgetUebrig < 0){
        budgetUebrig = 0;
    }
    **/
    float[] chartData = new float[] {   fixausgabenGesamt,
                                        lebensmittelGesamt,
                                        gebrauchsgegenstaendeGesamt,
                                        unterhaltungGesamt,
                                        transportGesamt,
                                        sonstigesGesamt,
                                        budgetUebrig};

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

    private PieChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //aktueller Monat für Homeseite
        TextView textView = (TextView)view.findViewById(R.id.header_home);
        textView.setText(getMonth());

        mChart = (PieChart) view.findViewById(R.id.chart);
        styleChart();
        setChartData();

        return view;
    }

    public String getMonth(){

        //abfrage und formatierung des Datums
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        return df.format(c);
    }

    //Daten ins Chart einfügen, stylen
    private void setChartData(){
        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i < chartData.length; i++){
            values.add(new PieEntry(chartData[i], kategorien[i]));
        }

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setColors(new int[]{graph_red, graph_blue, graph_green, graph_orange, graph_purple, graph_lightblue, graph_gray});

        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        //refresh des Graphen
        mChart.invalidate();
    }

    //Anpassungen für den Chart
    private void styleChart(){

        mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);
        mChart.setDrawHoleEnabled(true);
        mChart.setEntryLabelTextSize(0f);

        mChart.setMaxAngle(180);
        mChart.setRotationAngle(180);
        mChart.setRotationEnabled(false);

        mChart.animateY(1500, Easing.EaseInOutCubic);

        //Anpassung Legende
        Legend legend = mChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setDrawInside(true);
        legend.setYOffset(55f);

    }

}