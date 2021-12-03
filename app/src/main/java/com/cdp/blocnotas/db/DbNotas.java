package com.cdp.blocnotas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.cdp.blocnotas.entidades.Notas;

import java.util.ArrayList;

public class DbNotas extends DbHelper {

    Context context;

    public DbNotas(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarNotas(String tipo, String titulo, String contenido, String imagen, String rutaGaleria, String rutaVideo, String rutaGalVideo, String rutaVoz) {

        long id = 0;

        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("tipo", tipo);
            values.put("titulo", titulo);
            values.put("contenido", contenido);
            values.put("imagen", imagen);
            values.put("galeria", rutaGaleria);
            values.put("video", rutaVideo);
            values.put("videoGal", rutaGalVideo);
            values.put("rutaVoz", rutaVoz);

            id = db.insert(TABLE_NOTAS, null, values);
        } catch (Exception ex) {
            ex.toString();
        }

        return id;
    }

    public ArrayList<Notas> mostrarNotas() {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<Notas> listaNotas = new ArrayList<>();
        Notas nota;
        Cursor cursorNotas;

        cursorNotas = db.rawQuery("SELECT * FROM " + TABLE_NOTAS + " ORDER BY titulo ASC", null);

        if (cursorNotas.moveToFirst()) {
            do {
                nota = new Notas();
                nota.setId(cursorNotas.getInt(0));
                nota.setTipo(cursorNotas.getString(1));
                nota.setTitulo(cursorNotas.getString(2));
                nota.setContenido(cursorNotas.getString(3));
                nota.setImagen(cursorNotas.getString(4));
                nota.setRutaGaleria(cursorNotas.getString(5));
                nota.setVideo(cursorNotas.getString(6));
                nota.setVideoGaleria(cursorNotas.getString(7));
                nota.setNotavoz(cursorNotas.getString(8));
                listaNotas.add(nota);
            } while (cursorNotas.moveToNext());
        }

        cursorNotas.close();

        return listaNotas;
    }

    public Notas verNota(int id) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Notas nota = null;
        Cursor cursorNotas;

        cursorNotas = db.rawQuery("SELECT * FROM " + TABLE_NOTAS + " WHERE id = " + id + " LIMIT 1", null);

        if (cursorNotas.moveToFirst()) {
            nota = new Notas();
            nota.setId(cursorNotas.getInt(0));
            nota.setTipo(cursorNotas.getString(1));
            nota.setTitulo(cursorNotas.getString(2));
            nota.setContenido(cursorNotas.getString(3));
            nota.setImagen(cursorNotas.getString(4));
            nota.setRutaGaleria(cursorNotas.getString(5));
            nota.setVideo(cursorNotas.getString(6));
            nota.setVideoGaleria(cursorNotas.getString(7));
            nota.setNotavoz(cursorNotas.getString(8));
        }

        cursorNotas.close();

        return nota;
    }

    public boolean editarNotas(int id, String tipo, String titulo, String contenido) {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_NOTAS + " SET tipo = '" + tipo + "', titulo = '" + titulo + "', contenido = '" + contenido + "' WHERE id='" + id + "' ");
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }

    public boolean eliminarNota(int id) {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_NOTAS + " WHERE id = '" + id + "'");
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }
}
