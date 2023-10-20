package com.perpustakaan.eperpus.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.util.ArrayList;

public class Peminjaman_Fragment extends Fragment {

    ViewGroup root;
    Peminjaman_Adapter adapter;
    ArrayList<Peminjaman_Model> list;
    RecyclerView rc_listPeminjaman;
    DatabaseReference dbPeminjaman;
    FirebaseAuth userAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_peminjaman, container, false);

        rc_listPeminjaman = root.findViewById(R.id.rc_listPeminjaman);
        rc_listPeminjaman.setHasFixedSize(true);
        rc_listPeminjaman.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        list = new ArrayList<>();
        adapter = new Peminjaman_Adapter(getActivity(), list);
        rc_listPeminjaman.setAdapter(adapter);
        userAuth = FirebaseAuth.getInstance();

        dbPeminjaman = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Peminjaman");

        getKeyPeminjaman();
        getKeranjangPinjam();


        return root;
    }

    private void getKeyPeminjaman() {
        FirebaseUser currentUser = userAuth.getCurrentUser();
        String UID = currentUser.getUid();
        dbPeminjaman.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Peminjaman_Model peminjamanModel = snap.getValue(Peminjaman_Model.class);
                        if (peminjamanModel.getStatus().equals("Telah dikonfirmasi admin") && peminjamanModel.getPeminjam().equals(UID)) {
                            list.add(peminjamanModel);
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

    private void getKeranjangPinjam() {
        FirebaseUser firebaseUser = userAuth.getCurrentUser();
        String UID = firebaseUser.getUid();

        DatabaseReference dbKeranjang = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("KeranjangPinjam");

        dbKeranjang.child(UID).orderByChild("status").equalTo("Telah dikonfirmasi admin").addValueEventListener(new ValueEventListener() {
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
