package com.example.diamon.Vue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.diamon.Modele.HistoryAdapter;
import com.example.diamon.Modele.ProviderAdapter;
import com.example.diamon.Modele.TopUserAdapter;
import com.example.diamon.R;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
import java.util.ArrayList;

public class PageMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Button btn_sell,btn_buy,btn_graph;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    private Button buy_d, buy_provider;
    TextView txt_user_pseudo,txt_taux,txt_prix,txt_valeur,txt_old,txt_new,txt_nb_dmd;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    public static final String URL_DMD ="http://192.168.1.120/dmd/function_valeur_dmd.php";
    //public static final String URL_MAIN ="http://dmd.moulenetadi.com/dmd_work.php";
    //public static final String URL_DMD ="http://dmd.moulenetadi.com/function_valeur_dmd.php";


    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="",DMD_VALUE="",DMD_TAUX="0", DMD_PRIX="",PWD="";
    public String BALANCE;
    public float OLD_TAUX=0,NEW_TAUX;

    RecyclerView recyclerView1,recyclerView2;
    String s1[][] = new String[4][7];
    String s2[][]  = new String[4][7];
    Spinner code_pays;


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
        txt_valeur= findViewById(R.id.id_valeur);
        txt_nb_dmd= findViewById(R.id.id_nb_dmd);
        txt_prix= findViewById(R.id.id_prix);
        /**
         * btn_buy
         */
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop_buy();
                //Intent intent = new Intent(PageMainActivity.this, BuyActivity.class);
                //startActivity(intent);
            }
        });

        /**
         * btn_sell
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
            PWD=(String) bundle.get("pwd");
        }



        setSupportActionBar(findViewById(R.id.toolbar));


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,
                findViewById(R.id.toolbar),R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        if(isConnectingToInternet(PageMainActivity.this)) {
            Toast.makeText(getApplicationContext(),"internet is available", Toast.LENGTH_LONG).show();
            select_user(USER_PSEUDO);
            select_dmd();
            select_lp();
            pop_activity();
            //OLD_TAUX = Float.parseFloat(DMD_TAUX);

        }
        else {
            Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
        }

        /**
         *    1->
         *    2-> provider_name;
         *    3-> number
         *    4->pay_agent
         *    5->ville_pays
         *    6->type"
         */


    }



        public void actualiser(){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    select_dmd();
                }
            },10000);
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
                Intent intent_home = new Intent(PageMainActivity.this, PageMainActivity.class);
                intent_home.putExtra("pseudo",USER_PSEUDO);
                intent_home.putExtra("pwd",PWD);
                startActivity(intent_home);
                break;

            case R.id.buy:
                pop_buy();
                //Intent intent_deposit = new Intent(PageMainActivity.this, BuyActivity.class);
                //startActivity(intent_deposit);
                break;

            case R.id.send:
                Intent intent_send = new Intent(PageMainActivity.this, SendActivity.class);
                intent_send.putExtra("user_account_id",USER_ACCOUNT_ID);
                intent_send.putExtra("balance",BALANCE);
                intent_send.putExtra("pwd",PWD);
                startActivity(intent_send);
                break;

            case R.id.withdrawal:
                Intent intent_wi = new Intent(PageMainActivity.this, withdrawalActivity.class);
                startActivity(intent_wi);
                break;

            case R.id.setting:
                Intent intent_tran = new Intent(PageMainActivity.this, TransactionsActivity.class);
                intent_tran.putExtra("user_account_id",USER_ACCOUNT_ID);
                startActivity(intent_tran);
                break;

            case R.id.blp:
                Intent intent_blp = new Intent(PageMainActivity.this, BlpActivity.class);
                intent_blp.putExtra("user_name",USER_NAME);
                intent_blp.putExtra("user_pseudo",USER_PSEUDO);
                intent_blp.putExtra("user_account_id",USER_ACCOUNT_ID);
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

        /**
        *SELECTION DES INFO USER NAME
        * PSEUDO , ACCOUNT_ID , BALANCE
        */

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
            //pdLoading.setMessage("\tLoading...");
           // pdLoading.setCancelable(false);
            //pdLoading.show();

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
            if(result!="" && !result.equalsIgnoreCase("false")  && !result.equalsIgnoreCase("exception"))
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
                        BALANCE=obj.getString("nb_dmd");
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

        /**
        *SELECTION DES INFO DMD VALEUR , PRIX , TAUX
        *
        */
        public void select_dmd() {

            // Initialize  AsyncLogin() class with email and password
            /**
             * valeur a envoyer vers le serveur
             */
            new PageMainActivity.AsyncDMD().execute("select_dmd");

        }

        private class AsyncDMD extends AsyncTask<String, String, String>
        {
            ProgressDialog pdLoading = new ProgressDialog(PageMainActivity.this);
            HttpURLConnection conn;
            URL url = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //this method will be running on UI thread
               // pdLoading.setMessage("\tLoading...");
               // pdLoading.setCancelable(false);
               // pdLoading.show();

            }

            @Override
            protected String doInBackground(String... params) {
                try {

                    // Enter URL address where your php file resides
                    url = new URL(URL_DMD);
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
                            .appendQueryParameter("select_dmd", params[0]);
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
                if(result!="" && !result.equalsIgnoreCase("false")  && !result.equalsIgnoreCase("exception"))
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
                    String[] dmd_result = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = null;
                        try {
                            obj = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            OLD_TAUX=Float.parseFloat(DMD_TAUX);
                            //user_result[i] = obj.getString("nom");
                            DMD_VALUE=obj.getString("valeur");
                            //DMD_PRIX=obj.getString("prix");
                            DMD_TAUX=obj.getString("taux");
                            float valeur_set=Float.parseFloat(DMD_VALUE);
                            float nb_dmd_set =Float.parseFloat(BALANCE);
                            txt_nb_dmd.setText(BALANCE+" DMD");
                            txt_prix.setText("$"+(valeur_set*nb_dmd_set));
                            txt_valeur.setText("$"+DMD_VALUE);
                            NEW_TAUX=Float.parseFloat(DMD_TAUX);

                            float aff_taux=OLD_TAUX-NEW_TAUX;
                            txt_taux.setText(aff_taux+"%");
                            select_user(USER_PSEUDO);
                            if(aff_taux < 0){
                                Drawable img = txt_taux.getContext().getResources().getDrawable(R.drawable.ic_trend_down);
                                txt_taux.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                                txt_taux.setText(aff_taux+"%");
                                txt_taux.setTextColor(getResources().getColor(R.color.red1));}else{
                                if(aff_taux > 0){
                                    Drawable img2 = txt_taux.getContext().getResources().getDrawable( R.drawable.ic_trend_up );
                                    txt_taux.setCompoundDrawablesWithIntrinsicBounds( img2, null, null, null);
                                    txt_taux.setText(aff_taux+"%");
                                    txt_taux.setTextColor(getResources().getColor(R.color.vert1));

                                }else{
                                    Drawable img3 = txt_taux.getContext().getResources().getDrawable( R.drawable.ic_trend_up );
                                    txt_taux.setCompoundDrawablesWithIntrinsicBounds( img3, null, null, null);
                                    txt_taux.setText(aff_taux+"%");
                                    txt_taux.setTextColor(getResources().getColor(R.color.vert1));}
                            }

                            actualiser();
                            //Toast.makeText(PageMainActivity.this,BALANCE,Toast.LENGTH_LONG).show();
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


    public void pop_buy(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View pop_add_view= getLayoutInflater().inflate(R.layout.buy_popup,null);
       // buy_d = pop_add_view.findViewById(R.id.id_buy_d);
        buy_provider = pop_add_view.findViewById(R.id.id_buy_provider);


        dialogBuilder.setView(pop_add_view);
        dialog= dialogBuilder.create();
        dialog.show();

       /* buy_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_deposit = new Intent(PageMainActivity.this, BuyActivity.class);
                startActivity(intent_deposit);
            }
        });*/

        buy_provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_blp = new Intent(PageMainActivity.this, BlpActivity.class);
                intent_blp.putExtra("user_name",USER_NAME);
                intent_blp.putExtra("user_pseudo",USER_PSEUDO);
                intent_blp.putExtra("user_account_id",USER_ACCOUNT_ID);
                startActivity(intent_blp);

            }
        });
    }


    /**
     * POP UP ONCREATE
     *
     */

    public void pop_activity(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View pop_add_view= getLayoutInflater().inflate(R.layout.oncreate_pop,null);
        Button install_btn;
        install_btn = pop_add_view.findViewById(R.id.id_install);

        dialogBuilder.setView(pop_add_view);
        dialog= dialogBuilder.create();
        dialog.show();

        install_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_web("https://moulenetadi.com");
            }
        });

    }



    /**
     * SELECTION DES INFOS PROVIDER
     *
     */

    public void select_lp() {

        // Initialize  AsyncLogin() class with email and password
        /**
         * valeur a envoyer vers le serveur
         */
        new PageMainActivity.AsyncLcl_P().execute("select_blp");
    }


    private class AsyncLcl_P extends AsyncTask<String, String, String>
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
                        .appendQueryParameter("select_blp", params[0]);

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
            //shime.setVisibility(View.GONE);

            Log.d("message du serveur", result);
            if(result!="" && !result.equalsIgnoreCase("false")  && !result.equalsIgnoreCase("exception"))
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
                String[][] blp_result = new String[jsonArray.length()][7];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = jsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        /**   1->
                         *    2-> provider_name;
                         *    3-> number
                         *    4->pay_agent
                         *    5->ville_pays
                         *    6->type"
                         */

                        blp_result[i][2] = obj.getString("provider_name");
                        blp_result[i][3] = obj.getString("number");
                        blp_result[i][4] = obj.getString("pay_agent");
                        blp_result[i][5] = obj.getString("ville_pays");
                        blp_result[i][6] = obj.getString("type");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                recyclerView2 = findViewById(R.id.id_recy_pro_2);

                s1 = blp_result;
                ProviderAdapter providerAdapter = new ProviderAdapter(PageMainActivity.this,s1);
                recyclerView2.setAdapter(providerAdapter);
                recyclerView2.setLayoutManager(new LinearLayoutManager(PageMainActivity.this));









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

    /**
     * FONCTION POUR OUVRIR UN LIEN
     *
     */

    public void open_web(String link){
        Intent webIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
        startActivity(webIntent);
    }
}