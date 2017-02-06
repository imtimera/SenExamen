package com.example.netbook.senexamen;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

public class MyContentProvider extends ContentProvider {

    final String CONTENT_TYPE = "vnd.android.cursor.dir/com.example.netbook.senexamen";
    final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.example.netbook.senexamen;";
    private static final String AUTHORITY = "com.example.netbook.senexamen";

    private static final String EXAMEN_PATH = "examen";

    private static final String URL_EXAMEN = "content://" + AUTHORITY + "/" + EXAMEN_PATH;

    public static final Uri EXAMEN_URI = Uri.parse(URL_EXAMEN);

    public static final int EXAMEN_TABLE = 1;
    public static final int URL = 2;
    public static final int SERIE = 3;
    public static final int MATIERE = 4;
    public static final int TYPE = 5;
    public static final int ANNEE = 6;
    public static final int STOCKAGE = 7;




    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(AUTHORITY, EXAMEN_PATH, EXAMEN_TABLE);
        matcher.addURI(AUTHORITY, EXAMEN_PATH + "/URL/#", URL);
        matcher.addURI(AUTHORITY, EXAMEN_PATH + "/SERIE/#", SERIE);
        matcher.addURI(AUTHORITY, EXAMEN_PATH + "/MATIERE/#", MATIERE);
        matcher.addURI(AUTHORITY, EXAMEN_PATH + "/TYPE/#", TYPE);
        matcher.addURI(AUTHORITY, EXAMEN_PATH + "/ANNEE/#", ANNEE);
        matcher.addURI(AUTHORITY, EXAMEN_PATH + "/STOCKAGE/#", STOCKAGE);


    }

    private DataBase baseDD;

    private SQLiteDatabase db;


    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int r = 0;
        db = baseDD.getWritableDatabase();
        Log.d("delete uri", uri.toString());
        switch (matcher.match(uri)) {
            case EXAMEN_TABLE:
                r = db.delete(DataBase.EXAMEN, selection, selectionArgs);
                break;
            case URL:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.delete(DataBase.EXAMEN, DataBase.URL + " = " + id, null);
                else
                    r = db.delete(DataBase.EXAMEN, DataBase.URL + " = " + id + " and" + selection, selectionArgs);
                break;
            case SERIE:
                String serie = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.delete(DataBase.EXAMEN, DataBase.SERIE + " = " + serie, null);
                else
                    r = db.delete(DataBase.EXAMEN, DataBase.SERIE + " = " + serie + " and" + selection, selectionArgs);
                break;
            case MATIERE:
                String matiere = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.delete(DataBase.EXAMEN, DataBase.MATIERE + " = " + matiere, null);
                else
                    r = db.delete(DataBase.EXAMEN, DataBase.MATIERE + " = " + matiere + " and" + selection, selectionArgs);
                break;
            case ANNEE:
                String annee = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.delete(DataBase.EXAMEN, DataBase.ANNEE + " = " + annee, null);
                else
                    r = db.delete(DataBase.EXAMEN, DataBase.ANNEE + " = " + annee + " and" + selection, selectionArgs);
                break;

            case STOCKAGE:
                String stockage = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.delete(DataBase.EXAMEN, DataBase.STOCKAGE + " = " + stockage, null);
                else
                    r = db.delete(DataBase.EXAMEN, DataBase.STOCKAGE + " = " + stockage + " and" + selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return r;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (matcher.match(uri)) {
            case EXAMEN_TABLE:
                return CONTENT_TYPE;
            case SERIE:
                return CONTENT_ITEM_TYPE;
            case MATIERE:
                return CONTENT_ITEM_TYPE;
            case ANNEE:
                return CONTENT_ITEM_TYPE;
            case STOCKAGE:
                return CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        db = baseDD.getWritableDatabase();
        int code =matcher.match(uri);
        Uri.Builder builder = new Uri.Builder();
        long id = 0;
        switch (code) {
            case EXAMEN_TABLE:
                id = db.insert(DataBase.EXAMEN, null, values);
                builder.appendPath("examen");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null);
        builder.authority(AUTHORITY);
        builder = ContentUris.appendId(builder, id);
        return builder.build();
    }


    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        try {
            baseDD = new DataBase(getContext());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        db = baseDD.getReadableDatabase();
        int code = matcher.match(uri);
        Cursor cursor=null;
        switch (code) {
            case EXAMEN_TABLE:
                cursor = db.query(EXAMEN_PATH, new String[]{DataBase.URL, DataBase.SERIE,DataBase.MATIERE, DataBase.ANNEE,DataBase.STOCKAGE}, selection, selectionArgs, null, null, sortOrder);
                break;
            case URL:
                long id = ContentUris.parseId(uri);
                cursor = db.query(EXAMEN_PATH, new String[]{DataBase.URL, DataBase.SERIE,DataBase.MATIERE, DataBase.ANNEE,DataBase.STOCKAGE},DataBase.URL + "="+id,null, null, null, null);
                break;
            case SERIE:
                long serie = ContentUris.parseId(uri);
                cursor = db.query(EXAMEN_PATH, new String[]{DataBase.URL, DataBase.SERIE,DataBase.MATIERE, DataBase.ANNEE,DataBase.STOCKAGE},DataBase.SERIE + "="+serie,null, null, null, null);
                break;
            case MATIERE:
                long matiere = ContentUris.parseId(uri);
                cursor = db.query(EXAMEN_PATH, new String[]{DataBase.URL, DataBase.SERIE,DataBase.MATIERE, DataBase.ANNEE,DataBase.STOCKAGE},DataBase.MATIERE + "="+matiere,null, null, null, null);
                break;
            case ANNEE:
                long annee = ContentUris.parseId(uri);
                cursor = db.query(EXAMEN_PATH, new String[]{DataBase.URL, DataBase.SERIE,DataBase.MATIERE, DataBase.ANNEE,DataBase.STOCKAGE},DataBase.ANNEE + "="+annee,null, null, null, null);
                break;

            case STOCKAGE:
                long stockage = ContentUris.parseId(uri);
                cursor = db.query(EXAMEN_PATH, new String[]{DataBase.URL, DataBase.SERIE,DataBase.MATIERE, DataBase.ANNEE,DataBase.STOCKAGE},DataBase.STOCKAGE + "="+stockage,null, null, null, null);
                break;
            default:
                Log.d("Uri provider = ", uri.toString());
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int r=0;
        db = baseDD.getReadableDatabase();
        int code = matcher.match(uri);
        Cursor cursor=null;
        switch (code) {
            case EXAMEN_TABLE:
                r = db.update(DataBase.EXAMEN, values, selection, selectionArgs);
                break;
            case URL:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.update(DataBase.EXAMEN, values, DataBase.URL + "=" + id, selectionArgs);
                else
                    r = db.update(DataBase.EXAMEN, values, DataBase.URL + " = " + id + " and" + selection, selectionArgs);
                break;
            case SERIE:
                String serie = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.update(DataBase.EXAMEN, values, DataBase.SERIE + "=" + serie, selectionArgs);
                else
                    r = db.update(DataBase.EXAMEN, values, DataBase.SERIE + " = " + serie + " and" + selection, selectionArgs);
                break;
            case MATIERE:
                String matiere = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.update(DataBase.EXAMEN, values, DataBase.MATIERE + "=" + matiere, selectionArgs);
                else
                    r = db.update(DataBase.EXAMEN, values, DataBase.MATIERE + " = " + matiere + " and" + selection, selectionArgs);
                break;

            case ANNEE:
                String annee = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.update(DataBase.EXAMEN, values, DataBase.ANNEE + "=" + annee, selectionArgs);
                else
                    r = db.update(DataBase.EXAMEN, values, DataBase.ANNEE + " = " + annee + " and" + selection, selectionArgs);
                break;

            case STOCKAGE:
                String stockage = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    r = db.update(DataBase.EXAMEN, values, DataBase.STOCKAGE + "=" + stockage, selectionArgs);
                else
                    r = db.update(DataBase.EXAMEN, values, DataBase.STOCKAGE + " = " + STOCKAGE + " and" + selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange (uri, null);
                return r;
        }
}
