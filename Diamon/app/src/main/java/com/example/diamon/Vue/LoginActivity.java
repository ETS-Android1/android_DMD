package com.example.diamon.Vue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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

public class LoginActivity extends AppCompatActivity {
        private Button btn_sign_up;
        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;
        private EditText ed_user_name;
        private EditText ed_pwd;
        private TextView reg;
        public String user_name,pwd;
        public Switch rmb;
        public String is_rmb="false";
       public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
       //public static final String URL_MAIN ="http://dmd.moulenetadi.com/dmd_work.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
             setContentView(R.layout.activity_login);

        btn_sign_up    = findViewById(R.id.btn_sign_up);
        ed_user_name   = findViewById(R.id.editTextTextPersonName);
        ed_pwd         = findViewById(R.id.tx1);
        reg            = findViewById(R.id.register);
        rmb            = findViewById(R.id.rmb);

        SharedPreferences preferences = getSharedPreferences("switch",MODE_PRIVATE);
        String le_switch = preferences.getString("remember","");
        String le_pseudo = preferences.getString("pseudo","");
        String le_pwd = preferences.getString("pwd","");

        /**
         * check remember
         */
        if(le_switch.equals("true"))
        {

            Intent intent = new Intent(LoginActivity.this, PageMainActivity.class);
            intent.putExtra("pseudo",le_pseudo);
            intent.putExtra("pwd",le_pwd);
            startActivity(intent);
            LoginActivity.this.finish();

        }

        /**
         * register
         */

        reg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

        rmb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        is_rmb="true";
                 }
            });
        /**
         * make remember
         */
        btn_sign_up.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                /**
                 * check connection
                 */
                if(isConnectingToInternet(LoginActivity.this))
                {
                    checkLogin();
                    Toast.makeText(getApplicationContext(),"internet is available",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    /**
     * check connection
     */
    public static boolean isConnectingToInternet(LoginActivity context)
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
     * check login function
     */
    public void checkLogin()
    {

        // Get text from email and passord field
        user_name = ed_user_name.getText().toString();
        pwd = ed_pwd.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(user_name,pwd,"login");

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                // Enter URL address where your php file resides
                url = new URL(URL_MAIN);
                Log.d("connection" ,"doInBackground:*************************************************** ");

            } catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try
            {
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
                        .appendQueryParameter("password", params[1])
                        .appendQueryParameter("login", params[2]);
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

            try
            {

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
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result)
        {

            //this method will be running on UI thread

            pdLoading.dismiss();
            Log.d("message du serveur", result);
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                /**
                 * check remember and save info
                 */
                if(is_rmb.equals("true"))
                {

                        SharedPreferences preferences = getSharedPreferences("switch",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember","true");
                        editor.putString("pseudo",user_name);
                        editor.putString("pwd", pwd);
                        editor.apply();
                        Toast.makeText(LoginActivity.this,"will remember you",Toast.LENGTH_LONG).show();
                }


                Intent intent = new Intent(LoginActivity.this,PageMainActivity.class);
                intent.putExtra("pseudo",user_name);
                intent.putExtra("pwd",pwd);
                startActivity(intent);
                LoginActivity.this.finish();

            }else if (result.equalsIgnoreCase("false"))
            {

                // If username and password does not match display a error message
                Toast.makeText(LoginActivity.this, "Invalid pseudo or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(LoginActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            } else{
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();

                Log.d("************************************************************************", "onPostExecute: "+result);
            }
        }

    }


}