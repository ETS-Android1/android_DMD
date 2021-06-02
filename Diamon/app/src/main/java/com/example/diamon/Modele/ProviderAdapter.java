package com.example.diamon.Modele;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamon.R;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.MyViewHolder> {

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
        /**   1->
         *    2-> provider_name;
         *    3-> number
         *    4->pay_agent
         *    5->ville_pays
         *    6->type"
         */
        holder.txt_name.setText(data[position][2]);
        holder.txt_type.setText(data[position][6]);
        holder.txt_lcl_pay.setText(data[position][4]);

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView txt_name, txt_type,txt_lcl_pay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name= itemView.findViewById(R.id.id_name);
            txt_type = itemView.findViewById(R.id.id_type);
            txt_lcl_pay = itemView.findViewById(R.id.id_lcl_pay);


        }
    }

}
