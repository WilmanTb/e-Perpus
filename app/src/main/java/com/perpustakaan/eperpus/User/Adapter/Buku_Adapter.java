package com.perpustakaan.eperpus.User.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.perpustakaan.eperpus.Admin.Activity.Dashboard_Admin;
import com.perpustakaan.eperpus.Admin.Activity.Edit_Buku;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Activity.Desc_Buku;
import com.perpustakaan.eperpus.User.Class.Buku_Model;

import java.util.ArrayList;

public class Buku_Adapter extends RecyclerView.Adapter<Buku_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Buku_Model> list;

    public Buku_Adapter(Context context, ArrayList<Buku_Model> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_buku, parent, false);
        return new Buku_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Buku_Model bukuModel = list.get(position);
        holder.nama_buku.setText(bukuModel.getJudulBuku());
        holder.pengarang_buku.setText(bukuModel.getPengarang());
        holder.stok_buku.setText("Stok : " + bukuModel.getStokBuku());
        Glide.with(context).load(list.get(position).getGambar()).into(holder.gambar_buku);
        holder.cv_buku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Dashboard_Admin.i == 1) {
                    context.startActivity(new Intent(context, Desc_Buku.class).putExtra("deskripsi", list.get(holder.getAdapterPosition())));
                }else if (Dashboard_Admin.i == 0){
                    context.startActivity(new Intent(context, Edit_Buku.class).putExtra("deskripsi", list.get(holder.getAdapterPosition())));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama_buku, pengarang_buku, stok_buku;
        ImageView gambar_buku;
        CardView cv_buku;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nama_buku = itemView.findViewById(R.id.nama_buku);
            pengarang_buku = itemView.findViewById(R.id.pengarang_buku);
            stok_buku = itemView.findViewById(R.id.stok_buku);
            gambar_buku = itemView.findViewById(R.id.gambar_buku);
            cv_buku = itemView.findViewById(R.id.cv_buku);
        }
    }
}
