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
import com.perpustakaan.eperpus.Admin.Adapter.Tunggu_Adapter_Admin;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.util.ArrayList;

public class Data_Peminjaman extends Fragment {

    ViewGroup root;
    RecyclerView rc_listPeminjaman;
    DatabaseReference dbPeminjaman;
    ArrayList<Peminjaman_Model> listTunggu;
    Tunggu_Adapter_Admin adapterTunggu;
    int i;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_data_peminjaman, container, false);

        Daftar_Tunggu.x = 3;

        rc_listPeminjaman = root.findViewById(R.id.rc_listPeminjaman);
        rc_listPeminjaman.setHasFixedSize(true);
        rc_listPeminjaman.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        listTunggu = new ArrayList<>();
        adapterTunggu = new Tunggu_Adapter_Admin(getActivity(), listTunggu);
        rc_listPeminjaman.setAdapter(adapterTunggu);

        dbPeminjaman = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        getData();

        return root;
    }

    private void getData() {
        dbPeminjaman.child("Peminjaman").orderByChild("status").equalTo("Telah dikonfirmasi admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTunggu.clear();
                i = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Peminjaman_Model peminjamanModel = dataSnapshot.getValue(Peminjaman_Model.class);
                        listTunggu.add(peminjamanModel);
                        dbPeminjaman.child("Users").child(peminjamanModel.getPeminjam()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    listTunggu.get(i).setPeminjam(snapshot.child("nama").getValue().toString());
                                    i++;
                                    peminjamanModel.setPeminjam(snapshot.child("nama").getValue().toString());
                                    adapterTunggu.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    adapterTunggu.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
