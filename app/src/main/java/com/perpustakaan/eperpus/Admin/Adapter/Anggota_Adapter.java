package com.perpustakaan.eperpus.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.perpustakaan.eperpus.Admin.Activity.Desc_Anggota;
import com.perpustakaan.eperpus.Admin.Class.Anggota_Model;
import com.perpustakaan.eperpus.R;

import java.util.ArrayList;

public class Anggota_Adapter extends RecyclerView.Adapter<Anggota_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Anggota_Model> listAnggota;

    public Anggota_Adapter(Context context, ArrayList<Anggota_Model> listAnggota){
        this.listAnggota = listAnggota;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_anggota, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Anggota_Model anggotaModel = listAnggota.get(position);
        holder.nama_user.setText(anggotaModel.getNama());
        holder.npm_user.setText(anggotaModel.getNpm());
        Glide.with(context).load(listAnggota.get(position).getFoto()).into(holder.foto_user);
        holder.listAnggota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, Desc_Anggota.class).putExtra("desc_anggota", listAnggota.get(holder.getAdapterPosition())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAnggota.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama_user, npm_user;
        ImageView foto_user;
        CardView listAnggota;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nama_user = itemView.findViewById(R.id.nama_user);
            npm_user = itemView.findViewById(R.id.npm_user);
            foto_user = itemView.findViewById(R.id.foto_user);
            listAnggota = itemView.findViewById(R.id.cv_listAnggtoa);
        }
    }
}
