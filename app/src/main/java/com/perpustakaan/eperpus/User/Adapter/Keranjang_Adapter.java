package com.perpustakaan.eperpus.User.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.util.ArrayList;

public class Keranjang_Adapter extends RecyclerView.Adapter<Keranjang_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Peminjaman_Model> list;
    DatabaseReference dbPeminjaman;

    public Keranjang_Adapter(Context context, ArrayList<Peminjaman_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_daftar_tunggu, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Peminjaman_Model peminjamanModel = list.get(position);
        holder.nama_user.setText(peminjamanModel.getJudulBuku());
        holder.npm_user.setText(peminjamanModel.getPengarang());
        Glide.with(context).load(list.get(position).getGambar()).into(holder.gambar_buku);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama_user, npm_user;
        ImageView gambar_buku;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nama_user = itemView.findViewById(R.id.nama_peminjam);
            npm_user = itemView.findViewById(R.id.npm_peminjam);
            gambar_buku = itemView.findViewById(R.id.gambar_buku);
        }
    }
}
