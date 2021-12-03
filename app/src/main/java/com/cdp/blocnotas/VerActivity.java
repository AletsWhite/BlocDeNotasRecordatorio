package com.cdp.blocnotas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cdp.blocnotas.db.DbNotas;
import com.cdp.blocnotas.entidades.Notas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VerActivity extends AppCompatActivity {

    EditText txtTipo, txtTitulo, txtContenido;
    Button btnGuarda, btnRec;
    FloatingActionButton fabEditar, fabEliminar;
    ImageView imgviewVer;
    String notaVoz = " ";
    VideoView videoGrabado;
    MediaPlayer mediaPlayerVer;

    Notas nota;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ver);

        txtTipo = findViewById(R.id.txtTipo);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtContenido = findViewById(R.id.txtContenido);
        fabEditar = findViewById(R.id.fabEditar);
        fabEliminar = findViewById(R.id.fabEliminar);
        btnGuarda = findViewById(R.id.btnGuarda);
        btnGuarda.setVisibility(View.INVISIBLE);
        imgviewVer = findViewById(R.id.imageViewVer);
        videoGrabado = findViewById(R.id.videoGrabado);

        btnRec = (Button)findViewById(R.id.btn_rec);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                id = Integer.parseInt(null);
            } else {
                id = extras.getInt("ID");
            }
        } else {
            id = (int) savedInstanceState.getSerializable("ID");
        }

        final DbNotas dbNotas = new DbNotas(VerActivity.this);
        nota = dbNotas.verNota(id);

        if(nota != null){
            txtTipo.setText(nota.getTipo());
            txtTitulo.setText(nota.getTitulo());
            txtContenido.setText(nota.getContenido());
            txtTipo.setInputType(InputType.TYPE_NULL);
            txtTitulo.setInputType(InputType.TYPE_NULL);
            txtContenido.setInputType(InputType.TYPE_NULL);

            if(nota.getImagen()!=null){
                Uri verImg = Uri.parse(nota.getImagen());
                imgviewVer.setImageURI(verImg);
            } else {
                Toast.makeText(getApplicationContext(), "no se tomo foto", Toast.LENGTH_SHORT).show();
            }

            if(nota.getRutaGaleria()!=null){
                Uri verGal = Uri.parse(nota.getRutaGaleria());
                imgviewVer.setImageURI(verGal);
            } else{
                Toast.makeText(getApplicationContext(), "no se agrego foto", Toast.LENGTH_SHORT).show();
            }
            if(nota.getVideo()!=null){
                Uri verVid = Uri.parse(nota.getVideo());
                videoGrabado.setVideoURI(verVid);
            } else {
                Toast.makeText(getApplicationContext(), "no se tomo video", Toast.LENGTH_SHORT).show();
            }

            if(nota.getVideoGaleria()!=null){
                Uri verGalVid = Uri.parse(nota.getVideoGaleria());
                imgviewVer.setImageURI(verGalVid);
            } else{
                Toast.makeText(getApplicationContext(), "no se agrego video", Toast.LENGTH_SHORT).show();
            }



        }

        fabEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerActivity.this, EditarActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

        fabEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerActivity.this);
                builder.setMessage("¿Desea eliminar esta nota?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(dbNotas.eliminarNota(id)){
                                    lista();
                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });
    }

    private void lista(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void reproducirVer(View view) {
        if(nota.getNotavoz() != null){
            try{
                mediaPlayerVer = new MediaPlayer();
                mediaPlayerVer.setDataSource(nota.getNotavoz());
                mediaPlayerVer.prepare();
                mediaPlayerVer.start();
                Toast.makeText(this, "Reproduciendo...", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}