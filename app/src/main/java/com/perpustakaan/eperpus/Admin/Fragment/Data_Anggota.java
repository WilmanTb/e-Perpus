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
import com.perpustakaan.eperpus.Admin.Adapter.Anggota_Adapter;
import com.perpustakaan.eperpus.Admin.Class.Anggota_Model;
import com.perpustakaan.eperpus.R;

import java.util.ArrayList;

public class Data_Anggota extends Fragment {

    ViewGroup root;
    RecyclerView rc_listAnggota;
    Anggota_Adapter adapterAnggota;
    ArrayList<Anggota_Model> listAnggota;
    DatabaseReference dbAnggota;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_data_anggota,container, false);

        rc_listAnggota = root.findViewById(R.id.rc_listAnggota);
        rc_listAnggota.setHasFixedSize(true);
        listAnggota = new ArrayList<>();
        rc_listAnggota.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterAnggota = new Anggota_Adapter(getActivity(), listAnggota);
        rc_listAnggota.setAdapter(adapterAnggota);

        dbAnggota = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        getAnggota();


        return  root;
    }

    private void getAnggota(){
        dbAnggota.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAnggota.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Anggota_Model anggotaModel = dataSnapshot.getValue(Anggota_Model.class);
                        if (anggotaModel.getFoto().equals("empty")){
                            anggotaModel.setFoto("https://firebasestorage.googleapis.com/v0/b/eperpus-2fe42.appspot.com/o/user.png?alt=media&token=43289f11-e413-4369-a52e-33e20df962fb");
                            listAnggota.add(anggotaModel);
                            adapterAnggota.notifyDataSetChanged();
                        }

                    }
                    adapterAnggota.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
