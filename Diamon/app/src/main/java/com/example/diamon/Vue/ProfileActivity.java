package com.example.diamon.Vue;

import androidx.appcompat.app.AppCompatActivity;
import com.example.diamon.Controleur.Controle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.diamon.Controleur.Controle;
import com.example.diamon.R;

public class ProfileActivity extends AppCompatActivity {

    private Button btn_user;
    private Button btn_user_select;
    private TextView txt_user;
    private Controle controle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btn_user = findViewById(R.id.btn_user);
        btn_user_select = findViewById(R.id.btn_user_select);
        txt_user = findViewById(R.id.txt_user);
        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
        btn_user_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recupUsers();
            }
        });


    }


     public void init(){
         this.controle = Controle.getInstance(this);
         this.controle.creerUser("moi","monpseudo","pwd");

            }


    public void recupUsers(){
        this.controle = Controle.getInstance(this);
       // this.controle.selectUser();
        txt_user.setText(controle.getNom()+" "+controle.getPseudo());
    }

}