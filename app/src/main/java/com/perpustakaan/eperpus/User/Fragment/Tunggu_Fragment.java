package com.perpustakaan.eperpus.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.perpustakaan.eperpus.User.Adapter.Tunggu_Adapter;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;
import com.sun.mail.imap.protocol.UID;

import java.util.ArrayList;

public class Tunggu_Fragment extends Fragment {

    ViewGroup root;
    RecyclerView rc_listTunggu;
    DatabaseReference dbTunggu;
    ArrayList<Peminjaman_Model> listTunggu;
    Tunggu_Adapter adapterTunggu, adapterTunggu2;
    FirebaseAuth userAuth;
    String Key;
    ArrayList<Peminjaman_Model> listKeranjangTunggu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_tunggu, container, false);

        rc_listTunggu = root.findViewById(R.id.rc_listTunggu);
        rc_listTunggu.setHasFixedSize(true);
        rc_listTunggu.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        listTunggu = new ArrayList<>();
        listKeranjangTunggu = new ArrayList<>();
        adapterTunggu = new Tunggu_Adapter(getActivity(), listTunggu);
        rc_listTunggu.setAdapter(adapterTunggu);
        userAuth = FirebaseAuth.getInstance();

        dbTunggu = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Peminjaman");

        getKeyTunggu();
        getKeranjangTunggu();

        return root;

    }

    private void getKeyTunggu() {
        FirebaseUser currentUser = userAuth.getCurrentUser();
        String UID = currentUser.getUid();
        dbTunggu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listTunggu.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                       Peminjaman_Model peminjamanModel = dataSnapshot.getValue(Peminjaman_Model.class);
                        if (peminjamanModel.getStatus().equals("Menunggu dikonfirmasi admin") && peminjamanModel.getPeminjam().equals(UID)){
                            listTunggu.add(peminjamanModel);
                        }
                    }
                    adapterTunggu.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getKeranjangTunggu(){
        FirebaseUser firebaseUser = userAuth.getCurrentUser();
        String UID = firebaseUser.getUid();

        DatabaseReference dbKeranjang = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("KeranjangPinjam");

        dbKeranjang.child(UID).orderByChild("status").equalTo("Menunggu dikonfirmasi admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Peminjaman_Model peminjamanModel = dataSnapshot.getValue(Peminjaman_Model.class);
                        listTunggu.add(peminjamanModel);
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
