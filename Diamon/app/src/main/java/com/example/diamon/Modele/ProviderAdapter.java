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
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamon.R;
import com.example.diamon.Vue.BlpActivity;
import com.example.diamon.Vue.PageMainActivity;
import com.example.diamon.Vue.ProfileActivity;
import com.example.diamon.Vue.SendActivity;
import com.example.diamon.Vue.TransactionsActivity;
import com.example.diamon.Vue.withdrawalActivity;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.MyViewHolder> {
    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    public Context context;
    public  String data[][];

    public ProviderAdapter(Context ct,String[][] s1) {
        context = ct;
        data    = s1;
    }

    @NonNull
    @Override
    public ProviderAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.provider_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderAdapter.MyViewHolder holder, int position) {
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

       if(position==0){
           holder.rank.setImageResource(R.drawable.p);
       }else{
           if(position==1){
               holder.rank.setImageResource(R.drawable.d);
           }else{
               if(position==2){
               holder.rank.setImageResource(R.drawable.t);
                }else{
                   holder.rank.setVisibility(View.GONE);
               }
           }
       }


        switch (data[position][5]){

            case  "Togo":
                holder.imageView.setImageResource(R.drawable.flag_togo);
                break;

            case "Benin":
                holder.imageView.setImageResource(R.drawable.flag_benin);
                break;

            case "Burkina Faso":
                holder.imageView.setImageResource(R.drawable.flag_burkina);
                break;

            case "Uruguay":
                holder.imageView.setImageResource(R.drawable.flag_uruguay);
                break;

        }

        boolean installed = isAppInstalled("com.yowhatsapp");



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

        holder.p_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pop_activity();
                return  false;
            }
        });
    }



    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView txt_name, txt_type,txt_lcl_pay;
        ImageView imageView,rank;
        ConstraintLayout p_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name= itemView.findViewById(R.id.id_name);
            txt_type = itemView.findViewById(R.id.id_type);
            txt_lcl_pay = itemView.findViewById(R.id.id_lcl_pay);
            imageView = itemView.findViewById(R.id.id_flag);
            p_layout= itemView.findViewById(R.id.id_layout);
            rank = itemView.findViewById(R.id.id_rank);


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


    public void pop_activity(){

        dialogBuilder = new AlertDialog.Builder(context);
        final View pop_add_view= View.inflate(context,R.layout.buy_popup,null);

        dialogBuilder.setView(pop_add_view);
        dialog= dialogBuilder.create();
        dialog.show();

    }

}
