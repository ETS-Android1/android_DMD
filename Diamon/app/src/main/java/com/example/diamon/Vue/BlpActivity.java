package com.example.diamon.Vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.diamon.Modele.HistoryAdapter;
import com.example.diamon.Modele.ProviderAdapter;
import com.example.diamon.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class BlpActivity extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    public static final String URL_DMD ="http://192.168.1.120/dmd/function_valeur_dmd.php";
    //public static final String URL_MAIN ="http://dmd.moulenetadi.com/dmd_work.php";
    //public static final String URL_DMD ="http://dmd.moulenetadi.com/function_valeur_dmd.php";
    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="";

    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    private Button pop_cancel, pop_save;
    FloatingActionButton add_btn;

    EditText id_town, id_lcl_money,id_phone;
    RecyclerView recyclerView;
    String s1[][] ;

    Spinner pays, type_provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_blp);


        if(isConnectingToInternet(BlpActivity.this)) {
            Toast.makeText(getApplicationContext(),"internet is available", Toast.LENGTH_LONG).show();
            select_lp();
        }
        else {
            Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
        }

        add_btn = findViewById(R.id.id_add_blp);


        Intent blp_intent= getIntent();
        Bundle bundle = blp_intent.getExtras();
        if(bundle!=null)
        {
            USER_ACCOUNT_ID =(String) bundle.get("user_account_id");
            USER_NAME =(String) bundle.get("user_name");

            if(isConnectingToInternet(BlpActivity.this)) {
                Toast.makeText(getApplicationContext(),"internet is available", Toast.LENGTH_LONG).show();
               // select_provider();
            }
            else {
                Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
            }

        }


       add_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               pop_add();
           }
       });
    }


    public static boolean isConnectingToInternet(BlpActivity context)
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


    public void pop_add(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View pop_add_view= getLayoutInflater().inflate(R.layout.add_popup,null);
        id_town = pop_add_view.findViewById(R.id.id_ville);
        id_lcl_money = pop_add_view.findViewById(R.id.id_pay_agent);
        id_phone = pop_add_view.findViewById(R.id.id_phone);
        pays = pop_add_view.findViewById(R.id.id_pays);
        type_provider = pop_add_view.findViewById(R.id.id_type_provider);

        ArrayList<String> pays_liste = new ArrayList<>();

        pays_liste.add("Your Country");
        pays_liste.add("Togo");
        pays_liste.add("Benin");
        pays_liste.add("Uruguay");
        pays_liste.add("Burkina Faso");

        pays.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,pays_liste));

        ArrayList<String> provider_liste = new ArrayList<>();

        provider_liste.add("Type of Provider");
        provider_liste.add("Seller");
        provider_liste.add("Buyer");
        provider_liste.add("Seller & Buyer");

        type_provider.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,provider_liste));
        pop_cancel= pop_add_view.findViewById(R.id.save2);
        pop_save= pop_add_view.findViewById(R.id.save);

        dialogBuilder.setView(pop_add_view);
        dialog= dialogBuilder.create();
        dialog.show();

        pop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        pop_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Toast.makeText(BlpActivity.this,USER_ACCOUNT_ID,Toast.LENGTH_LONG).show();
                blp();
            }
        });
    }

    public void blp() {


        String txt_pays = pays.getSelectedItem().toString();
        String txt_ville = id_town.getText().toString();
        String txt_lcl_money = id_lcl_money.getText().toString();
        String txt_type_provider= type_provider.getSelectedItem().toString();
        String txt_phone = id_phone.getText().toString();

        if(txt_pays!="Your Country" && txt_ville!=""  && txt_lcl_money!=""
                && txt_type_provider!= "Type of Provider" && txt_phone!="" ){

            new BlpActivity.AsyncBLP().execute(txt_pays,txt_ville,txt_lcl_money,txt_type_provider,txt_phone,USER_ACCOUNT_ID,USER_NAME,"blp");


        }
       // Toast.makeText(BlpActivity.this,txt_pays+" "+txt_type_provider+" "+txt_phone,Toast.LENGTH_LONG).show();

    }


    public void pop_success(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View pop_success_view= getLayoutInflater().inflate(R.layout.success_pop,null);


        pop_cancel= pop_success_view.findViewById(R.id.canc);
        pop_save= pop_success_view.findViewById(R.id.hom);

        dialogBuilder.setView(pop_success_view);
        dialog= dialogBuilder.create();
        dialog.show();

        pop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        pop_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent_home = new Intent(BlpActivity.this,PageMainActivity.class);
               startActivity(intent_home);
                BlpActivity.this.finish();
            }
        });
    }

    private class AsyncBLP extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(BlpActivity.this);
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
                url = new URL(URL_DMD);
                Log.d("///////////////////////////////////////////////////////////////////////",  "*********************************************************************************************************************" );
                //Log.d("connection" ,"doInBackground:*************************************************** ");

            } catch (MalformedURLException e) {
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
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("txt_pays", params[0])
                        .appendQueryParameter("txt_ville", params[1])
                        .appendQueryParameter("txt_lcl_money", params[2])
                        .appendQueryParameter("txt_type_provider", params[3])
                        .appendQueryParameter("txt_phone", params[4])
                        .appendQueryParameter("user_account_id", params[5])
                        .appendQueryParameter("user_name", params[6])
                        .appendQueryParameter("blp", params[6]);
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

            } catch (IOException e1) {
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

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();
            Log.d("message du serveur", result);
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                */
                dialog.dismiss();
                pop_success();


                // Intent intent = new Intent(BlpActivity.this,PageMainActivity.class);
                //startActivity(intent);
                //

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(BlpActivity.this, "Try again please", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("vide") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(BlpActivity.this, "VIDE Problem.", Toast.LENGTH_LONG).show();

            }else{
                Log.d("///////////////////////////////////////////////////////////////////////", " "+result);
            }

        }

    }

















    public void select_lp() {

        // Initialize  AsyncLogin() class with email and password
        /**
         * valeur a envoyer vers le serveur
         */
        new BlpActivity.AsyncLcl_P().execute("select_blp");
    }



    private class AsyncLcl_P extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(BlpActivity.this);
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

                recyclerView = findViewById(R.id.id_provider_recyclerview);
                s1 = blp_result;
                ProviderAdapter providerAdapter = new ProviderAdapter(BlpActivity.this,s1);
                recyclerView.setAdapter(providerAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(BlpActivity.this));


            }else if (result.equalsIgnoreCase("false"))
            {
                Log.e("probleme***************************************************************************************************************************" +
                        "", "onPostExecute: un probleme");
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(BlpActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

            /**
             *
             *
             */

        }
    }



}