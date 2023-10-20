package com.perpustakaan.eperpus.Admin.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.perpustakaan.eperpus.Admin.Adapter.Keranjang_Adapter;
import com.perpustakaan.eperpus.Admin.Adapter.Tunggu_Adapter_Admin;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Adapter.Tunggu_Adapter;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.util.ArrayList;

public class Daftar_Tunggu extends Fragment {

    ViewGroup root;
    RecyclerView rc_listTunggu;
    DatabaseReference dbTunggu;
    ArrayList<Peminjaman_Model> listTunggu;
    Tunggu_Adapter_Admin adapterTunggu;
    AutoCompleteTextView autoCompletePasscode;
    Button btn_cariBuku;
    String Passcode = "empty";
    public static String KeyKeranjangPinjam, KeyPeminjaman;
    public static int x;
    public static boolean status;
    int i, y, m;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_daftar_tunggu, container, false);

        x = 1;

        rc_listTunggu = root.findViewById(R.id.rc_listTunggu);
        autoCompletePasscode = root.findViewById(R.id.autoCompletePasscode);
        btn_cariBuku = root.findViewById(R.id.btn_cariBuku);

        rc_listTunggu.setHasFixedSize(true);
        rc_listTunggu.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        listTunggu = new ArrayList<>();
        adapterTunggu = new Tunggu_Adapter_Admin(getActivity(), listTunggu);
        rc_listTunggu.setAdapter(adapterTunggu);

        dbTunggu = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        btn_cariBuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPeminjaman();
            }
        });

        getData();
        getKeranjangTunggu();


        return root;
    }

    private void getData() {
        dbTunggu.child("Peminjaman").orderByChild("status").equalTo("Menunggu dikonfirmasi admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTunggu.clear();
                i = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        KeyPeminjaman = dataSnapshot.getKey();
                        Peminjaman_Model peminjamanModel = dataSnapshot.getValue(Peminjaman_Model.class);
                        listTunggu.add(peminjamanModel);
                        dbTunggu.child("Users").child(peminjamanModel.getPeminjam()).addValueEventListener(new ValueEventListener() {
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

    private void getKeranjangTunggu() {
        dbTunggu.child("KeranjangPinjam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        KeyKeranjangPinjam = dataSnapshot.getKey();
                        y = 0;
                        dbTunggu.child("KeranjangPinjam").child(KeyKeranjangPinjam).orderByChild("status")
                                .equalTo("Menunggu dikonfirmasi admin").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                            Peminjaman_Model peminjamanModel = dataSnapshot1.getValue(Peminjaman_Model.class);
                                            listTunggu.add(peminjamanModel);
                                            dbTunggu.child("Users").child(peminjamanModel.getPeminjam()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        listTunggu.get(y).setPeminjam(snapshot.child("nama").getValue().toString());
                                                        y++;
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

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPeminjaman() {
        Passcode = autoCompletePasscode.getText().toString();
        Toast.makeText(getActivity(), Passcode, Toast.LENGTH_SHORT).show();
        /*if (!Passcode.equals("empty")) {
            Toast.makeText(getActivity(), Passcode, Toast.LENGTH_SHORT).show();*/
        dbTunggu.child("Peminjaman").orderByChild("passcode").equalTo(Passcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listTunggu.clear();
                    m = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Peminjaman_Model peminjamanModel = dataSnapshot.getValue(Peminjaman_Model.class);
                        listTunggu.add(peminjamanModel);
                        dbTunggu.child("Users").child(peminjamanModel.getPeminjam()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    listTunggu.get(m).setPeminjam(snapshot.child("nama").getValue().toString());
                                    m++;
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

                } else {
                    dbTunggu.child("KeranjangPinjam").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String Key = dataSnapshot.getKey();
                                    dbTunggu.child("KeranjangPinjam").child(Key).orderByChild("passcode").equalTo(Passcode).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                listTunggu.clear();
                                                m = 0;
                                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                                    Peminjaman_Model peminjamanModel = dataSnapshot1.getValue(Peminjaman_Model.class);
                                                    if (peminjamanModel.getStatus().equals("Menunggu dikonfirmasi admin")) {
                                                        listTunggu.add(peminjamanModel);
                                                        dbTunggu.child("Users").child(peminjamanModel.getPeminjam()).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    listTunggu.get(m).setPeminjam(snapshot.child("nama").getValue().toString());
                                                                    m++;
                                                                    peminjamanModel.setPeminjam(snapshot.child("nama").getValue().toString());
                                                                    adapterTunggu.notifyDataSetChanged();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
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
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      /*  } else {
            Toast.makeText(getActivity(), "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }*/
    }
}
