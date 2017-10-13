package com.example.jimmy.eventtec;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.jimmy.eventtec.DTO.User;
import com.example.jimmy.eventtec.retrofit.ServerAPI;
import java.util.ArrayList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class Login extends AppCompatActivity {
    String baseurl = StartMyServiceAtBootReceiver.ip;

    ServerAPI service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ServerAPI.class);
        try{
        SharedPreferences pref = getSharedPreferences("dato",MODE_PRIVATE);
        if(pref.getString("ID","")!="" &&pref.getString("PASS","")!=""){
            EditText ID = (EditText) findViewById(R.id.ID_ET);
            EditText PASS = (EditText) findViewById(R.id.PASS_ET);
            ID.setText(pref.getString("ID", ""));
            PASS.setText(pref.getString("PASS", ""));
            login(null);

        }}
        catch (Exception e){
            Toast.makeText(Login.this,e.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    public void login(View v){
        EditText ID = (EditText) findViewById(R.id.ID_ET);
        final EditText PASS = (EditText) findViewById(R.id.PASS_ET);
        Call<ArrayList<User>> call = service.log(ID.getText()+"", PASS.getText()+"");

        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Response<ArrayList<User>> response, Retrofit retrofit) {
                try {
                    String nombre = response.body().get(0).getNombre();
                    String id = response.body().get(0).getID();
                    logint(id, PASS.getText() + "", nombre);

                } catch (Exception e) {
                    Toast.makeText(Login.this, "No esta registrado", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                //Toast.makeText(Login.this, t.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(Login.this, "Usuario o contraseña no valido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void logint(String id, String pass,String nombre){




        SharedPreferences pref = getSharedPreferences("dato",MODE_PRIVATE);

        SharedPreferences.Editor edit = pref.edit();
        edit.putString("ID", id);
        edit.putString("PASS", pass);
        edit.commit();

        Intent nn= new Intent(this,MainEventos.class);
        startActivity(nn);
        finish();

    }


}
