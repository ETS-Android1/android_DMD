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

import de.hdodenhof.circleimageview.CircleImageView;

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
         *    7->cetification"
         *    id_certified
         */

        holder.txt_name.setText(data[position][2]);
        holder.txt_type.setText(data[position][6]);
        holder.txt_lcl_pay.setText(data[position][4]);
        if(data[position][7].equals("true")){
            holder.certified_img.setVisibility(View.VISIBLE);
        }else{ holder.certified_img.setVisibility(View.GONE);}



        /**
         *
         * photo user
         */
        switch (data[position][2]){

            case  "Damron":
                holder.imageView.setImageResource(R.drawable.flag_danemark);
                holder.type_img.setImageResource(R.drawable.user5);
                holder.rank.setImageResource(R.drawable.p);
                break;

            case "Alejandro":
                holder.type_img.setImageResource(R.drawable.user1);
                holder.rank.setImageResource(R.drawable.d);
                break;

            case "Abdoule":
                holder.type_img.setImageResource(R.drawable.user6);
                break;

            case "Becki Haslett":
                holder.type_img.setImageResource(R.drawable.user4);
                holder.imageView.setImageResource(R.drawable.flag_japon);
                holder.rank.setImageResource(R.drawable.t);
                break;
            default:
                holder.type_img.setImageResource(R.drawable.ic_r_prof);
                holder.imageView.setImageResource(R.drawable.flag_ue);
                holder.rank.setVisibility(View.INVISIBLE);

        }

        switch (data[position][5]){

            case  "Togo":
                holder.imageView.setImageResource(R.drawable.flag_togo);
                break;
            case "Allemagne":
                holder.imageView.setImageResource(R.drawable.flag_allemagne);
                break;
            case "Autriche":
                holder.imageView.setImageResource(R.drawable.flag_autriche);
                break;
            case "Belgique":
                holder.imageView.setImageResource(R.drawable.flag_belgique);
                break;
            case "Danemark":
                holder.imageView.setImageResource(R.drawable.flag_danemark);
                break;
            case "Espagne":
                holder.imageView.setImageResource(R.drawable.flag_espagne);
                break;
            case "Finlande":
                holder.imageView.setImageResource(R.drawable.flag_finlande);
                break;
            case "France":
                holder.imageView.setImageResource(R.drawable.flag_france);
                break;
            case "Grèce":
                holder.imageView.setImageResource(R.drawable.flag_grece);
                break;
            case "Hongrie":
                holder.imageView.setImageResource(R.drawable.flag_hongri);
                break;
            case "Irlande":
                holder.imageView.setImageResource(R.drawable.flag_irlande);
                break;
            case "Italie":
                holder.imageView.setImageResource(R.drawable.flag_italie);
                break;
            case "Luxembourg":
                holder.imageView.setImageResource(R.drawable.flag_luxembourg);
                break;

            case "Bénin":
                holder.imageView.setImageResource(R.drawable.flag_benin);
                break;

            case "Burkina Faso":
                holder.imageView.setImageResource(R.drawable.flag_burkina);
                break;
            case "Cameroun":
                holder.imageView.setImageResource(R.drawable.flag_uruguay);
                break;
            case "Congo":
                holder.imageView.setImageResource(R.drawable.flag_congo);
                break;
            case "Chine":
                holder.imageView.setImageResource(R.drawable.flag_chine);
                break;
            case "Ghana":
                holder.imageView.setImageResource(R.drawable.flag_ghana);
                break;

        }

        boolean installed = isAppInstalled("com.yowhatsapp");



        holder.p_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (installed){
                    if(!data[position][3].equals("1")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+data[position][3]+"&text=Hi"));
                    context.startActivity(intent);}else{
                        Toast.makeText(context,"Private supplier you are not authorized to contact",Toast.LENGTH_LONG).show();
                    }
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
        CircleImageView type_img,certified_img;
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
            type_img = itemView.findViewById(R.id.id_type_img);
            certified_img =itemView.findViewById(R.id.id_certified);


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
