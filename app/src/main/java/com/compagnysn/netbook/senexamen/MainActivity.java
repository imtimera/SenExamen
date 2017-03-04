package com.compagnysn.netbook.senexamen;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Spinner serie, matiere,annee;
    Button valider;
    TextView welcome;
    String name_serie, name_matiere, url;
    int date;
    String s1="Terminale S1~S3", s2="Terminale S2~S4", l1="Terminale L1", l2="Terminale L2", g="Terminale G";
    String math="Mathématiques", svt="SVT", philo="Philosophie", anglais="Anglais", fr="Français",pc="Physique Chimie", hg="Histoire Géographie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        serie=(Spinner)findViewById(R.id.serie);
        matiere=(Spinner)findViewById(R.id.matiere);
        annee=(Spinner)findViewById(R.id.annee);
        valider=(Button)findViewById(R.id.valider);
        welcome=(TextView)findViewById(R.id.welcome);
        Cursor cur=getContentResolver().query(MyContentProvider.EXAMEN_URI,null,
                DataBase.MATIERE + " = ? " + " AND " + DataBase.SERIE + " = ?" ,
                new String[]{pc,s1},null);

        int i=cur.getCount();
        cur.moveToFirst();
        if (i==0) {
            remplirBDD();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pos",""+serie.getSelectedItemPosition());
        outState.putString("pos2",""+matiere.getSelectedItemPosition());
        outState.putString("pos3",""+annee.getSelectedItemPosition());


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int pos=Integer.parseInt(savedInstanceState.getString("pos").toString());
        int pos2=Integer.parseInt(savedInstanceState.getString("pos2").toString());
        int pos3=Integer.parseInt(savedInstanceState.getString("pos3").toString());
        serie.setSelection(pos);
        matiere.setSelection(pos2);
        annee.setSelection(pos3);


    }

    public void StartActivite(View v) {
        if (v==valider) {
            name_matiere = String.valueOf(matiere.getSelectedItem());
            name_serie = String.valueOf(serie.getSelectedItem());
            url = checkUrl(matiere.getSelectedItem().toString(), (serie.getSelectedItem().toString()), Integer.parseInt(annee.getSelectedItem().toString()));
            if (!url.equals("")) {
                if (isConnectedInternet() == true) {
                    Intent iii = new Intent(this, Apercu.class);
                    iii.putExtra("matiere", name_matiere);
                    iii.putExtra("serie", name_serie);
                    iii.putExtra("url", url);
                    iii.putExtra("annee", annee.getSelectedItem().toString());
                    /*Cursor cur=getContentResolver().query(MyContentProvider.EXAMEN_URI,null,
                            DataBase.MATIERE + " = ?" + " AND " + DataBase.SERIE + " = ?" +" AND "+DataBase.ANNEE + " = ? ",
                            new String[]{String.valueOf(matiere.getSelectedItemId()),String.valueOf(serie.getSelectedItemId()),String.valueOf(annee.getSelectedItemId())},null);

                    String type=cur.getString(0);
                    */
                    String type = "1";
                    iii.putExtra("type", type);
                    Toast t = Toast.makeText(this, "Baccalauréat " + annee.getSelectedItem().toString() + " " + matiere.getSelectedItem().toString() + " " + serie.getSelectedItem().toString(), Toast.LENGTH_LONG);
                    t.show();
                    startActivity(iii);
                } else {
                    this.setTheme(android.R.style.Animation_Dialog);
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);//new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                    dlgAlert.setTitle("ATTENTION");
                    dlgAlert.setMessage("Veuillez vérifier votre connexion Internet");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dlgAlert.setIcon(android.R.drawable.ic_dialog_alert);


                    dlgAlert.create().show();
                }
            } else {
                Toast t = Toast.makeText(this, "Ce choix n'existe pas dans la base de donnée", Toast.LENGTH_LONG);
                t.show();

            }
        }
    }


    protected String checkUrl(String m, String s, int d){
        Cursor cur=getContentResolver().query(MyContentProvider.EXAMEN_URI,null,
                DataBase.MATIERE + " = ?" + " AND " + DataBase.SERIE + " = ?" +" AND "+DataBase.ANNEE + " = ? ", new String[]{m,s,d+""},null);
        int i=cur.getCount();
        cur.moveToFirst();
        if (i>0)
            return cur.getString(0);
        return "";

    }
    protected void ajouterBDD(String url,String serie, String matiere, int annee,int type, int stock) {
        Cursor r = getContentResolver().query(MyContentProvider.EXAMEN_URI, null, DataBase.URL + " = ?"+" AND " +DataBase.SERIE + " = ?" , new String[]{url,serie}, null);
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

    public boolean isConnectedInternet()
    {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

        //return true;
    }


    protected void remplirBDD(){
        /*String url="https://drive.google.com/file/d/0B0caRnHCMmrVcTk1Z3pSbjJhR2M/view?usp=sharing";
        String serie="S2";
        String matiere="SVT";
        int annee=2015;
        int type=1;
        int stock=1;
        ajouterBDD("https://drive.google.com/file/d/0B0caRnHCMmrVcTk1Z3pSbjJhR2M/view?usp=sharing","S2","SVT",2015,1,1);
        ajouterBDD("https://drive.google.com/file/d/0B0caRnHCMmrVSUk2VFVEenBTd3c/view?usp=sharing","S2","SVT",2016,1,1);
        ajouterBDD("https://drive.google.com/file/d/0B0caRnHCMmrVd2Z6RmVheWFMRFk/view?usp=sharing","S1","SVT",2012,1,1);

        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_physiques_S1S3_1er_gr_2016.pdf","S1","Physique Chimie",2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Sciences_physiques_S1S3_1er_gr_2016.pdf","S1","Physique Chimie",2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_physiques_S2_1er_gr_2016.pdf","S2","Physique Chimie",2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_physiques_L2_1er_gr_2016.pdf","L2","Physique Chimie",2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Sciences_physiques_L2_1er_gr_2016.pdf","L2","Physique Chimie",2016,0,1);
        */
        //ajouterBDD(String url,String serie, String matiere, int annee,int type, int stock)

        //----------------------2016---------------------------------------------------------
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_S_1er_gr_2016.pdf",s1,fr,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_physiques_S1S3_1er_gr_2016.pdf",s1,pc,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Sciences_physiques_S1S3_1er_gr_2016.pdf",s1,pc,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Maths_S1-S3_1er_gr_2016.pdf",s1,math,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Maths_S1-S3_1er_gr_2016.pdf",s1,math,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/HG_L-S_1er_gr_2016-2.pdf",s1,hg,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Grille_HG-2.pdf",s1,hg,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_S_1er_gr_2016.pdf",s1,fr,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/SVT_S1_1er_gr_2016_ok.pdf",s1,svt,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_SVT_S1_1er_gr_2016.pdf",s1,svt,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Anglais_S_1er_gr_2016.pdf",s1,anglais,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Anglais_S_1er_gr_2016.pdf",s1,anglais,2016,0,1);

        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_S_1er_gr_2016.pdf",s2,fr,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_physiques_S2_1er_gr_2016.pdf",s2,pc,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Maths_S2_1er_gr_2016.pdf",s2,math,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Maths_S2_1er_gr_2016.pdf",s2,math,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/HG_L-S_1er_gr_2016-2.pdf",s2,hg,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Grille_HG-2.pdf",s2,hg,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_S_1er_gr_2016.pdf",s2,fr,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/SVT_S2_1er_gr_2016_ok.pdf",s2,svt,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_SVT_S2_1er_gr_2016.pdf",s2,svt,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Anglais_S_1er_gr_2016.pdf",s2,anglais,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Anglais_S_1er_gr_2016.pdf",s2,anglais,2016,0,1);

        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_physiques_L2_1er_gr_2016.pdf",l2,pc,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Sciences_physiques_L2_1er_gr_2016.pdf",l2,pc,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Epreuves_Anglais_LVI_1er_gr_2016.pdf",l2,anglais,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Anglais_LVI_1er_gr_2016.pdf",l2,anglais,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/HG_L-S_1er_gr_2016-2.pdf",l2,hg,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Grille_HG-2.pdf",l2,hg,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_L_1er_gr_2016.pdf",l2,fr,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/SVT_L2_1er_gr_2016.pdf",l2,svt,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_SVT_L2_1er_gr_2016.pdf",l2,svt,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Maths_L_1er_gr_2016.pdf",l2,math,2016,1,1);


        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Epreuves_Anglais_LVI_1er_gr_2016.pdf",l1,anglais,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Anglais_LVI_1er_gr_2016.pdf",l1,anglais,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/HG_L-S_1er_gr_2016-2.pdf",l1,hg,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Grille_HG-2.pdf",l1,hg,2016,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_L_1er_gr_2016.pdf",l1,fr,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Maths_L_1er_gr_2016.pdf",l1,math,2016,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_L_1er_gr_2016.pdf",l1,fr,2016,1,1);



        //----------------------2015---------------------------------------------------------
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Philo_S_1er_g_2015.pdf",s1,philo,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/ANGLAIS_SI-S1A-S2-S2A-S4-S5_2015.pdf",s1,anglais,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_ANGLAIS_SI-S1A-S2-S2A-S4-S5_2015.pdf",s1,anglais,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_Physiques_S1S3_1er_gr_2015.pdf",s1,pc,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Sciences_Physiques_S1S3_1groupe_2015.pdf",s1,pc,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_S_1er_gr_2015.pdf",s1,fr,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/SVT_S1_1er_gr_2015.pdf",s1,svt,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_SVT_S1_1er_gr_2015.pdf",s1,svt,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Maths_S1_1er_gr_2015.pdf",s1,math,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Maths_S1_1er_gr_2015.pdf",s1,math,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Histoire_Geographie_LS_1er_2015.pdf",s1,hg,2015,1,1);



        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Philo_S_1er_g_2015.pdf",s2,philo,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/ANGLAIS_SI-S1A-S2-S2A-S4-S5_2015.pdf",s2,anglais,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_ANGLAIS_SI-S1A-S2-S2A-S4-S5_2015.pdf",s2,anglais,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_Physiques_S2S2AS4S5_1er_gr_2015.pdf",s2,pc,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Sciences_Physiques_S2_1_groupe_2015.pdf",s2,pc,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_S_1er_gr_2015.pdf",s2,fr,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/SVT_S2S2AS4S5_1er_gr_2015.pdf",s2,svt,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_SVT_S2S2AS4S5_1er_gr_2015.pdf",s2,svt,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Maths_S2_1er_gr_2015.pdf",s2,math,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Maths_S2S2AS4S5_1er_gr_2015_bon.pdf",s2,math,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Histoire_Geographie_LS_1er_2015.pdf",s2,hg,2015,1,1);


        ajouterBDD("http://www.officedubac.sn/IMG/pdf/philo_L_1er_gp_2015.pdf",l2,philo,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sujet_L2_PC_1_groupe_15.pdf",l2,pc,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_L2_PC_1_er_gr_2015.pdf",l2,pc,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Anglais_Lv1_1er_gr_2015.pdf",l2,anglais,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Anglais_Lv1_1er_gr_2015.pdf",l2,anglais,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Histoire_Geographie_LS_1er_2015.pdf",l2,hg,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_L_1er_gr_2015.pdf",l2,fr,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/nonce_s_v_t_L2_1_er_gp_2015.pdf",l2,svt,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/corrige_s_v_t_L2_1er_gp_2015.pdf",l2,svt,2015,0,1);


        ajouterBDD("http://www.officedubac.sn/IMG/pdf/philo_L_1er_gp_2015.pdf",l1,philo,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Anglais_Lv1_1er_gr_2015.pdf",l1,anglais,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Anglais_Lv1_1er_gr_2015.pdf",l1,anglais,2015,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Histoire_Geographie_LS_1er_2015.pdf",l1,hg,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_L_1er_gr_2015.pdf",l1,fr,2015,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Maths_L_1er_gr_2015.pdf",l1,math,2015,1,1);


        //----------------------2014---------------------------------------------------------
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/s_philo2014.pdf",s1,philo,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sc_Phys_S1_1er_gp_2014-2.pdf",s1,pc,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Sc_Phys_S1_1er_Groupe.pdf",s1,pc,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/histo_geo_2014_1er_gpe.pdf",s1,hg,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_Francais_S_1er_gr_2014.pdf",s1,fr,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_S_V_T_S1_1_er_gp_2014.pdf",s1,svt,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_S_V_T_S_1_rer_gp2014.pdf",s1,svt,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_Maths_S1_1er_gr_2014.pdf",s1,math,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/14_G_A_01_Corrige_2014-2.pdf",s1,math,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_Anglais_S_1er_gr_2014.pdf",s1,anglais,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Anglais_S_1er_gr_2014.pdf",s1,anglais,2014,0,1);


        ajouterBDD("http://www.officedubac.sn/IMG/pdf/s_philo2014.pdf",s2,philo,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sc_Phys_S2_1er_gp_2014.pdf",s2,pc,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Sc_Phys_S2_1er_Groupe.pdf",s2,pc,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/histo_geo_2014_1er_gpe.pdf",s2,hg,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_Francais_S_1er_gr_2014.pdf",s2,fr,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_s_v_t_S2_1_er_gpe_2014.pdf",s2,svt,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/s_v_t_S2_1_er_gpe_2014.pdf",s2,svt,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/ennonce_maths_S2_1er_gpe_2014.pdf",s2,math,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_14_G_26_A_01.pdf",s2,math,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_Anglais_S_1er_gr_2014.pdf",s2,anglais,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Anglais_S_1er_gr_2014.pdf",s2,anglais,2014,0,1);


        ajouterBDD("http://www.officedubac.sn/IMG/pdf/l_philo2014.pdf",l2,philo,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/14_G_24_A_01.pdf",l2,pc,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_L2_1er_Groupe-2.pdf",l2,pc,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/anglais_LV1_1er_gpe_2014.pdf",l2,anglais,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/corrige_Anglais_LV1_1_er_gpe_2014.pdf",l2,anglais,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/histo_geo_2014_1er_gpe.pdf",l2,hg,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_Francais_L_1er_gr_2014.pdf",l2,fr,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_SVT_L2_1er_gr_2014.pdf",l2,svt,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_SVT_L2_1er_gr_2014.pdf",l2,svt,2014,0,1);




        ajouterBDD("http://www.officedubac.sn/IMG/pdf/l_philo2014.pdf",l1,philo,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/anglais_LV1_1er_gpe_2014.pdf",l1,anglais,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/corrige_Anglais_LV1_1_er_gpe_2014.pdf",l1,anglais,2014,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/histo_geo_2014_1er_gpe.pdf",l1,hg,2014,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Enonce_Francais_L_1er_gr_2014.pdf",l1,fr,2014,1,1);

        //----------------------2013---------------------------------------------------------
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Sciences_phys_S1_S3.pdf",s1,pc,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_epreuve_session_normal_PC_seri_S1_bis.pdf",s1,pc,2013,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Histoire_Geo_2013_1_er_groupe.pdf",s1,hg,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/francais_S_1er_gpe_2013_Norm.pdf",s1,fr,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/13_G_25_A_01_S_V_T_S1_1er_groupe_2013.pdf",s1,svt,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_S_V_T_S1_1er_groupe_2013.pdf",s1,svt,2013,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/13_G_18_bis_A_01_maths_S1_S3.pdf",s1,math,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/13_G_18_BIS_A01_FINI_corrige_maths_S1_S3.pdf",s1,math,2013,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/13_G_33_A_01_Anglais_S1_S2.pdf",s1,anglais,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/corrige_Anglais_S1_S22013.pdf",s1,anglais,2013,0,1);


        ajouterBDD("http://www.officedubac.sn/IMG/pdf/sciences_physiques_S2_S4_S5.pdf",s2,pc,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_epreuve_session_normale_PC_seri_S2_bis.pdf",s2,pc,2013,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Histoire_Geo_2013_1_er_groupe.pdf",s2,hg,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/francais_S_1er_gpe_2013_Norm.pdf",s2,fr,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/13_G_27_A_01_S_V_T_S2_1_er_groupe_2013.pdf",s2,svt,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_S_V_T_S2_1er_groupe_2013.pdf",s2,svt,2013,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Maths_S2_1er_gr_2013.pdf",s2,math,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_Maths_S2_1er_gr_2013.pdf",s2,math,2013,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/13_G_33_A_01_Anglais_S1_S2.pdf",s2,anglais,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/corrige_Anglais_S1_S22013.pdf",s2,anglais,2013,0,1);



        ajouterBDD("http://www.officedubac.sn/IMG/pdf/sciences_phys_1er_gpe_L2_Normale_2013.pdf",l2,pc,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/ANGLAIS_LV1_1ER_GROUPE_2013.pdf",l2,anglais,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/corrige_anglais_LV1_1er_gpe.pdf",l2,anglais,2013,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Histoire_Geo_2013_1_er_groupe.pdf",l2,hg,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_L_1er_gpe_Norm_2013.pdf",l2,fr,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/13_G_24_Bis_A_01S_V_T_L2_2013.pdf",l2,svt,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Corrige_S_V_T_L2_2013.pdf",l2,svt,2013,0,1);



        ajouterBDD("http://www.officedubac.sn/IMG/pdf/ANGLAIS_LV1_1ER_GROUPE_2013.pdf",l1,anglais,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/corrige_anglais_LV1_1er_gpe.pdf",l1,anglais,2013,0,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Histoire_Geo_2013_1_er_groupe.pdf",l1,hg,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/Francais_L_1er_gpe_Norm_2013.pdf",l1,fr,2013,1,1);
        ajouterBDD("http://www.officedubac.sn/IMG/pdf/MATHS_L_1ER_GROUPE_NORMAL_2013.pdf",l1,math,2013,1,1);


        //----------------------2012---------------------------------------------------------
        ajouterBDD("http://officedubac.sn/IMG/pdf/SCiences_Physiques_S1_1er_groupe_2012.pdf",s1,pc,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_epreuve_PC_S1_session_normale_12.pdf",s1,pc,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HG_1_er_Gpe_2012.pdf",s1,hg,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Francais_S_1_er_Gpe_2012.pdf",s1,fr,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T_S1_1_ER_GPE_2012.pdf",s1,svt,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_SVT_S1_1_er_gpe_2012.pdf",s1,svt,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/maths_S1_1er_Gpe_2012.pdf",s1,math,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_Mathematiques_S1-S3_1er_groupe_2012.pdf",s1,math,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGANGLAIS_S1-S2-S4-S5_1ER_GROUPE_2012.pdf",s1,anglais,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_Anglais_S1_S2_S4_S5_2012.pdf",s1,anglais,2012,0,1);




        ajouterBDD("http://officedubac.sn/IMG/pdf/12_G_27_A_01_sc_phys_S2.pdf",s2,pc,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_epreuve_session_normale_PC_S2.pdf",s2,pc,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HG_1_er_Gpe_2012.pdf",s2,hg,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Francais_S_1_er_Gpe_2012.pdf",s2,fr,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T_S2_1_er_Gpe_2012.pdf",s2,svt,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_S_V_T_S2_1_er_Gpe_12_G_28_A.pdf",s2,svt,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Maths_S2_1_er_Groupe_2012.pdf",s2,math,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_Maths_S2_1er_groupe_2012.pdf",s2,math,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGANGLAIS_S1-S2-S4-S5_1ER_GROUPE_2012.pdf",s2,anglais,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_Anglais_S1_S2_S4_S5_2012.pdf",s2,anglais,2012,0,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/Sciences_Physiques_L2_2012.pdf",l2,pc,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_12_G_24_A_01.pdf",l2,pc,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Anglais_LV1_1er_groupe_2012.pdf",l2,anglais,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_Anglais_LV1_1er_groupe.pdf",l2,anglais,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HG_1_er_Gpe_2012.pdf",l2,hg,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_1er_groupe_L_2012.pdf",l2,fr,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_L2_2012.pdf",l2,svt,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/CORRIGE_SVT_L2_1er_G_2012.pdf",l2,svt,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Maths_L_1_er_Gpe_2012.pdf",l2,math,2012,1,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/Anglais_LV1_1er_groupe_2012.pdf",l1,anglais,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_Anglais_LV1_1er_groupe.pdf",l1,anglais,2012,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HG_1_er_Gpe_2012.pdf",l1,hg,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_1er_groupe_L_2012.pdf",l1,fr,2012,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Maths_L_1_er_Gpe_2012.pdf",l1,math,2012,1,1);



        //----------------------2011---------------------------------------------------------
        ajouterBDD("http://officedubac.sn/IMG/pdf/philosophie_S_1er_groupe_2011.pdf",s1,philo,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/s1_physique-2.pdf",s1,pc,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrigeTS1.pdf",s1,pc,2011,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/histo_geo_1_er_gpe_1.pdf",s1,hg,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRANCAIS_S_1ER_GROUPE.pdf",s1,fr,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T_S1_1ER_GROUPE.pdf",s1,svt,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_S1_1ER_GROUPE.pdf",s1,math,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_S1_CORRIGE_1ER_GROUPE.pdf",s1,math,2011,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGLAIS_S1_S2_1ER_GROUPE.pdf",s1,anglais,2011,1,1);




        ajouterBDD("http://officedubac.sn/IMG/pdf/philosophie_S_1er_groupe_2011.pdf",s2,philo,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/s2_physique.pdf",s2,pc,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrigeTS2.pdf",s2,pc,2011,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/histo_geo_1_er_gpe_1.pdf",s2,hg,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRANCAIS_S_1ER_GROUPE.pdf",s2,fr,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T_S2-S4-S5_1ER_GROUPE.pdf",s2,svt,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_S2_1ER_GROUPE.pdf",s2,math,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGLAIS_S1_S2_1ER_GROUPE.pdf",s2,anglais,2011,1,1);




        ajouterBDD("http://officedubac.sn/IMG/pdf/philo_L_1er_gr_2011.pdf",l2,philo,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/sciences_physiques_L2_1er_Gpe.pdf",l2,svt,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/CorrigeL2.pdf",l2,svt,2011,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/LVI_ANGLAIS.pdf",l2,anglais,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/histo_geo_1_er_gpe_1.pdf",l2,hg,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRANCAIS_L_1ER_GROUPE.pdf",l2,fr,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T_L2_1ER_GROUPE.pdf",l2,svt,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_L_1ER_GROUPE.pdf",l2,math,2011,1,1);



        ajouterBDD("http://officedubac.sn/IMG/pdf/philo_L_1er_gr_2011.pdf",l1,philo,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/LVI_ANGLAIS.pdf",l1,anglais,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/histo_geo_1_er_gpe_1.pdf",l1,hg,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRANCAIS_L_1ER_GROUPE.pdf",l1,fr,2011,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_L_1ER_GROUPE.pdf",l1,math,2011,1,1);


        //----------------------2010---------------------------------------------------------
        ajouterBDD("http://officedubac.sn/IMG/pdf/philo_S_2010.pdf",s1,philo,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/sciences_physiques_S1S3_1er_gp.pdf",s1,pc,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/PCcorrigeS1.pdf",s1,pc,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HISTO-GEO_1er_Grpe_2010.pdf",s1,hg,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_S1S2S3S4S5.pdf",s1,fr,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S.V.T_S1_1ER_GROUPE.pdf",s1,svt,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/CORRIGE_S.V.T_S1_DU_1ER_GROUPE.pdf",s1,svt,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/10G18bisA01.pdf",s1,math,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/10G18bisA01-2.pdf",s1,math,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGLAIS_S1_S2_s4_s5.pdf",s1,anglais,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_A_N_G_L_A_I_S.pdf",s1,anglais,2010,0,1);




        ajouterBDD("http://officedubac.sn/IMG/pdf/philo_S_2010.pdf",s2,philo,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/PCs2s4s5.pdf",s2,pc,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrigeS2.pdf",s2,pc,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HISTO-GEO_1er_Grpe_2010.pdf",s2,hg,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_S1S2S3S4S5.pdf",s2,fr,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S.V.T_S2-S2A-S4-S5_1ER_GROUPE.pdf",s2,svt,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/CORRIGE_S.V.T_S2-S2A-S4-S5_1ER_GROUPE.pdf",s2,svt,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_S2_1er_grpe_2010.pdf",s2,math,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrigeS2-2.pdf",s2,math,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGLAIS_S1_S2_s4_s5.pdf",s2,anglais,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_A_N_G_L_A_I_S.pdf",s2,anglais,2010,0,1);




        ajouterBDD("http://officedubac.sn/IMG/pdf/philo_L_2010.pdf",l2,philo,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Directives_Philo.pdf",l2,philo,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/PC_sujet_L2_1er_gr.pdf",l2,pc,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrigeL2.pdf",l2,pc,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Anglais_LVI_1er_groupe_2010.pdf",l2,anglais,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_LV1_Anglais_1_er_gp.pdf",l2,anglais,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HISTO-GEO_1er_Grpe_2010.pdf",l2,hg,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRANCAIS_L_1er_grpe_2010.pdf",l2,fr,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T_L2_1er_groupe_2010.pdf",l2,svt,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_SVTL2.pdf",l2,svt,2010,0,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/philo_L_2010.pdf",l1,philo,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Directives_Philo.pdf",l1,philo,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Anglais_LVI_1er_groupe_2010.pdf",l1,anglais,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_LV1_Anglais_1_er_gp.pdf",l1,anglais,2010,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HISTO-GEO_1er_Grpe_2010.pdf",l1,hg,2010,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRANCAIS_L_1er_grpe_2010.pdf",l1,fr,2010,1,1);


        //----------------------2009---------------------------------------------------------
        ajouterBDD("http://officedubac.sn/IMG/pdf/philosophie_S_1er_groupe.pdf",s1,philo,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/sciences_physiques_S1_S3_1groupe_2009.pdf",s1,pc,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_sciences_physiques_1groupe_2009.pdf",s1,pc,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Histoire_Geographie_TOUTES_SERIES.pdf",s1,hg,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_premier_groupe_2009_S.pdf",s1,fr,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_S11er_groupe.pdf",s1,svt,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrigeSVT_s11er_groupe.pdf",s1,svt,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/09G18bisA01.pdf",s1,math,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrigeS1S3.pdf",s1,math,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANG_S2_S4_S5_S1.pdf",s1,anglais,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_A_N_G_L_A_I_S1.pdf",s1,anglais,2009,0,1);



        ajouterBDD("http://officedubac.sn/IMG/pdf/philosophie_S_1er_groupe.pdf",s2,philo,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/sciences_physiques_s2_s2A_s4_S5_1groupe_2009.pdf",s2,pc,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_sciences_physiques_S2_premier_groupe.pdf",s2,pc,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Histoire_Geographie_TOUTES_SERIES.pdf",s2,hg,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_premier_groupe_2009_S.pdf",s2,fr,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_S2-S2A-S4-S51er_groupe.pdf",s2,svt,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/CORRIGE_SVT_S2-S2A-S4-S5_1er_groupe.pdf",s2,svt,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/maths_S2_S2A_S4_S5.pdf",s2,math,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/09G26bisA01Cor.pdf",s2,math,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANG_S2_S4_S5_S1.pdf",s2,anglais,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_A_N_G_L_A_I_S1.pdf",s2,anglais,2009,0,1);




        ajouterBDD("http://officedubac.sn/IMG/pdf/philosophie_L_1er_groupe.pdf",l2,philo,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/PC_L2.pdf",l2,pc,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Corrige_L2_1er_gr_09.pdf",l2,pc,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/anglais_lv1_1groupe_2009.pdf",l2,anglais,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/CORRIGE_A_N_G_L_A_I_S_LVI_1groupe_2009.pdf",l2,anglais,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Histoire_Geographie_TOUTES_SERIES.pdf",l2,hg,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_premier_groupe_2009_L2_L1a_L1b_L_1.pdf",l2,fr,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_L2.pdf",l2,svt,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_svt_L2.pdf",l2,svt,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/maths_L1er_groupe.pdf",l2,math,2009,1,1);




        ajouterBDD("http://officedubac.sn/IMG/pdf/philosophie_L_1er_groupe.pdf",l1,philo,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/anglais_lv1_1groupe_2009.pdf",l1,anglais,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/CORRIGE_A_N_G_L_A_I_S_LVI_1groupe_2009.pdf",l1,anglais,2009,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Histoire_Geographie_TOUTES_SERIES.pdf",l1,hg,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_premier_groupe_2009_L2_L1a_L1b_L_1.pdf",l1,fr,2009,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/maths_L1er_groupe.pdf",l1,math,2009,1,1);


        //----------------------2008---------------------------------------------------------
        ajouterBDD("http://officedubac.sn/IMG/pdf/physique_s1_s3.pdf",s1,pc,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/phys_corrige_S1_S3.pdf",s1,pc,2008,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/hg_ttes_series.pdf",s1,hg,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_S.pdf",s1,fr,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T__S1.pdf",s1,svt,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_S1.pdf",s1,svt,2008,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Math08s1.pdf",s1,math,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corrige_maths1.pdf",s1,math,2008,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/anglais_S1-S2-S4-S5.pdf",s1,anglais,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/cor_anglais_s.pdf",s1,anglais,2008,0,1);



        ajouterBDD("http://officedubac.sn/IMG/pdf/Phys_S2_S2A_S4_S5.pdf",s2,pc,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/sciences_physiques_S2.pdf",s2,pc,2008,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/hg_ttes_series.pdf",s2,hg,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_S.pdf",s2,fr,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T_S2_S2A_.pdf",s2,svt,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/CORRIGE_SCIENCES_DE_LA_VIE_ET_DE_LA_TERRE.pdf",s2,svt,2008,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_S2.pdf",s2,math,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/anglais_S1-S2-S4-S5.pdf",s2,anglais,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/cor_anglais_s.pdf",s2,anglais,2008,0,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/sciences_physiques_L.pdf",l2,pc,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGLAIS_LV1.pdf",l2,anglais,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corr_ang__LV1.pdf",l2,anglais,2008,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/hg_ttes_series.pdf",l2,hg,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRANCAIS_L_2_L1a_L1b_L_1.pdf",l2,fr,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/S_V_T_L2.pdf",l2,svt,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Correction_S_V_T_L2.pdf",l2,svt,2008,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Maths_L.pdf",l2,math,2008,1,1);



        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGLAIS_LV1.pdf",l1,anglais,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/corr_ang__LV1.pdf",l1,anglais,2008,0,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/hg_ttes_series.pdf",l1,hg,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRANCAIS_L_2_L1a_L1b_L_1.pdf",l1,fr,2008,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Maths_L.pdf",l1,math,2008,1,1);


        //----------------------2007---------------------------------------------------------
        ajouterBDD("http://officedubac.sn/IMG/pdf/Philo_S_1er_GR.pdf",s1,philo,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SC_PHYS_S1_1er_Gr.pdf",s1,pc,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HG_1_er_Gr.pdf",s1,hg,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Francais_S_1_er_Gr.pdf",s1,fr,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/07G1S1Juillet.pdf",s1,math,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANG_S1_S2_1er_Gr.pdf",s1,anglais,2007,1,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/Philo_S_1er_GR.pdf",s2,philo,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SC_PHYS_S2_1er.pdf",s2,pc,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HG_1_er_Gr.pdf",s2,hg,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Francais_S_1_er_Gr.pdf",s2,fr,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_S_1er_Gr.pdf",s2,svt,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Maths_S2_1er_GR.pdf",s2,math,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANG_S1_S2_1er_Gr.pdf",s2,anglais,2007,1,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/Philosophie_terminale.pdf",l2,philo,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SC_PHYS_L2_1er_Gr.pdf",l2,pc,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Anglais_LV1_terminale.pdf",l2,anglais,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HG_1_er_Gr.pdf",l2,hg,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRancais_L_1er_gr.pdf",l2,fr,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_L2_1er_GR.pdf",l2,svt,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_L_1er_GR.pdf",l2,math,2007,1,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/Philosophie_terminale.pdf",l1,philo,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Anglais_LV1_terminale.pdf",l1,anglais,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/HG_1_er_Gr.pdf",l1,hg,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/FRancais_L_1er_gr.pdf",l1,fr,2007,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/MATHS_L_1er_GR.pdf",l1,math,2007,1,1);


        //----------------------2006---------------------------------------------------------
        ajouterBDD("http://officedubac.sn/IMG/pdf/Sc_Phy_S1_S3_1er_gr.pdf",s1,pc,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_serie_S-2.pdf",s1,fr,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Philosophie_S_1er_Gr.pdf",s1,philo,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_S1_1er_Gr.pdf",s1,svt,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Mathematiques_S1_S3_1er_Gr.pdf",s1,math,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGLAIS_S_2006.pdf",s1,anglais,2006,1,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/Sc_Physiques_S2_1er_Gr.pdf",s2,pc,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/francais_serie_S-2.pdf",s2,fr,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Philosophie_S_1er_Gr.pdf",s2,philo,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_S2_1er_Gr.pdf",s2,svt,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/ANGLAIS_S_2006.pdf",s2,anglais,2006,1,1);



        ajouterBDD("http://officedubac.sn/IMG/pdf/Sc_Physiques_L2_1er_Gr.pdf",l2,pc,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Anglais_LV1_1er_gr.pdf",l2,anglais,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Francais_L_1er_Gr.pdf",l2,fr,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/philosophie_serie_L.pdf",l2,philo,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/SVT_L2_1er_Gr.pdf",l2,svt,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Mathematiques_L_1er_Gr.pdf",l2,math,2006,1,1);


        ajouterBDD("http://officedubac.sn/IMG/pdf/Anglais_LV1_1er_gr.pdf",l1,anglais,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Francais_L_1er_Gr.pdf",l1,fr,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/philosophie_serie_L.pdf",l1,philo,2006,1,1);
        ajouterBDD("http://officedubac.sn/IMG/pdf/Mathematiques_L_1er_Gr.pdf",l1,math,2006,1,1);


    }

}
