package com.example.diamon.Vue;

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
import android.view.WindowManager;
import android.widget.Toast;

import com.example.diamon.Modele.HistoryAdapter;
import com.example.diamon.R;

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

public class TransactionsActivity extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="";



    RecyclerView recyclerView;
    String s1[][] ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_transactions);

        Intent history_intent= getIntent();
        Bundle bundle = history_intent.getExtras();
        if(bundle!=null)
        {
            USER_ACCOUNT_ID =(String) bundle.get("user_account_id");
            if(isConnectingToInternet(TransactionsActivity.this)) {
                Toast.makeText(getApplicationContext(),"internet is available", Toast.LENGTH_LONG).show();
                select_history(USER_ACCOUNT_ID);
            }
            else {
                Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
            }

        }



    }


    public static boolean isConnectingToInternet(TransactionsActivity context)
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

    public void select_history(String account_id) {

        // Initialize  AsyncLogin() class with email and password
        /**
         * valeur a envoyer vers le serveur
         */
        new TransactionsActivity.AsyncHistory().execute("select_history",account_id);
    }



    private class AsyncHistory extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(TransactionsActivity.this);
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
                        .appendQueryParameter("select_history", params[0])
                        .appendQueryParameter("account_id", params[1]);
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
                String[][] history_result = new String[jsonArray.length()][6];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = jsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        /**   1-> type;
                         *    2-> somme
                         *    3-> receiver
                         *    4->sender
                         *    5->dat
                         */

                        history_result[i][2] = obj.getString("somme");
                        history_result[i][3] = obj.getString("receiver");
                        history_result[i][4] = obj.getString("sender");
                        history_result[i][5] = obj.getString("dat");
                        if(USER_ACCOUNT_ID.equals(obj.getString("receiver"))){
                            history_result[i][1] = "receive";
                        }else{
                            history_result[i][1] = obj.getString("type");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                recyclerView = findViewById(R.id.id_history_recyclerview);
                s1 = history_result;
                HistoryAdapter historyAdapter = new HistoryAdapter(TransactionsActivity.this,s1);
                recyclerView.setAdapter(historyAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(TransactionsActivity.this));


            }else if (result.equalsIgnoreCase("false"))
            {
                Log.e("probleme***************************************************************************************************************************" +
                        "", "onPostExecute: un probleme");
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(TransactionsActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

            /**
             *
             *
             */

        }
    }

}