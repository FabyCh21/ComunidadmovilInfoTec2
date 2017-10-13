package com.example.jimmy.eventtec;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.jimmy.eventtec.DTO.Eventos;
import com.example.jimmy.eventtec.retrofit.ServerAPI;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Jimmy on 25/08/2016.
 */
public class CustomService extends Service {
    MyTask myTask;
    int numeroMensajes=0;
    private boolean cent;
    @Override
    public void onCreate() {
        super.onCreate();

            myTask = new MyTask();
            cent=false;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!cent)
            myTask.execute();
        return START_STICKY ;//super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cent) {

            myTask.cancel(true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        super.onTaskRemoved(rootIntent);
    }

    private class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            if (cent){
                try {
                    pushNotificationStar(getApplicationContext());
                } catch (Exception e) {

                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cent = true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cent = false;
        }
    }

    public void pushNotificationStar(final Context context){

        final SharedPreferences pref = context.getSharedPreferences("dato",
                Context.MODE_PRIVATE);


        Firebase.setAndroidContext(context);
        Firebase myFirebaseRef = new Firebase("https://infotec-61239.firebaseio.com/");

        myFirebaseRef.child("departamentos/"+pref.getString("DEPA","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                mensajesSinleer(pref.getString("ID",""));
            }
            @Override public void onCancelled(FirebaseError error) {

            }
        });
    }
    void mensajesSinleer(String ID){
        numeroMensajes=0;
        String baseurl = StartMyServiceAtBootReceiver.ip;
        ServerAPI service;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ServerAPI.class);

        Call<ArrayList<Eventos>> call = service.eventos(ID);
        call.enqueue(new Callback<ArrayList<Eventos>>() {
            @Override
            public void onResponse(Response<ArrayList<Eventos>> response, Retrofit retrofit) {
                try {
                    ArrayList<Eventos> lista;
                    lista =  response.body();

                    for (int i=0;i<lista.size();i++){
                        if(lista.get(i).getVisto().equals("no")){
                            numeroMensajes++;

                        }

                    }
                    if(numeroMensajes>0){
                        mostrar(getApplicationContext(),numeroMensajes);

                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Throwable t) {
            }
        });


    }

    void mostrar(Context thi,int num){

        Intent intent = new Intent(thi, MainEventos.class);
        PendingIntent pIntent = PendingIntent.getActivity(thi, (int) System.currentTimeMillis(), intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(thi)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setLargeIcon(BitmapFactory.decodeResource(thi.getResources(), R.drawable.in))
                        .setContentTitle("Tec Informa.")
                        .setContentText("Tienes "+num+" mensajes sin leer")
                        .setContentInfo("")
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setLights(Color.BLUE, 500, 500)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setContentIntent(pIntent)
                        .setTicker("Alerta!");
        NotificationManager mNotificationManager = (NotificationManager) thi.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());



    }


}
