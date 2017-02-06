package com.example.netbook.senexamen;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.SslError;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Apercu extends AppCompatActivity {
    Toolbar toolbar;
    WebView wv;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apercu);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wv=(WebView)findViewById(R.id.web);
        Intent i=getIntent();
        String url=i.getStringExtra("url");//"http://www.officedubac.sn/IMG/pdf/Sciences_physiques_S1S3_1er_gr_2016.pdf";
        wv.getSettings().setJavaScriptEnabled(true);
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
        wv.loadUrl(url);
        //wv.loadUrl("https://www.irif.fr/~zielonka/");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.Télécharger:

                return true;

            case R.id.info:
                this.setTheme(android.R.style.Holo_ButtonBar_AlertDialog);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);//new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                dlgAlert.setTitle("Informations");
                dlgAlert.setMessage("Cette application a été developpé pour aider " +
                                    "les eleves de terminale (S/G/L) à préparer leur bac .\n" +
                                    "Cependant son utilisation necessite une connection internet" );
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Bon courage!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlgAlert.setIcon(android.R.drawable.ic_dialog_alert);


                dlgAlert.create().show();
                return true;

            case R.id.corriger:
                /**A Faire
                 * */
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
