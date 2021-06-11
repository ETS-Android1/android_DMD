package com.example.diamon.Modele;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.diamon.R;
import com.example.diamon.Vue.BlpActivity;

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

public class SelectProvider {


    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    public static final String URL_DMD ="http://192.168.1.120/dmd/function_valeur_dmd.php";
    //public static final String URL_MAIN ="http://dmd.moulenetadi.com/dmd_work.php";
    //public static final String URL_DMD ="http://dmd.moulenetadi.com/function_valeur_dmd.php";
    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="";



    public String s1[][] ;

    public String[][] getS1() {
        return s1;
    }

    public void setS1(String[][] s1) {
        this.s1 = s1;
    }







    public void select_lp(String query) {

        // Initialize  AsyncLogin() class with email and password
        /**
         * valeur a envoyer vers le serveur
         */
        if(query!=""){
            new SelectProvider.AsyncLcl_P().execute("select_blp",query);
        }else{
        new SelectProvider.AsyncLcl_P().execute("select_blp","");}
    }



    private class AsyncLcl_P extends AsyncTask<String, String, String>
    {
        //ProgressDialog pdLoading = new ProgressDialog(BlpActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                        .appendQueryParameter("select_blp", params[0])
                        .appendQueryParameter("query", params[1]);

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
           // pdLoading.dismiss();
            //shime.setVisibility(View.GONE);

            Log.d("message du serveur", result);
            if(result!="" && !result.equalsIgnoreCase("false")  && !result.equalsIgnoreCase("exception")) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Log.e("result***************************************************************************************************************************" +
                        "", "onPostExecute: " + result);

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(jsonArray!=null){
                String[][] blp_result = new String[jsonArray.length()][8];
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
                         *    7->cetification"
                         */

                        blp_result[i][2] = obj.getString("provider_name");
                        blp_result[i][3] = obj.getString("number");
                        blp_result[i][4] = obj.getString("pay_agent");
                        blp_result[i][5] = obj.getString("ville_pays");
                        blp_result[i][6] = obj.getString("type");
                        blp_result[i][7] = obj.getString("certifie");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                setS1(blp_result);
            }else{

                }
            }else if (result.equalsIgnoreCase("false"))
            {


                Log.e("probleme***************************************************************************************************************************" +
                        "", "onPostExecute: un probleme");
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

               // Toast.makeText(BlpActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

            /**
             *
             *
             */

        }
    }

}
