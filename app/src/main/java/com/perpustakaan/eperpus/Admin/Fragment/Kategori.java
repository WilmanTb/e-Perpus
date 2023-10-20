package com.perpustakaan.eperpus.Admin.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.Admin.Adapter.Kategori_Adapter;
import com.perpustakaan.eperpus.Admin.Class.Kategori_Model;
import com.perpustakaan.eperpus.R;

import java.util.ArrayList;

public class Kategori extends Fragment {

    ViewGroup root;
    RecyclerView rc_listKategori;
    ArrayList<Kategori_Model> listKategori;
    Kategori_Adapter adapterKategori;
    DatabaseReference dbKategori;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_kategori, container, false);

        rc_listKategori = root.findViewById(R.id.rc_listKategori);
        rc_listKategori.setHasFixedSize(true);
        rc_listKategori.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        listKategori = new ArrayList<>();
        adapterKategori = new Kategori_Adapter(getActivity(), listKategori);
        rc_listKategori.setAdapter(adapterKategori);

        dbKategori = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Daftar Kategori");

        getDataKategori();

        return root;
    }

    private void getDataKategori(){
        dbKategori.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listKategori.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Kategori_Model kategoriModel = dataSnapshot.getValue(Kategori_Model.class);
                        listKategori.add(kategoriModel);
                    }
                    adapterKategori.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
