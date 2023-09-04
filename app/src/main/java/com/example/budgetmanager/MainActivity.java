package com.example.budgetmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialisierung
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        //lädt Setupcheck verspätet, um zeit zum laden zu geben
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {

                //initialisiert sharedReferences um Persistente Daten zu lesen
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("prefBudgetManager", 0);

                //läd setup nur, wenn flag nicht gesetzt ist
                if(sharedPreferences.contains("SetupDone") == false){

                    try {
                        runSetup();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        }, 300);


        //setzen des Standardfragments
        replaceFragment(new HomeFragment());

        //öffnen des Navigationdrawers
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        //Durch auswahl einer Menüoption wird das oberste Fragment durch das Ausgewählte ersetzt
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);

                if(id == R.id.nav_home){
                    replaceFragment(new HomeFragment());
                }else if (id == R.id.nav_history){
                    replaceFragment(new HistoryFragment());
                }else if (id == R.id.nav_export){
                    replaceFragment(new ExportFragment());
                }else if (id == R.id.nav_settings){
                    replaceFragment(new SettingsFragment());
                }else if (id == R.id.nav_about){
                    replaceFragment(new AboutFragment());
                }else{
                    return true;
                }

                return true;

            }
        });


        //Änderund des Nutzernamens
        setUsername();

    }


/**---------------------------------------------------------------------
----------------------------------------------------------------------**/
   
    //ersetzt das gerade angezeigte Fragment durch das Übergebene mithilfe des Fragmentmanagers
    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }



    //erzwingt schliessen der app nur, wenn zurück doppelt gedrückt wird
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(!(fragment instanceof HomeFragment)){

            replaceFragment(new HomeFragment());

        }else{

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "zum Verlassen ZURÜCK erneut drücken", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);

        }

    }



/**-----------------------------------------------------------------------------------------**/


    //läd Setup, nur wenn setupDone flag in sharedPreferneces nicht existiert
    public void runSetup() throws IOException {

        LayoutInflater loadSetupPopupWindow = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup containerSetup = (ViewGroup) loadSetupPopupWindow.inflate(R.layout.popup_setup, null);

        PopupWindow popupWindowSetup = new PopupWindow(containerSetup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowSetup.showAtLocation(drawerLayout, Gravity.CENTER, 0, 0);


        //initialisiert sharedReferences um Persistente Daten zu speichern
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("prefBudgetManager", 0);
        SharedPreferences.Editor referenceEditor = sharedPreferences.edit();


        //Defaultdaten, falls eingabe abgebrochen
        referenceEditor.putString("Username", "Nutzer");
        referenceEditor.putFloat("Budget", 2000.00f);
        referenceEditor.putString("Currency", "€");
        referenceEditor.putBoolean("SetupDone", true);
        referenceEditor.putString("LastLogin", getCurrentMonth("MMMM_yyyy"));
        referenceEditor.commit();

        //erstellen der Dateien für den jetzigen Monat und für die Fixkosten in den privaten Appspeicher
        FileOutputStream fOut = openFileOutput(getCurrentMonth("MMMM_yyyy") + ".json", Context.MODE_PRIVATE);
        fOut = openFileOutput("fixedCosts.json", Context.MODE_PRIVATE);

        //schreibt leeres JsonArray in die Datei fixedCosts.json
        String filePathFixed = getFilesDir() + "/" + "fixedCosts.json";
        File fileFixed = new File(filePathFixed);
        FileWriter writerFixed = new FileWriter(fileFixed);
        writerFixed.write("[]");
        writerFixed.close();

        //schreibt leeres JsonArray in die Datei des jetzigen Monats
        String filePathMonth = getFilesDir() + "/" + getCurrentMonth("MMMM_yyyy") + ".json";
        File fileMonth = new File(filePathMonth);
        FileWriter writerMonth = new FileWriter(fileMonth);
        writerMonth.write("[]");
        writerMonth.close();

        Button setupButton = popupWindowSetup.getContentView().findViewById(R.id.popup_setup_button);

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView setupUsername = popupWindowSetup.getContentView().findViewById(R.id.popup_setup_name_input);
                TextView setupBudget = popupWindowSetup.getContentView().findViewById(R.id.popup_setup_budget_input);

                //check, ob Felder im setup Popup ausgefüllt sind
                if(TextUtils.isEmpty(setupUsername.getText().toString()) || TextUtils.isEmpty(setupBudget.getText().toString())) {

                    Toast.makeText(getBaseContext(), "Bitte alle benötigten Felder ausfüllen", Toast.LENGTH_SHORT).show();

                }else{


                    //legt Nutzernamen an
                    referenceEditor.putString("Username", setupUsername.getText().toString());

                    //legt Budget an
                    referenceEditor.putFloat("Budget", Float.valueOf(setupBudget.getText().toString()));
                    referenceEditor.commit();

                    setUsername();

                    //HomeFragment mit Budget neu Laden
                    replaceFragment(new HomeFragment());

                    popupWindowSetup.dismiss();

                }
            }
        });

    }

    public void setUsername(){

        //initialisiert sharedReferences um Persistente Daten zu lesen
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("prefBudgetManager", 0);

        //Lade namensfeld und lese nutzernamen aus sharedPreferences
        View header = navigationView.getHeaderView(0);
        TextView username = header.findViewById(R.id.nav_header_username);
        username.setText(sharedPreferences.getString("Username", null));

    }


    public String getCurrentMonth(String patern){

        //abfrage und formatierung des Datums
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(patern, Locale.getDefault());

        return dateFormat.format(date);

    }

}
