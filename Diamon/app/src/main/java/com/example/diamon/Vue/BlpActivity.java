package com.example.diamon.Vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.diamon.Modele.HistoryAdapter;
import com.example.diamon.Modele.ProviderAdapter;
import com.example.diamon.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BlpActivity extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    //public static final String URL_MAIN ="http://dmd.moulenetadi.com/dmd_work.php";
    //public static final String URL_DMD ="http://dmd.moulenetadi.com/function_valeur_dmd.php";
    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="";

    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    private Button pop_cancel, pop_save;
    FloatingActionButton add_btn;

    RecyclerView recyclerView;
    String[] s1= new String[8] ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_blp);

        add_btn = findViewById(R.id.id_add_blp);


        Intent blp_intent= getIntent();
        Bundle bundle = blp_intent.getExtras();
        if(bundle!=null)
        {
            USER_ACCOUNT_ID =(String) bundle.get("user_account_id");
            if(isConnectingToInternet(BlpActivity.this)) {
                Toast.makeText(getApplicationContext(),"internet is available", Toast.LENGTH_LONG).show();
               // select_provider();
            }
            else {
                Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
            }

        }

        recyclerView = findViewById(R.id.id_provider_recyclerview);
        s1[0]="Koffi blaise";
        s1[1]="Alex Manu";
        s1[2]="Leo Snart";
        s1[3]="Kob bryan";
        s1[4]="Koffi blaise";
        s1[5]="Alex Manu";
        s1[6]="Leo Snart";
        s1[7]="Kob bryan";
        ProviderAdapter providerAdapter = new ProviderAdapter(BlpActivity.this,s1);
        recyclerView.setAdapter(providerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlpActivity.this));

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
                /// code save


            }
        });
    }
}