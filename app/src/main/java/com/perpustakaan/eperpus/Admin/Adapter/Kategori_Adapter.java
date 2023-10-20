package com.perpustakaan.eperpus.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.perpustakaan.eperpus.Admin.Activity.Edit_Kategori;
import com.perpustakaan.eperpus.Admin.Class.Kategori_Model;
import com.perpustakaan.eperpus.Admin.Fragment.Kategori;
import com.perpustakaan.eperpus.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Kategori_Adapter extends RecyclerView.Adapter<Kategori_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Kategori_Model> list;

    public Kategori_Adapter(Context context, ArrayList<Kategori_Model> list){
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_kategori,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Kategori_Model kategoriModel = list.get(position);
        holder.nama_kategori.setText(kategoriModel.getKategori());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama_kategori;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama_kategori = itemView.findViewById(R.id.nama_kategori);
        }
    }

}
