package com.example.diamon.Vue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.diamon.R;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PageMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Button btn_sell,btn_buy;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_page_main);


        drawerLayout = findViewById(R.id.layout);
        navigationView = findViewById(R.id.id_nav);
     // toolbar = findViewById(R.id.toolbar);
        btn_sell = findViewById(R.id.btn_sell);
        btn_buy = findViewById(R.id.btn_buy);

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PageMainActivity.this, DepositActivity.class);
                startActivity(intent);
            }
        });



        setSupportActionBar(findViewById(R.id.toolbar));


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,
                findViewById(R.id.toolbar),R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

























































        GraphView graph = (GraphView) findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {

                new DataPoint(0, 0.1),
                new DataPoint(0.2, 0.2),
                new DataPoint(0.3, 0.5),
                new DataPoint(0.7, 1),
                new DataPoint(1, 1.5),
                new DataPoint(1.5, 1.8),
                new DataPoint(1.6, 2),
                new DataPoint(1.7, 2.1),
                new DataPoint(1.8, 2.2),
                new DataPoint(1.9, 2),
                new DataPoint(2, 2.1),
                new DataPoint(2.1, 2.1),
                new DataPoint(2.2, 2.5),
                new DataPoint(2.3, 2.3),
                new DataPoint(2.4, 2.2),
                new DataPoint(2.5, 2.6),
                new DataPoint(2.6, 2.1),
                new DataPoint(2.7, 2.5),
                new DataPoint(2.8, 2),
                new DataPoint(2.9, 2.2),
                new DataPoint(3, 2),
                new DataPoint(3.2, 1.9),
                new DataPoint(3.8, 1.7),
                new DataPoint(4, 1.5),
                new DataPoint(4.1, 1.3),
                new DataPoint(4.2, 1.2),
                new DataPoint(4.3, 1.6),
                new DataPoint(4.4, 1.1),
                new DataPoint(4.5, 1.5),
                new DataPoint(4.6, 1.3)

        }
        );

        series.setDrawBackground(true);
        series.setColor(Color.rgb(173,	193	,197));
        graph.getViewport().setDrawBorder(false);
        graph.getGridLabelRenderer().setHighlightZeroLines(false);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.addSeries(series);
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
                Toast.makeText(this,"home clicked",Toast.LENGTH_LONG).show();
                break;

            case R.id.setting:
                Intent intent_tran = new Intent(PageMainActivity.this, TransactionsActivity.class);
                startActivity(intent_tran);
                break;

            case R.id.deposit:
                Intent intent_deposit = new Intent(PageMainActivity.this, DepositActivity.class);
                startActivity(intent_deposit);
                break;

            case R.id.send:
                Intent intent_send = new Intent(PageMainActivity.this, SendActivity.class);
                startActivity(intent_send);
                break;

            case R.id.withdrawal:
                Intent intent_wi = new Intent(PageMainActivity.this, withdrawalActivity.class);
                startActivity(intent_wi);
                break;

            case R.id.blp:
                Intent intent_blp = new Intent(PageMainActivity.this, BlpActivity.class);
                startActivity(intent_blp);
                break;

            case R.id.identity:
                Intent intent_profil = new Intent(PageMainActivity.this, ProfileActivity.class);
                startActivity(intent_profil);
                break;

        }


        return true;
    }
}