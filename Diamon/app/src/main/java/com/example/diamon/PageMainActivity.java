package com.example.diamon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class PageMainActivity extends AppCompatActivity {

    public Button btn_sell,btn_buy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_page_main);

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