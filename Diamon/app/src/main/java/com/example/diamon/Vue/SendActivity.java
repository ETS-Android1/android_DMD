package com.example.diamon.Vue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diamon.R;

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

public class SendActivity extends AppCompatActivity {

    public Button send_btn;
    public TextView txt_receiver_id,txt_amount,txt_code;
    public String RECEIVER,AMOUNT,CODE;
    public static final String URL_DMD ="http://192.168.1.120/dmd/function_valeur_dmd.php";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="",BALANCE="",PWD="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_send);


        send_btn = findViewById(R.id.btn_send);
        txt_receiver_id= findViewById(R.id.Receiver_account_id);
        txt_amount= findViewById(R.id.amount);
        txt_code= findViewById(R.id.secret_code);


        Intent log_intent= getIntent();
        Bundle bundle = log_intent.getExtras();
        if(bundle!=null)
        {
            USER_ACCOUNT_ID =(String) bundle.get("user_account_id");
            BALANCE =(String) bundle.get("balance");
            PWD =(String) bundle.get("pwd");
        }

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SendActivity.this, USER_ACCOUNT_ID+" "+PWD+" "+BALANCE, Toast.LENGTH_LONG).show();
                AMOUNT = txt_amount.getText().toString();
                //CODE = txt_code.getText().toString();
                float balance_ = Float.parseFloat(BALANCE);
                float amount_ = Float.parseFloat(AMOUNT);

                CODE= txt_code.getText().toString();
                if (PWD.equals(CODE)) {
                    if (amount_ <= balance_) {
                        send_dmd();
                    } else {
                        Toast.makeText(SendActivity.this, "Can't send more  " + BALANCE + " DMD", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SendActivity.this, "Wrong password ", Toast.LENGTH_LONG).show();
                }
            }
        });



    }











    public void send_dmd() {

        // Get text from email and passord field
        RECEIVER = txt_receiver_id.getText().toString();
        AMOUNT = txt_amount.getText().toString();
        CODE= txt_code.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        new SendActivity.AsyncSendDMD().execute(RECEIVER,AMOUNT,CODE,USER_ACCOUNT_ID,"send_dmd");

    }


    private class AsyncSendDMD extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SendActivity.this);
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
                        .appendQueryParameter("receiver", params[0])
                        .appendQueryParameter("amount", params[1])
                        .appendQueryParameter("code", params[2])
                        .appendQueryParameter("sender_id", params[3])
                        .appendQueryParameter("send_dmd", params[4]);
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
                Intent intent = new Intent(SendActivity.this,PageMainActivity.class);
                startActivity(intent);
                SendActivity.this.finish();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(SendActivity.this, "Try again please", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("vide") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(SendActivity.this, "VIDE Problem.", Toast.LENGTH_LONG).show();

            }else{
                Log.d("///////////////////////////////////////////////////////////////////////", " "+result);
            }

        }

        }





}