package com.example.netbook.senexamen;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Spinner serie, matiere;
    Button valider;
    TextView welcome;
    String name_serie, name_matiere, url;
    int date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        serie=(Spinner)findViewById(R.id.serie);
        matiere=(Spinner)findViewById(R.id.matiere);
        valider=(Button)findViewById(R.id.valider);
        welcome=(TextView)findViewById(R.id.welcome);
        remplirBDD();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pos",""+serie.getSelectedItemPosition());
        outState.putString("pos2",""+matiere.getSelectedItemPosition());


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int pos=Integer.parseInt(savedInstanceState.getString("pos").toString());
        int pos2=Integer.parseInt(savedInstanceState.getString("pos2").toString());
        serie.setSelection(pos);
        matiere.setSelection(pos2);

    }

    protected  void StartActivite(View v){
        name_matiere=String.valueOf(matiere.getSelectedItem());
        name_serie=String.valueOf(serie.getSelectedItem());
        url=checkUrl(matiere.getSelectedItem().toString(), valueSerie(serie.getSelectedItem().toString()),2015);
        if (!url.equals("")) {
            Intent iii = new Intent(this, Apercu.class);
            iii.putExtra("matiere", name_matiere);
            iii.putExtra("serie", name_serie);
            iii.putExtra("url", url);
            Toast t = Toast.makeText(this, "Vous avez choisi " + serie.getSelectedItem().toString() + "/" + matiere.getSelectedItem().toString(), Toast.LENGTH_LONG);
            t.show();
            startActivity(iii);
        }
        else{
            Toast t = Toast.makeText(this, "Ce choix n'existe pas dans la base de donnée ", Toast.LENGTH_LONG);
            t.show();
        }
    }

    protected  String valueSerie(String t){
        if (t.equals("Terminale S1~S3"))
            return "S1";
        if (t.equals("Terminale S2~S4"))
            return "S2";
        if (t.equals("Terminale G"))
            return "G";
        //else
        return "L1";
    }

    protected String checkUrl(String m, String s, int d){
        Cursor cur=getContentResolver().query(MyContentProvider.EXAMEN_URI,null,
                DataBase.MATIERE + "=?" + " AND " + DataBase.SERIE + "=?" +" AND "+DataBase.ANNEE + "=?", new String[]{m,s,d+""},null);
        int i=cur.getCount();
        cur.moveToFirst();
        if (i>0)
            return cur.getString(0);
        return "";

    }
    protected void ajouterBDD(String url,String serie, String matiere, int annee,int type, int stock) {
        Cursor r = getContentResolver().query(MyContentProvider.EXAMEN_URI, null, DataBase.URL + "= ?" , new String[]{url}, null);
        if (r.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(DataBase.URL, url);
            values.put(DataBase.SERIE, serie);
            values.put(DataBase.MATIERE, matiere);
            values.put(DataBase.ANNEE, annee);
            values.put(DataBase.TYPE, type);
            values.put(DataBase.STOCKAGE, stock);
            Uri uri = getContentResolver().insert(
                    MyContentProvider.EXAMEN_URI, values);
        }
    }

    protected void remplirBDD(){
        String url="https://drive.google.com/file/d/0B0caRnHCMmrVcTk1Z3pSbjJhR2M/view?usp=sharing";
        String serie="S2";
        String matiere="SVT";
        int annee=2015;
        int type=1;
        int stock=1;
        ajouterBDD(url,serie,matiere,annee,type,stock);
    }

}
