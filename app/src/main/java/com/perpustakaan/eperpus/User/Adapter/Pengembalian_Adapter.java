package com.perpustakaan.eperpus.User.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.util.ArrayList;

public class Pengembalian_Adapter extends RecyclerView.Adapter<Pengembalian_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Peminjaman_Model> list;

    public Pengembalian_Adapter(Context context, ArrayList<Peminjaman_Model> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_pengembalian, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Peminjaman_Model peminjamanModel = list.get(position);
        holder.judul_buku.setText(peminjamanModel.getJudulBuku());
        holder.pengarang_buku.setText(peminjamanModel.getPengarang());
        holder.tanggal_pinjam.setText(peminjamanModel.getTanggalKembali());
        Glide.with(context).load(list.get(position).getGambar()).into(holder.gambar_buku);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView judul_buku,pengarang_buku,tanggal_pinjam;
        ImageView gambar_buku;
        CardView cv_listPeminjaman;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            judul_buku = itemView.findViewById(R.id.judul_buku);
            pengarang_buku = itemView.findViewById(R.id.pengarang_buku);
            tanggal_pinjam = itemView.findViewById(R.id.tanggal_kembali);
            gambar_buku = itemView.findViewById(R.id.gambar_buku);
            cv_listPeminjaman = itemView.findViewById(R.id.cv_listPeminjaman);
        }
    }
}
