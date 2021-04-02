package com.example.diamon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class DepositActivity extends AppCompatActivity {

        public Button btn_deposit;
        Spinner operateur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_deposit);

        operateur = findViewById(R.id.operateur);

        btn_deposit = findViewById(R.id.btn_deposit);
        btn_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DepositActivity.this, SendActivity.class);
                startActivity(intent);
            }
        });


        ArrayList<String> operateur_liste = new ArrayList<>();

       operateur_liste.add("carte Visa");
        operateur_liste.add("Perfect money");
        operateur_liste.add("Paypal");
        operateur_liste.add("payeer");
        operateur.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,operateur_liste));



    }
}