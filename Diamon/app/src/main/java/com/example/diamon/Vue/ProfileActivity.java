package com.example.diamon.Vue;

import androidx.appcompat.app.AppCompatActivity;
import com.example.diamon.Controleur.Controle;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diamon.Controleur.Controle;
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

public class ProfileActivity extends AppCompatActivity {

    private Button          btn_update_profile;
    public  TextView        txt_user_account_id;
    public  EditText        txt_user_name,txt_user_pseudo,txt_user_phone,txt_user_mail,
                            txt_user_pays,txt_user_pay_agent;

    public static  String   USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="",
                            USER_PHONE="",USER_MAIL="",USER_PAYS="",USER_PAY_AGENT="";

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);
        txt_user_name = findViewById(R.id.txt_name);
        txt_user_pseudo = findViewById(R.id.txt_pseudo);
        txt_user_account_id = findViewById(R.id.txt_account_id);
        txt_user_phone = findViewById(R.id.txt_numero);
        txt_user_mail = findViewById(R.id.txt_mail);
        txt_user_pays = findViewById(R.id.txt_pays);
        txt_user_pay_agent = findViewById(R.id.txt_payement_agent);
        btn_update_profile = findViewById(R.id.btn_update_profile);
        Intent profile_intent= getIntent();
        Bundle bundle = profile_intent.getExtras();

        if(bundle!=null)
        {
            USER_NAME =(String) bundle.get("user_name");
            USER_PSEUDO =(String) bundle.get("user_pseudo");
            USER_ACCOUNT_ID =(String) bundle.get("user_account_id");
            txt_user_name.setText(USER_NAME);
            txt_user_pseudo.setText(USER_PSEUDO);
            txt_user_account_id.setText(USER_ACCOUNT_ID);
            select_user(USER_PSEUDO);
            /*
            txt_user_phone.setText(USER_PHONE);
            txt_user_mail.setText(USER_MAIL);
            txt_user_pays.setText(USER_PAYS);
            txt_user_pay_agent.setText(USER_PAY_AGENT);*/

        }

        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               USER_NAME        = txt_user_name.getText().toString();
               USER_PSEUDO      = txt_user_pseudo.getText().toString();
               USER_PHONE       =txt_user_phone.getText().toString();
               USER_MAIL        =txt_user_mail.getText().toString();
               USER_PAYS        =txt_user_pays.getText().toString();
               USER_PAY_AGENT  =txt_user_pay_agent.getText().toString();

                new ProfileActivity.AsyncUpdate_Profile().execute(  USER_NAME,
                                                                    USER_ACCOUNT_ID,
                                                                    USER_PHONE,
                                                                    USER_MAIL,
                                                                    USER_PAYS,
                                                                    USER_PAY_AGENT,
                                                                    "update_profile");
            }
        });



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
        new ProfileActivity.AsyncUser().execute("select_user",user_pseudo);

    }
    private class AsyncUser extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ProfileActivity.this);
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
                        USER_MAIL=obj.getString("mail");
                        USER_PAYS=obj.getString("pays");
                        USER_PHONE=obj.getString("phone");
                        USER_PAY_AGENT=obj.getString("pay_agent");
                        txt_user_name.setText(USER_NAME);
                        txt_user_pseudo.setText(USER_PSEUDO);
                        txt_user_account_id.setText(USER_ACCOUNT_ID);
                        txt_user_phone.setText(USER_PHONE);
                        txt_user_mail.setText(USER_MAIL);
                        txt_user_pays.setText(USER_PAYS);
                        txt_user_pay_agent.setText(USER_PAY_AGENT);
                        //txt_user_pseudo.setText(USER_NAME);
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

                Toast.makeText(ProfileActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

            /**
             *
             *
             */


        }
    }


    private class AsyncUpdate_Profile extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ProfileActivity.this);
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
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("account_id", params[1])
                        .appendQueryParameter("phone", params[2])
                        .appendQueryParameter("mail", params[3])
                        .appendQueryParameter("pays", params[4])
                        .appendQueryParameter("pay_agent", params[5])
                        .appendQueryParameter("update_profile", params[6]);
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
                Toast.makeText(ProfileActivity.this, "Profile Update successful", Toast.LENGTH_LONG).show();


            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(ProfileActivity.this, "OOPs! Something went wrong. Please Try again with another pseudo", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("champ_vide")){

                // If username and password does not match display a error message
                Toast.makeText(ProfileActivity.this, "Name can't be empty", Toast.LENGTH_LONG).show();

            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(ProfileActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }



}