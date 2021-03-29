package com.example.diamon;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class PageMainActivity extends AppCompatActivity {

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


        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,
           findViewById(R.id.toolbar),R.string.nav_open,R.string.nav_close);
            drawerLayout.addDrawerListener(toggle);
                toggle.syncState();


        btn_sell = findViewById(R.id.btn_sell);
        btn_buy = findViewById(R.id.btn_buy);

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PageMainActivity.this, DepositActivity.class);
                startActivity(intent);
            }
        });

    }

}