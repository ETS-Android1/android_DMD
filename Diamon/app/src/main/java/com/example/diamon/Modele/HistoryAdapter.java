package com.example.diamon.Modele;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamon.R;

public class HistoryAdapter  extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    public Context context;
    public  String data[][];
    /**
     * data[value][name]
     */

    public HistoryAdapter(Context ct, String[][] s1) {
        context = ct;
        data = s1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.history_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyViewHolder holder, int position) {
            /**   1-> type;
             *    2-> somme
             *    3-> receiver
             *    4->sender
             *    5->dat
            */
        holder.txt_type.setText(data[position][1]);
        holder.txt_somme.setText(data[position][2]+" "+"DMD");
        holder.txt_dat.setText(data[position][5]);
        if(data[position][1].equals("receive")){
            holder.txt_to_from.setText("From "+data[position][4]);
            holder.imageView.setImageResource(R.drawable.ic_receive_h);
            holder.line.setBackgroundColor(Color.parseColor("#6627F46A"));
        }else{
            holder.txt_to_from.setText("To "+data[position][3]);
        }
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView txt_type,txt_somme,txt_dat,txt_to_from;
        ImageView imageView;
        LinearLayout line;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_type = itemView.findViewById(R.id.id_type);
            txt_somme = itemView.findViewById(R.id.id_somme);
            txt_to_from = itemView.findViewById(R.id.id_to_from);
            txt_dat = itemView.findViewById(R.id.id_dat);
            imageView = itemView.findViewById(R.id.id_type_img);
            line = itemView.findViewById(R.id.linearLayout);
        }
    }

}
