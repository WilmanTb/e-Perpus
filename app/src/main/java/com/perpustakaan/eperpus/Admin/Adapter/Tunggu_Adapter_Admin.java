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
import com.perpustakaan.eperpus.Admin.Activity.Konfirmasi_Peminjaman;
import com.perpustakaan.eperpus.Admin.Activity.Konfirmasi_Pengembalian;
import com.perpustakaan.eperpus.Admin.Fragment.Daftar_Tunggu;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.util.ArrayList;

public class Tunggu_Adapter_Admin extends RecyclerView.Adapter<Tunggu_Adapter_Admin.MyViewHolder> {

    Context context;
    ArrayList<Peminjaman_Model> list;

    public Tunggu_Adapter_Admin(Context context, ArrayList<Peminjaman_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_daftar_tunggu, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Peminjaman_Model peminjamanModel = list.get(position);
        holder.nama_peminjam.setText(peminjamanModel.getPeminjam());
        holder.tanggal_pinjam.setText(peminjamanModel.getTanggalPeminjaman());
        holder.npm_peminjam.setText(peminjamanModel.getNpm());
        Glide.with(context).load(list.get(position).getGambar()).into(holder.gambar_buku);
        holder.cv_listTunggu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Daftar_Tunggu.x == 1) {
                    context.startActivity(new Intent(context, Konfirmasi_Peminjaman.class).putExtra("deskripsi_tunggu", list.get(holder.getAdapterPosition())));
                } else if (Daftar_Tunggu.x == 4) {
                    context.startActivity(new Intent(context, Konfirmasi_Pengembalian.class).putExtra("deskripsi_tunggu2", list.get(holder.getAdapterPosition())));
                } else if (Daftar_Tunggu.x == 5) {
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama_peminjam, npm_peminjam, tanggal_pinjam;
        ImageView gambar_buku;
        CardView cv_listTunggu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nama_peminjam = itemView.findViewById(R.id.nama_peminjam);
            npm_peminjam = itemView.findViewById(R.id.npm_peminjam);
            tanggal_pinjam = itemView.findViewById(R.id.tanggal_pinjam);
            gambar_buku = itemView.findViewById(R.id.gambar_buku);
            cv_listTunggu = itemView.findViewById(R.id.cv_listTunggu);
        }
    }
}
