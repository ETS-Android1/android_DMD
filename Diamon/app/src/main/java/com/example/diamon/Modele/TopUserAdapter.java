package com.example.diamon.Modele;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamon.R;
import com.example.diamon.Vue.BlpActivity;
import com.example.diamon.Vue.PageMainActivity;
import com.example.diamon.Vue.ProfileActivity;
import com.example.diamon.Vue.SendActivity;
import com.example.diamon.Vue.TransactionsActivity;
import com.example.diamon.Vue.withdrawalActivity;

public class TopUserAdapter extends RecyclerView.Adapter<TopUserAdapter.MyViewHolder> {

    public Context context;
    public  String data[][];

    public TopUserAdapter(Context ct,String[][] s1) {
        context = ct;
        data    = s1;
    }

    @NonNull
    @Override
    public TopUserAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.users_row_h,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopUserAdapter.MyViewHolder holder, int position) {
        /**
         *    1->
         *    2-> provider_name;
         *    3-> number
         *    4->pay_agent
         *    5->ville_pays
         *    6->type"
         */

        holder.txt_name.setText(data[position][2]);
        holder.txt_type.setText(data[position][6]);
        holder.txt_lcl_pay.setText(data[position][4]);

        switch (data[position][5]){

            case  "togo":
                holder.imageView.setImageResource(R.drawable.flag_togo);
                break;

            case "Benin":
                holder.imageView.setImageResource(R.drawable.flag_benin);
                break;
/*
            case R.id.send:
                Intent intent_send = new Intent(PageMainActivity.this, SendActivity.class);
                intent_send.putExtra("user_account_id",USER_ACCOUNT_ID);
                intent_send.putExtra("balance",BALANCE);
                intent_send.putExtra("pwd",PWD);
                startActivity(intent_send);
                break;

            case R.id.withdrawal:
                Intent intent_wi = new Intent(PageMainActivity.this, withdrawalActivity.class);
                startActivity(intent_wi);
                break;

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

            case R.id.chat:
                Toast.makeText(this,"chat clicked",Toast.LENGTH_LONG).show();
                break;

            case R.id.help:
                Toast.makeText(this,"help clicked",Toast.LENGTH_LONG).show();
                break;

            case R.id.logout:
                SharedPreferences preferences = getSharedPreferences("switch",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();
                finish();
                break;*/

        }

        boolean installed = isAppInstalled("com.whatsapp");



        holder.p_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (installed){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+data[position][3]+"&text=Hi"));
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context,"whatsapp is not intalled on your phone",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView txt_name, txt_type,txt_lcl_pay;
        ImageView imageView;
        ConstraintLayout p_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name= itemView.findViewById(R.id.id_name);
            txt_type = itemView.findViewById(R.id.id_type);
            txt_lcl_pay = itemView.findViewById(R.id.id_lcl_pay);
            imageView = itemView.findViewById(R.id.id_flag);
            p_layout= itemView.findViewById(R.id.id_layout);


        }
    }



    private boolean isAppInstalled(String s){
        PackageManager packageManager = context.getPackageManager();
        boolean is_intalled;

        try{
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            is_intalled = true;
        } catch (PackageManager.NameNotFoundException e){
            is_intalled = false;
            e.printStackTrace();
        }
        return  is_intalled;
    }

}
