package com.example.diamon.Vue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.diamon.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PageMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Button btn_sell,btn_buy;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView txt_user_pseudo,txt_taux,txt_prix,txt_valeur;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_page_main);



        drawerLayout = findViewById(R.id.layout);
        navigationView = findViewById(R.id.id_nav);
        View header = navigationView.getHeaderView(0);
     // toolbar = findViewById(R.id.toolbar);
        btn_sell = findViewById(R.id.btn_sell);
        btn_buy = findViewById(R.id.btn_buy);
        txt_user_pseudo = header.findViewById(R.id.id_user_pseudo);
        txt_taux= findViewById(R.id.id_taux);
        /**
         * btn_buy
         */
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PageMainActivity.this, BuyActivity.class);
                startActivity(intent);
            }
        });

        /**
         * btn_buy
         */

        btn_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable img = txt_taux.getContext().getResources().getDrawable( R.drawable.ic_trend_down );
                txt_taux.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                txt_taux.setText(" -8%");
                txt_taux.setTextColor(getResources().getColor(R.color.red1));
            }
        });



        Intent log_intent= getIntent();
        Bundle bundle = log_intent.getExtras();
        if(bundle!=null)
        {
            USER_PSEUDO =(String) bundle.get("pseudo");
        }



        setSupportActionBar(findViewById(R.id.toolbar));


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,
                findViewById(R.id.toolbar),R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        if(isConnectingToInternet(PageMainActivity.this))
        {
            Toast.makeText(getApplicationContext(),"internet is available",Toast.LENGTH_LONG).show();
            select_user(USER_PSEUDO);
        }
        else {
            Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
        }



      /*  GraphView graph = (GraphView) findViewById(R.id.graph);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{

                new DataPoint(0, 0.1),
                new DataPoint(0.2, 0.2),
                new DataPoint(0.3, 0.5),
                new DataPoint(0.7, 1),
                new DataPoint(1, 1.5),
                new DataPoint(1.5, 1.8),
                new DataPoint(1.6, 2),
                new DataPoint(1.7, 2.1),
                new DataPoint(1.8, 2.2),
                new DataPoint(1.9, 2),
                new DataPoint(2, 2.1),
                new DataPoint(2.1, 2.1),
                new DataPoint(2.2, 2.5),
                new DataPoint(2.3, 2.3),
                new DataPoint(2.4, 2.2),
                new DataPoint(2.5, 2.6),
                new DataPoint(2.6, 2.1),
                new DataPoint(2.7, 2.5),
                new DataPoint(2.8, 2),
                new DataPoint(2.9, 2.2),
                new DataPoint(3, 2),
                new DataPoint(3.2, 1.9),
                new DataPoint(3.8, 1.7),
                new DataPoint(4, 1.5),
                new DataPoint(4.1, 1.3),
                new DataPoint(4.2, 1.2),
                new DataPoint(4.3, 1.6),
                new DataPoint(4.4, 1.1),
                new DataPoint(4.5, 1.5),
                new DataPoint(4.6, 1.3)

            });

            series.setDrawBackground(true);
            series.setColor(Color.rgb(173,	193	,197));
            graph.getViewport().setDrawBorder(false);
            graph.getGridLabelRenderer().setHighlightZeroLines(false);
            graph.getViewport().setScalable(true);
            graph.getViewport().setScrollable(true);
            graph.addSeries(series);*/

        }

    // drawer menu function

    @Override
    public void onBackPressed(){

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
                 super.onBackPressed();
        }
    }
/* home, setting, deposit, send, withdrawal, blp,identity*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case  R.id.home:
                Toast.makeText(this,"home clicked",Toast.LENGTH_LONG).show();
                break;

            case R.id.buy:
                Intent intent_deposit = new Intent(PageMainActivity.this, BuyActivity.class);
                startActivity(intent_deposit);
                break;

            case R.id.send:
                Intent intent_send = new Intent(PageMainActivity.this, SendActivity.class);
                startActivity(intent_send);
                break;

            case R.id.withdrawal:
                Intent intent_wi = new Intent(PageMainActivity.this, withdrawalActivity.class);
                startActivity(intent_wi);
                break;
            case R.id.setting:
                Intent intent_tran = new Intent(PageMainActivity.this, TransactionsActivity.class);
                startActivity(intent_tran);
                break;

            case R.id.blp:
                Intent intent_blp = new Intent(PageMainActivity.this, BlpActivity.class);
                startActivity(intent_blp);
                break;
            case R.id.profile:
                Intent intent_profile = new Intent(PageMainActivity.this, ProfileActivity.class);
                intent_profile.putExtra("user_name",USER_NAME);
                intent_profile.putExtra("user_pseudo",USER_PSEUDO);
                intent_profile.putExtra("user_account_id",USER_ACCOUNT_ID);
                startActivity(intent_profile);
                break;

            case R.id.chat:
                Toast.makeText(this,"chat clicked",Toast.LENGTH_LONG).show();
                break;

            case R.id.help:
                Toast.makeText(this,"help clicked",Toast.LENGTH_LONG).show();
                break;

            case R.id.logout:
                SharedPreferences preferences = getSharedPreferences("switch",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();
                finish();
                break;

        }


        return true;
    }



    public static boolean isConnectingToInternet(PageMainActivity context)
    {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;

    }


    public void select_user(String user_pseudo) {

        // Initialize  AsyncLogin() class with email and password
        /**
        * valeur a envoyer vers le serveur
         */
        new PageMainActivity.AsyncUser().execute("select_user",user_pseudo);

    }


    private class AsyncUser extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(PageMainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(URL_MAIN);
                Log.d("connection" ,"doInBackground:*************************************************** ");

            } catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                // post name and value param
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_select", params[0])
                        .appendQueryParameter("user_pseudo", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e)
            {
                e.printStackTrace();
                return "exception";
            }
            finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            pdLoading.dismiss();
            Log.d("message du serveur", result);
            if(result!="" && !result.equalsIgnoreCase("false"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Log.e("result***************************************************************************************************************************" +
                        "", "onPostExecute: "+result);

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] user_result = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = jsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        //user_result[i] = obj.getString("nom");
                        USER_NAME=obj.getString("nom");
                        USER_PSEUDO=obj.getString("pseudo");
                        USER_ACCOUNT_ID=obj.getString("account_id");
                        txt_user_pseudo.setText(USER_NAME);
                        //Toast.makeText(PageMainActivity.this,USER_ACCOUNT_ID+" "+USER_NAME+" "+USER_PSEUDO,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if (result.equalsIgnoreCase("false"))
            {

                Log.e("probleme***************************************************************************************************************************" +
                        "", "onPostExecute: un probleme");
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(PageMainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

       /**
        *
        *
        */


        }
    }

}