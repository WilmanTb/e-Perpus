package com.perpustakaan.eperpus.User.Adapter;

import android.content.Context;
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
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.util.ArrayList;

public class Tunggu_Adapter extends RecyclerView.Adapter<Tunggu_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Peminjaman_Model> list;

    public Tunggu_Adapter(Context context, ArrayList<Peminjaman_Model> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_tunggu, parent, false);
        return new Tunggu_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Peminjaman_Model peminjamanModel = list.get(position);
        holder.judul_buku.setText(peminjamanModel.getJudulBuku());
        holder.pengarang_buku.setText(peminjamanModel.getPengarang());
        holder.status_konfirmasi.setText(peminjamanModel.getStatus());
        Glide.with(context).load(list.get(position).getGambar()).into(holder.gambar_buku);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView pengarang_buku, judul_buku, status_konfirmasi;
        ImageView gambar_buku;
        CardView cv_listTunggu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pengarang_buku = itemView.findViewById(R.id.pengarang_buku);
            judul_buku = itemView.findViewById(R.id.judul_buku);
            status_konfirmasi = itemView.findViewById(R.id.status_konfirmasi);
            gambar_buku = itemView.findViewById(R.id.gambar_buku);
            cv_listTunggu = itemView.findViewById(R.id.cv_listTunggu);
        }
    }
}
