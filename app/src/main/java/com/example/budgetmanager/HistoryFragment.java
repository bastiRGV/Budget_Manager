package com.example.budgetmanager;

import android.content.Context;
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
import android.widget.Toast;

import java.util.List;

public class HistoryFragment extends Fragment {

    String[] history = {"May 2023", "April 2023", "June 2023", "July 2023", "August 2023"};
    private ListView listHistory;
    private FrameLayout historyFragment;
    private PopupWindow popupWindowHistory;
    private LayoutInflater loadHistoryPopupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyFragment = view.findViewById(R.id.history_fragment);

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

    }

}