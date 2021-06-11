package com.example.diamon.Modele;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.diamon.Vue.PageMainActivity;

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

public class Select_In_Db {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    public static final String URL_VERSION ="http://192.168.1.120/dmd/dmd_version.php";
    public static final String URL_DMD ="http://192.168.1.120/dmd/function_valeur_dmd.php";
    //public static final String URL_MAIN ="http://dmd.moulenetadi.com/dmd_work.php";
    //public static final String URL_DMD ="http://dmd.moulenetadi.com/function_valeur_dmd.php";

    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="",DMD_VALUE="",DMD_TAUX="0", DMD_PRIX="",PWD="";
    public String BALANCE;


    public String version="";


    public void setVersion(String v) {
        this.version = v;
    }

    public String getVersion() {
        return version;
    }


    /**
     *SELECTION DES INFO USER NAME
     * PSEUDO , ACCOUNT_ID , BALANCE
     */

    public void check_version() {
        /**
         *
         */
        new Select_In_Db.Async().execute("select_element");

    }


    private class Async extends AsyncTask<String, String, String>
    {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(URL_VERSION);

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
                        .appendQueryParameter("select_element", params[0]);
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

            Log.d("message du serveur"+
                    "|||||||||||||||||||||||||||||||" +
                    "|||||||||||||||||||||||||||||||||||||||||||||||||||" +
                    "|||||||||||||||||||||||||||||||||||||||" +
                    "|||||||||||||||||||||||||||||||||||||||||||", result);
            if(result!="" && !result.equalsIgnoreCase("false")  && !result.equalsIgnoreCase("exception"))
            {


                Log.d(" SETVERSION /////////////////////////////////////////////////////" +
                        "///////////////////////////////////////////////////////" +
                        "///////////////////////////////////////////////////////" +
                        "/////////////////////////////////////////////////////", result);
                setVersion(result);

                Log.d("GETVERSION//////////////////////////////////////" +
                        "///////////////////////////////////////////////" +
                        "//////////////////////////////////////////////////" +
                        "/////////////////////////////////////////////////" +
                        "/////////////////////////////////////////////////", getVersion());



            }else if (result.equalsIgnoreCase("false"))
            {

                Log.e("probleme***************************************************************************************************************************" +
                        "", "onPostExecute: un probleme");
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Log.e("probleme***************************************************************************************************************************" +
                        "", "onPostExecute: un probleme");
            }

        }
    }


}
