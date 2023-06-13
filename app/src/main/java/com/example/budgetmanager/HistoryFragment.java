package com.example.budgetmanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class HistoryFragment extends Fragment {

    String[] history = {"May 2023", "April 2023", "June 2023", "July 2023", "August 2023"};
    private ListView listHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

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
            }
        });

        return view;
    }
}