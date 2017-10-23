package com.example.jimmy.eventtec;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jimmy.eventtec.DTO.Eventos;
import com.example.jimmy.eventtec.retrofit.ServerAPI;
import com.squareup.okhttp.internal.Util;
import android.webkit.WebViewClient;


import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class EventoView extends AppCompatActivity {
    String baseurl = StartMyServiceAtBootReceiver.ip;
    Eventos event;
    Bitmap imagen;
    ServerAPI service;
    WebView webView;
    String webPage_es;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_view);

        event = (Eventos)getIntent().getSerializableExtra("evento");
        webView  = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        String texto = event.getDescripcion();
        String webPage_es = "<html><body><div style=text-align:justify> "+texto+" </div></body></html>";

        webView.loadData(webPage_es,"text/html", "utf-8");
        webView.clearCache(true);
        webView.clearHistory();


        TextView titulo = (TextView) findViewById(R.id.titulo);
        TextView fecha = (TextView) findViewById(R.id.hora);
        ImageView img = (ImageView) findViewById(R.id.img);
        Button ButtonEliminarMensaje = (Button) findViewById(R.id.Eliminar_button);

        if(event.borrable.equals("false")){
            ButtonEliminarMensaje.setVisibility(View.INVISIBLE);
        }else if(event.borrable.equals("true")){
            ButtonEliminarMensaje.setVisibility(View.VISIBLE);
        }
        titulo.setText(event.getTitulo());
        fecha.setText(event.getFecha());

        try{
            String subS = event.getImagen();
            subS=subS.substring(subS.indexOf(',')+1);
            byte[] decodedString = Base64.decode(subS, Base64.DEFAULT);
            final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            final Bitmap rezisedByte = Bitmap.createScaledBitmap(decodedByte, 700, 700, true);
            img.setImageBitmap(rezisedByte);

            img.setImageBitmap(rezisedByte);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarImagen(rezisedByte);

                }
            });
        }
        catch (Exception ex){
                img.setImageResource(R.mipmap.no_image);
        }
        if(event.getVisto().equals("no")) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseurl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service = retrofit.create(ServerAPI.class);
                SharedPreferences pref = getSharedPreferences("dato", MODE_PRIVATE);
                Call<String> call = service.setVisto(pref.getString("ID", ""), Integer.parseInt(event.getMensaje_ID()));

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        try {
                            //finish();
                        } catch (Exception e) {
                            Toast.makeText(EventoView.this, "Se perdio la conexión", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(EventoView.this, "Se perdio la conexión", Toast.LENGTH_LONG);
                        //Toast.makeText(EventoView.this, "Sin conexion", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(EventoView.this, "Se perdio la conexión", Toast.LENGTH_LONG);
            }
        }
    }

    public void borrar(View view){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle( Html.fromHtml("<font color='#42dfff'>Borrar Mensaje</font>"))
                .setMessage("¿Esta suguro que desea borrar este mensaje")

                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarConfirmado();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void borrarConfirmado(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ServerAPI.class);

        SharedPreferences pref = getSharedPreferences("dato",MODE_PRIVATE);

        Call<String> call = service.borrarMensaje(event.getMensaje_ID(), pref.getString("ID",""));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                try {
                    Toast.makeText(EventoView.this, "Mensaje borrado", Toast.LENGTH_SHORT).show();
                    finish();

                } catch (Exception e) {
                    Toast.makeText(EventoView.this, "No se logro borrar", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(EventoView.this, "throwable" + t.toString(), Toast.LENGTH_LONG);
                //Toast.makeText(EventoView.this, "Sin conexion", Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evento_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void mostrarImagen(Bitmap imagen){

        String name = event.getTitulo();
        ImageView image = new ImageView(this);
        image.setImageBitmap(imagen);

        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(this).
                        setMessage(name).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(image);
        builder.create().show();
    }


}



