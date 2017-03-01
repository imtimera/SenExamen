package com.example.netbook.senexamen;

import android.content.Context;
import android.database.ContentObservable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by netbook on 26/01/17.
 */

public class DataBase extends SQLiteOpenHelper {

    private static final int VERSION = 21;
    private static final String DB_NAME = "SenExamen.db";

    public static final String EXAMEN = "examen";
    //attribut de la table
    public static final String URL = "url";
    public static final String SERIE = "serie";
    public static final String MATIERE = "matiere";
    public static final String ANNEE = "annee";
    public static final String TYPE = "type";
    public static final String STOCKAGE = "stockage";


    //creatoion de table carte
    public static final String CREATE_EXAMEN =
            "create table " + EXAMEN + "( " +
                    URL + " String not null, " +
                    SERIE + " String not null, " +
                    MATIERE + " String not null , " +
                    ANNEE + " integer  not null , "+
                    TYPE + " integer  not null , "+
                    STOCKAGE + " string " +
            "primary key ("+URL+" , " +SERIE+" , " +MATIERE+" , " +ANNEE+" , " +TYPE+" ) "+ ")";

    public DataBase(Context context) {
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_EXAMEN);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(DataBase.class.getName(),
                "Upgrading database from version " + i + " to "
                        + i1 + ", which will destroy all old data");
        if (i1 > i) {
            sqLiteDatabase.execSQL("drop table if exists " + EXAMEN);
        }
    }
}
