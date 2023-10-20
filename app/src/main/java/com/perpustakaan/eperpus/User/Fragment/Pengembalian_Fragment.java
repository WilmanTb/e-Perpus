package com.perpustakaan.eperpus.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Adapter.Peminjaman_Adapter;
import com.perpustakaan.eperpus.User.Adapter.Pengembalian_Adapter;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Pengembalian_Fragment extends Fragment {

    ViewGroup root;
    Pengembalian_Adapter adapter;
    ArrayList<Peminjaman_Model> list;
    RecyclerView rc_listPengembalian;
    DatabaseReference dbPeminjaman;
    FirebaseAuth userAuth;
    private Date tanggalPinjam, tanggalKembali;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_pengembalian, container, false);

        rc_listPengembalian = root.findViewById(R.id.rc_listPengembalian);
        rc_listPengembalian.setHasFixedSize(true);
        rc_listPengembalian.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        list = new ArrayList<>();
        adapter = new Pengembalian_Adapter(getActivity(), list);
        rc_listPengembalian.setAdapter(adapter);
        userAuth = FirebaseAuth.getInstance();

        dbPeminjaman = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        getKeyPeminjaman();
        getKeranjang();

        return  root;
    }

    private void getKeyPeminjaman() {
        FirebaseUser currentUser = userAuth.getCurrentUser();
        String UID = currentUser.getUid();
        getCurrentDate();
        dbPeminjaman.child("Peminjaman").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Peminjaman_Model peminjamanModel = snap.getValue(Peminjaman_Model.class);
                        if (peminjamanModel.getStatus().equals("Buku Telah dikembalikan") && peminjamanModel.getPeminjam().equals(UID)){
                            peminjamanModel.setTanggalKembali(String.valueOf(tanggalPinjam));
                            list.add(peminjamanModel);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        tanggalPinjam = calendar.getTime();
        calendar.add(Calendar.DATE, 7);
        tanggalKembali = calendar.getTime();
    }

    private void getKeranjang(){
        FirebaseUser currentUser = userAuth.getCurrentUser();
        String UID = currentUser.getUid();
        getCurrentDate();
        dbPeminjaman.child("KeranjangPinjam").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Peminjaman_Model peminjamanModel = dataSnapshot.getValue(Peminjaman_Model.class);
                        list.add(peminjamanModel);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
