
package com.example.diamon.Vue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.Build;
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
import com.example.diamon.Modele.SelectProvider;
import com.example.diamon.Modele.Select_In_Db;
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
    TextView txt_user_pseudo,txt_taux,txt_prix,txt_valeur,txt_old,txt_new,txt_nb_dmd,txt_account_id;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    public static final String URL_DMD ="http://192.168.1.120/dmd/function_valeur_dmd.php";
    //public static final String URL_MAIN ="http://dmd.moulenetadi.com/dmd_work.php";
    //public static final String URL_DMD ="http://dmd.moulenetadi.com/function_valeur_dmd.php";


    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="",DMD_VALUE="",DMD_TAUX="0", DMD_PRIX="",PWD="";
    public String BALANCE;
    public float OLD_TAUX=0,NEW_TAUX;
    private NotificationManager mNotificationManager;

    RecyclerView recyclerView1,recyclerView2;
    String s1[][] = new String[4][7];
    String s2[][]  = new String[4][7];
    Spinner code_pays;
    String le_link="";

    boolean provider_result = false;
    Select_In_Db select_in_db = new Select_In_Db();
    SelectProvider selectProvider = new SelectProvider();

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
        btn_graph = findViewById(R.id.btn_graph);
        txt_user_pseudo = header.findViewById(R.id.id_user_pseudo);
        txt_account_id  = header.findViewById(R.id.id_id_account);
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
            }
        });

        btn_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent1 = new Intent(Intent.ACTION_VIEW,Uri.parse("https://cryptoast.fr/cours-diamond-77.html"));
                startActivity(webIntent1);
            }
        });
        /**
         * btn_sell
         */

        btn_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop_buy();
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
            select_in_db.check_version();
            Toast.makeText(getApplicationContext(),"internet is available", Toast.LENGTH_LONG).show();
            select_user(USER_PSEUDO);
            select_dmd();

            selectProvider.select_lp("");

            check_provider_result();
        /**
             * ATTENDRE 10 SECONDES AVANT DE RECUPERE LE POPUP VERSION
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(select_in_db.getVersion()!="") {
                        pop_activity();
                    }
                }
            },10000);

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

            /*case R.id.withdrawal:
                Intent intent_wi = new Intent(PageMainActivity.this, withdrawalActivity.class);
                startActivity(intent_wi);
                break;*/

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

           /* case R.id.chat:
                Intent intent_chat = new Intent(PageMainActivity.this, ChatActivity.class);
                intent_chat.putExtra("user_name",USER_NAME);
                intent_chat.putExtra("user_pseudo",USER_PSEUDO);
                intent_chat.putExtra("user_account_id",USER_ACCOUNT_ID);
                startActivity(intent_chat);
                break;*/

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
                        txt_account_id.setText("ACCOUNT ID "+USER_ACCOUNT_ID);
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
        TextView txt;
        install_btn = pop_add_view.findViewById(R.id.id_install);

        /**
         * TXT POUR AFFICHER LE TEXT DU POP UP
         */
        txt = pop_add_view.findViewById(R.id.txt);

        dialogBuilder.setView(pop_add_view);
        dialog= dialogBuilder.create();
        dialog.show();


        String link = select_in_db.getVersion();
        le_link= link;
        if(le_link!=""){
        notif_version(le_link);}

       // Toast.makeText(PageMainActivity.this,select_in_db.version,Toast.LENGTH_LONG).show();

        install_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_web(link);
            }
        });

    }



    /**
     * SELECTION DES INFOS PROVIDER
     *
     */



    public boolean check_provider_result() {

        if (provider_result == false){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (selectProvider.getS1() == null) {
                        provider_result = false;
                        check_provider_result();
                    } else  {
                        recyclerView2 = findViewById(R.id.id_recy_pro_2);


                        ProviderAdapter providerAdapter = new ProviderAdapter(PageMainActivity.this,selectProvider.getS1());
                        recyclerView2.setAdapter(providerAdapter);
                        recyclerView2.setLayoutManager(new LinearLayoutManager(PageMainActivity.this));

                        provider_result = true;
                    }
                }
            }, 1000);
        }
        return provider_result;

    }


    /**
     * FONCTION POUR OUVRIR UN LIEN
     *
     */

    public void open_web(String link){
        Intent webIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
        startActivity(webIntent);
    }




    public void notif_version(String lien){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");
       // Intent intent_by_notif = new Intent(getApplicationContext().getApplicationContext(),PageMainActivity.class);
        Intent intent_by_notif  = new Intent(Intent.ACTION_VIEW,Uri.parse(lien));
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent_by_notif, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo_dmd);
        mBuilder.setContentTitle("New Version Available");
        mBuilder.setContentText("Update now");
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
       // Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();


    }


}