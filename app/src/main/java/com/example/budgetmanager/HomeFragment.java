package com.example.budgetmanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //aktueller Monat für Homeseite
        TextView textView = (TextView)view.findViewById(R.id.header_home);
        textView.setText(getMonth());

        return view;
    }

    public String getMonth(){

        //abfrage und formatierung des Datums
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        return df.format(c);
    }

}