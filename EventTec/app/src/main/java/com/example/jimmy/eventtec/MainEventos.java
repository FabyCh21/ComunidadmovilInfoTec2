package com.example.jimmy.eventtec;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimmy.eventtec.DTO.Departamento;
import com.example.jimmy.eventtec.DTO.Eventos;
import com.example.jimmy.eventtec.retrofit.ServerAPI;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainEventos extends AppCompatActivity {

    public class AdapterView extends BaseAdapter {

        LayoutInflater minflater;
        ArrayList<Eventos> lista;
        public AdapterView(Context context,ArrayList<Eventos> lista) {
            minflater = LayoutInflater.from(context);
            this.lista=lista;
        }

        public AdapterView() {
            super();
        }


        @Override
        public int getCount() {
            return lista.size();
        }

        @Override
        public Object getItem(int position) {
            return lista.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=minflater.inflate(R.layout.lista_item,null);

            }
            final TextView name= (TextView) convertView.findViewById(R.id.titulo);
            name.setText(lista.get(position).getTitulo());
            final TextView hora= (TextView) convertView.findViewById(R.id.hora);
            hora.setText(lista.get(position).getFecha()+"");
            final TextView nuevo = (TextView) convertView.findViewById(R.id.nuevo);
            if(lista.get(position).getVisto().equals("no")){
                nuevo.setVisibility(nuevo.VISIBLE);
            }
            else{
                nuevo.setVisibility(nuevo.INVISIBLE);
            }


            final ImageView imagen = (ImageView) convertView.findViewById(R.id.img);
            try {

                String subS =lista.get(position).getImagen();
                if(subS.equals("http://localhost/aspnet_client")){
                    imagen.setImageDrawable(getDrawable(R.drawable.online));
                }else{
                subS=subS.substring(subS.indexOf(',')+1);

                byte[] decodedString = Base64.decode(subS, Base64.DEFAULT);
                final Bitmap  decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                final Bitmap rezisedByte = Bitmap.createScaledBitmap(decodedByte,700,700,true);

                imagen.setImageBitmap(getCircularBitmapWithWhiteBorder(rezisedByte,10));
                imagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarImagen(rezisedByte, lista.get(position).getTitulo());

                    }
                });}
            }catch (Exception e){
                imagen.setImageResource(R.mipmap.no_image);
            }

            return convertView;
        }
    }


    public static Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap,
                                                          int borderWidth) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    void mostrarImagen(Bitmap img,String name){
        ImageView image = new ImageView(this);
        image.setImageBitmap(img);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setMessage(name).
                        setPositiveButton("OK " , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(image);
        builder.create().show();
    }
    String My_id="";
    TextView info;
    String baseurl = StartMyServiceAtBootReceiver.ip;
    ArrayList<Eventos> lista;
    ServerAPI service;
    SharedPreferences pref;
    ImageView in_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_eventos);
        info = (TextView) findViewById(R.id.InfoTextView);
        in_out = (ImageView) findViewById(R.id.in_out);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ServerAPI.class);
        try{

            pref = getSharedPreferences("dato",MODE_PRIVATE);
            if(pref.getString("ID","")!="" &&pref.getString("PASS","")!=""){
                My_id =pref.getString("ID","");

                if(StartMyServiceAtBootReceiver.runing==false){// corre el proceso en segundo plano si no esta corriendoz

                    StartMyServiceAtBootReceiver.runing=true;
                    startService(new Intent(getApplicationContext(), CustomService.class));
                }





                Firebase.setAndroidContext(this);
                Firebase myFirebaseRef = new Firebase("https://infotec-61239.firebaseio.com/");
                final ArrayList<Departamento> listaDepartamentos = new ArrayList<Departamento>();

                final Call<ArrayList<Departamento>> call = service.getDepartementPerson(pref.getString("ID",""));

                call.enqueue(new Callback<ArrayList<Departamento>>(){
                    @Override
                    public void onResponse(Response<ArrayList<Departamento>> response, Retrofit retrofit){
                        try{
                            for(int i = 0; i < response.body().size(); i++){
                                Departamento d = new Departamento(response.body().get(i).toString());
                                listaDepartamentos.add(d);
                            }
                        }
                        catch (Exception e){

                        }
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(MainEventos.this,"No hay conexion a internet",Toast.LENGTH_SHORT).show();
                    }
                 });


                for (int i=0;i<listaDepartamentos.size();i++) {
                    myFirebaseRef.child("departamentos/" + listaDepartamentos.get(i).toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            actualizarEventos(My_id);
                        }

                        @Override
                        public void onCancelled(FirebaseError error) {

                        }
                    });
                }
            }else
            {
                Intent nn= new Intent(this,Login.class);
                startActivity(nn);
                finish();
            }
        }catch (Exception e){
            Toast.makeText(MainEventos.this, "ERROR="+e.getMessage(), Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_eventos, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarEventos(My_id);
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

    void intentVer(Eventos obj){
        Intent nn= new Intent(this,EventoView.class);
        nn.putExtra("evento",obj);
        try {

            startActivity(nn);

        }
        catch (Exception e){
            Toast.makeText(MainEventos.this,"Error imagen no manejable",Toast.LENGTH_SHORT).show();
            System.out.println(e.toString());
        }

    }
    void actualizarEventos(String id){
        Call<ArrayList<Eventos>> call = service.eventos(id);
        call.enqueue(new Callback<ArrayList<Eventos>>() {
            @Override
            public void onResponse(Response<ArrayList<Eventos>> response, Retrofit retrofit) {
               try {
                   in_out.setBackgroundResource(R.drawable.online);
                   lista =  response.body();
                    if(lista.size()==0){


                        info.setText("No tienes mensajes");
                    }else{
                        TextView info = (TextView) findViewById(R.id.InfoTextView);
                        info.setText("Tec Informa");
                    }
                    ListView listV = (ListView) findViewById(R.id.Eventos_LV);
                    AdapterView a=new AdapterView(MainEventos.this,lista);
                    listV.setAdapter(a);
                    listV.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                            intentVer(lista.get(position));
                        }
                    });
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Throwable t) {
                in_out.setBackgroundResource(R.drawable.offline);
                info.setText("Fallo en recibir eventos");
            }
        });
    }
    public void logOut(View V){
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("ID", "");
        edit.putString("PASS", "");
        edit.putString("DEPA", "");
        edit.commit();
        Intent nn= new Intent(this,Login.class);
        startActivity(nn);
        finish();

    }
}
