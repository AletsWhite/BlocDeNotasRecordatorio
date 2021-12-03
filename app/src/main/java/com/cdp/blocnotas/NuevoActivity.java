package com.cdp.blocnotas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.cdp.blocnotas.db.DbNotas;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class NuevoActivity extends AppCompatActivity {

    MediaPlayer mediaPlayerNuevo;

    EditText txtTipo, txtTitulo, txtContenido;
    Button btnGuarda, btnCamara, btnVoz, btnGrabar;
    ImageView imgView;
    VideoView videoView;
    String rutaImagen, nombreImagen = " ", rutaVoz, UriPath, rutaVideo, UriVideo;
    private TextView notificationsTime;
    private int alarmID = 1;
    private SharedPreferences settings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NuevoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }

        btnCamara = findViewById(R.id.btnCamara);
        imgView = findViewById(R.id.imFoto);
        txtTipo = findViewById(R.id.txtTipo);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtContenido = findViewById(R.id.txtContenido);
        btnGuarda = findViewById(R.id.btnGuarda);
        btnVoz = findViewById(R.id.btnNotaVoz);
        videoView = findViewById(R.id.videoView);


        rutaVoz = getIntent().getStringExtra("archivoUri");

        btnVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NotaVozActivity.class);
                startActivity(intent);
            }
        });
        settings = getSharedPreferences(getString(R.string.encabezado), Context.MODE_PRIVATE);

        String hour, minute;

        hour = settings.getString("hour","");
        minute = settings.getString("minute","");

        notificationsTime = (TextView) findViewById(R.id.notifications_time);

        if(hour.length() > 0)
        {
            notificationsTime.setText(hour + ":" + minute);
        }

        findViewById(R.id.change_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NuevoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String finalHour, finalMinute;

                        finalHour = "" + selectedHour;
                        finalMinute = "" + selectedMinute;
                        if (selectedHour < 10) finalHour = "0" + selectedHour;
                        if (selectedMinute < 10) finalMinute = "0" + selectedMinute;
                        notificationsTime.setText(finalHour + ":" + finalMinute);

                        Calendar today = Calendar.getInstance();

                        today.set(Calendar.HOUR_OF_DAY, selectedHour);
                        today.set(Calendar.MINUTE, selectedMinute);
                        today.set(Calendar.SECOND, 0);

                        SharedPreferences.Editor edit = settings.edit();
                        edit.putString("hour", finalHour);
                        edit.putString("minute", finalMinute);

                        //SAVE ALARM TIME TO USE IT IN CASE OF REBOOT
                        edit.putInt("alarmID", alarmID);
                        edit.putLong("alarmTime", today.getTimeInMillis());

                        edit.commit();

                        Toast.makeText(NuevoActivity.this, getString(R.string.changed_to, finalHour + ":" + finalMinute), Toast.LENGTH_LONG).show();

                        Utils.setAlarm(alarmID, today.getTimeInMillis(), NuevoActivity.this);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.select_time));
                mTimePicker.show();

            }
        });

        btnGuarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!txtTipo.getText().toString().equals("") && !txtTitulo.getText().toString().equals("")) {

                    DbNotas dbNotas = new DbNotas(NuevoActivity.this);
                    long id = dbNotas.insertarNotas(txtTipo.getText().toString(), txtTitulo.getText().toString(), txtContenido.getText().toString(), rutaImagen, UriPath, rutaVideo, UriVideo, rutaVoz);

                    if (id > 0) {
                        Toast.makeText(NuevoActivity.this, "REGISTRO GUARDADO", Toast.LENGTH_LONG).show();
                        limpiar();
                    } else {
                        Toast.makeText(NuevoActivity.this, "ERROR AL GUARDAR REGISTRO", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NuevoActivity.this, "DEBE LLENAR LOS CAMPOS OBLIGATORIOS", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamara();
            }
        });
    }

    private void limpiar() {
        txtTipo.setText("");
        txtTitulo.setText("");
        txtContenido.setText("");
    }

    private void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            File imagenArchivo = null;

            try {
                imagenArchivo = crearImagen();

            } catch (IOException e) {
                Log.e("Error", e.toString());
            }
            if(imagenArchivo!=null){

                Uri fotoUri = FileProvider.getUriForFile(this, "com.cdp.blocnotas.fileprovider",imagenArchivo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(intent, 1);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            Bitmap imgBitMap = BitmapFactory.decodeFile(rutaImagen);
            imgView.setImageBitmap(imgBitMap);
        }
        if(requestCode == 10 &&resultCode == RESULT_OK){
            Uri path = data.getData();
            UriPath = path.toString();
            Toast.makeText(getApplicationContext(), UriPath, Toast.LENGTH_SHORT).show();
            imgView.setImageURI(path);
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            rutaVideo = videoUri.toString();
            videoView.setVideoURI(videoUri);
        }

        if(requestCode == 5 &&resultCode == RESULT_OK){
            Uri path = data.getData();
            UriVideo = path.toString();
            Toast.makeText(getApplicationContext(), UriPath, Toast.LENGTH_SHORT).show();
            videoView.setVideoURI(path);
        }
    }

    private File crearImagen() throws IOException {
        nombreImagen = "picture_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);

        rutaImagen = imagen.getAbsolutePath();
        return imagen;
    }



    public static void setAlarm(int i, long timestamp, Context ctx){
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(ctx, i, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_secundario, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuGaleria:
                abrirGaleria();
                return true;

            case R.id.menuGrabar:
                TomarVideo();

                return true;

            case R.id.videoGaleria:
                abrirVideos();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la aplicacion"), 10);
    }

    static final int REQUEST_VIDEO_CAPTURE = 1;

    public void TomarVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    private void abrirVideos(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la aplicacion"), 5);
    }

    public void reproducirNuevo(View view) {
        try{
            mediaPlayerNuevo = new MediaPlayer();
            mediaPlayerNuevo.setDataSource(rutaVoz);
            mediaPlayerNuevo.prepare();
            mediaPlayerNuevo.start();
            Toast.makeText(this, "Reproduciendo...", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}