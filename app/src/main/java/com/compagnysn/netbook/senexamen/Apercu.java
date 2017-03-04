package com.compagnysn.netbook.senexamen;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.support.v7.widget.ShareActionProvider;
import android.widget.Toast;

import java.io.File;

public class Apercu extends AppCompatActivity {
    Toolbar toolbar;
    WebView wv;
    final Activity activity = this;
    private ShareActionProvider mShareActionProvider;
    private String url, serie, matiere, type;
    private int annee, typeCur;
    private ActionBar actionBar;
    Cursor cur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_apercu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wv = (WebView) findViewById(R.id.web);
        Intent i = getIntent();
        url = i.getStringExtra("url");
        serie = i.getStringExtra("serie");
        annee = Integer.parseInt(i.getStringExtra("annee"));
        matiere = i.getStringExtra("matiere");
        type = i.getStringExtra("type");
        wv.getSettings().setJavaScriptEnabled(true);
        typeCur = Integer.parseInt(type.toString());
        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        wv.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        wv.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        // You could also hide the action Bar
        // getSupportActionBar().hide();

        // Show the Up button in the action bar.
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //mShareActionProvider.setShareIntent(sharedIntent);
        return true;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
        /*Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"SText");
        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(intent);
    */
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.Télécharger:
                download();
                return true;

            case R.id.info:
                this.setTheme(android.R.style.Animation_Translucent);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                dlgAlert.setTitle("Informations");
                dlgAlert.setMessage("Cette application a été développée à destination des élèves de terminale (S1~S2~S3~S4/L1~L2) du Sénégal.\n\n" +
                        "Dans un but de vous aider à préparer l'examen du baccalauréat, elle regroupe les sujets des examens de 2006 à 2016, accompagnés de correction, son utilisation nécessite une connexion internet.\n" +
                        "\n" +
                        "Vous pouvez, à tout moment basculer du sujet à la correction d'un examen et vice versa, vous pouvez aussi partager ou sauvegarder l’énoncé en local sur votre appareil Android.\n \n" +
                        "Des questions ou des remarques ? Contacter nous à " + "imtimera1@gmail.com");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Bon courage!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlgAlert.setIcon(android.R.drawable.ic_dialog_info);


                dlgAlert.create().show();
                return true;

            case R.id.corriger:
                int type2 = 1 - typeCur;
                cur = getContentResolver().query(MyContentProvider.EXAMEN_URI, null,
                        DataBase.MATIERE + " = ? " + " AND " + DataBase.SERIE + " = ? " + " AND " + DataBase.ANNEE + " = ? " + " AND " + DataBase.TYPE + " = ? ",
                        new String[]{matiere, serie, annee + "", type2 + ""}, null);

                int i = cur.getCount();
                cur.moveToFirst();
                if (i > 0) {
                    typeCur = type2;
                    if (isConnectedInternet() == true) {
                        url = cur.getString(0);
                        wv.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
                    } else {
                        Toast t = Toast.makeText(this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_LONG);
                        t.show();
                    }
                } else {
                    Toast t = Toast.makeText(this, "Malheureusement la correction de ce sujet n'existe pas :( ", Toast.LENGTH_LONG);
                    t.show();
                }
                return true;
            case R.id.share:
                Log.i("matcnoo", "Button share ok !");
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is the share content body";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SenExamen");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(sharingIntent, "SenExamen: Partager ce sujet via"));
                return true;
        }
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return super.onOptionsItemSelected(item);
    }


    public boolean isConnectedInternet() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

        //return true;
    }


    public void download() {
        Uri uri = Uri.parse(url);

        File dir=Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS).getParentFile();
        final File dirsave = new File(dir,"SenExamen");
        if (!dirsave.exists()){
            //dirsave.setWritable(true);
            //dirsave.setReadable(true);
            boolean b = dirsave.mkdirs();
            Log.d("SAVE","Creation of "+dirsave+" = "+b);
        }

        DownloadManager.Request request = new DownloadManager.Request(uri);
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Téléchargement en cours");
        request.setDescription("Bac " + matiere + " " + serie + " " + annee);
        request.allowScanningByMediaScanner();
        final String nom = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url));

        request.setDestinationInExternalPublicDir("SenExamen/",nom);
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long myDownloadReference = dm.enqueue(request);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ref = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (myDownloadReference == ref) {
                    //Charger le fichier telecharger
                    Toast an = Toast.makeText(Apercu.this, "Téléchargement terminé", Toast.LENGTH_LONG);
                    an.show();
                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(DataBase.STOCKAGE,dirsave.getAbsolutePath()+"/"+nom);
                    cr.update(MyContentProvider.EXAMEN_URI,values,DataBase.URL + " = ? ",new String[]{url});
                    cur = getContentResolver().query(MyContentProvider.EXAMEN_URI, null,
                            DataBase.MATIERE + " = ? " + " AND " + DataBase.SERIE + " = ? " + " AND " + DataBase.ANNEE + " = ? " + " AND " + DataBase.TYPE + " = ? ",
                            new String[]{matiere, serie, annee + "", type + ""}, null);
                    cur.moveToFirst();
                    //Toast an1 = Toast.makeText(Apercu.this, cur.getString(0)+cur.getString(5), Toast.LENGTH_LONG);
                    //an1.show();
                }
            }
        };
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
