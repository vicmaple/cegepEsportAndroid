package com.mvd.esport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class voirEntrainement extends AppCompatActivity {

    public Button btnRetour;
    public Button btnAfficher;
    SQLiteOpenHelper helper;
    SQLiteDatabase database;

    //section time et date picker pour le input date et durée
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    EditText dateText;
    EditText timeText;
    //fin time et date picker

    //Maxime 05-03-2022 19:18: variable locale
    EditText inputNom;
    EditText inputEquipe;
    EditText inputActivite;
    EditText inputpersonelle;
    EditText choixintense;
    EditText notePerso;
    int nombreSemaine = 0;
    String TAG = "voirEntrainement";
    int semaineChoisis = 0;

    // you need to have a list of data that you want the spinner to display
    List<String> choixSemaine = new ArrayList<String>();
    ArrayAdapter<String> adapter; //s'assurer que l'adapter n'est jamais détruit.
    Spinner sItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voir_entrainement);
        initMainLayout();


        //initialise la variable helper en cherchant la base de donnée
        helper = new SQLiteOpenHelper(this, "DataSemaine.db", null, 3) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE Esport (_Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Name TEXT, Team TEXT, ActivityPerformed TEXT, Date DATE, ObjectifPersonnel TEXT, Time TIME, Intensity TEXT, Note TEXT)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };

        // pour lire la Bd
        database = helper.getReadableDatabase();
        //ont compte le nombre de semaine que la personne elle à
        try {
            Cursor c = database.rawQuery("SELECT * FROM Esport", null);
            nombreSemaine = c.getCount();

            //si ya des lignes
            if (nombreSemaine > 0) {
                for (int i = 1; i < nombreSemaine; i++) {
                    choixSemaine.add(String.valueOf(i));
                }
            } else {
                Toast.makeText(this, "Au minimum un rapport est nécessaire pour faire afficher les rapports antérieurs", Toast.LENGTH_SHORT).show();
                btnAfficher.setEnabled(false);
            }

        } catch (SQLiteException databaseError) {
            Toast.makeText(this, "Fait un export avant de voir les entrainements", Toast.LENGTH_SHORT).show();
            btnAfficher.setEnabled(false);
        }


        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, choixSemaine);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.choixSemaine);
        sItems.setAdapter(adapter);

    }

    public void initMainLayout() {

        //bouton pour retourner à l'activité principal
        btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAfficher = findViewById(R.id.btnAfficher);
        btnAfficher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Lit une  entré dans la BD
                    semaineChoisis = Integer.parseInt(sItems.getSelectedItem().toString());
                    //https://stackoverflow.com/questions/50441874/rawquery-formatting
                    Cursor c2 = database.rawQuery("SELECT * FROM Esport WHERE _Id = " + semaineChoisis, null);
                    c2.moveToNext();
                    Log.d(TAG, String.valueOf(semaineChoisis));

                    //Affiche les données à l'écran
                    inputNom.setText(c2.getString(1));
                    inputEquipe.setText(c2.getString(2));
                    inputActivite.setText(c2.getString(3));
                    dateText.setText(c2.getString(4));
                    timeText.setText(c2.getString(5));
                    inputpersonelle.setText(c2.getString(6));
                    choixintense.setText(c2.getString(7));
                    notePerso.setText(c2.getString(8));
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "Aucune semaine selectionée.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //nom de la personne ansi que son équipe
        inputNom = findViewById(R.id.voirEntrainementNom);
        inputEquipe = findViewById(R.id.voirEntrainementEquipe);

        //formulaire
        inputActivite = findViewById(R.id.voirEntrainementActivite);
        dateText = findViewById(R.id.voirEntrainementDate);
        inputpersonelle = findViewById(R.id.voirEntrainementPersonnel);
        timeText = findViewById(R.id.voirEntrainementTemps);
        choixintense = findViewById(R.id.voirEntrainementIntensite);
        notePerso = findViewById(R.id.voirEntrainementNotePerso);
        //fin formulaire

    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}