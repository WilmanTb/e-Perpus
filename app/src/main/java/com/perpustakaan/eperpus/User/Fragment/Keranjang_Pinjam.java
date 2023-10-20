package com.perpustakaan.eperpus.User.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.perpustakaan.eperpus.User.Activity.Desc_Buku;
import com.perpustakaan.eperpus.User.Activity.Passcode;
import com.perpustakaan.eperpus.User.Activity.Passcode2;
import com.perpustakaan.eperpus.User.Adapter.Keranjang_Adapter;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import papaya.in.sendmail.SendMail;

public class Keranjang_Pinjam extends Fragment {

    ViewGroup root;
    RecyclerView rc_keranjangPinjam;
    Keranjang_Adapter adapterKeranjang;
    public static ArrayList<Peminjaman_Model> listKeranjang;
    DatabaseReference dbKeranjang;
    FirebaseAuth userAuth;
    String UID,Email;
    Button btn_pinjam;
    public static int passcodeNumber;
    private String tanggalPinjam, tanggalKembali;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_keranjang_pinjam, container, false);

        rc_keranjangPinjam = root.findViewById(R.id.rc_keranjangPinjam);
        btn_pinjam = root.findViewById(R.id.btn_pinjam);
        rc_keranjangPinjam.setHasFixedSize(true);
        rc_keranjangPinjam.setLayoutManager(new LinearLayoutManager(getActivity()));
        listKeranjang = new ArrayList<>();
        adapterKeranjang = new Keranjang_Adapter(getActivity(), listKeranjang);
        rc_keranjangPinjam.setAdapter(adapterKeranjang);
        userAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = userAuth.getCurrentUser();
        UID = firebaseUser.getUid();

        dbKeranjang = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        btn_pinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePasscode();
                Toast.makeText(getActivity(), Email, Toast.LENGTH_SHORT).show();
                SendMail mail = new SendMail("rizki01tebe@gmail.com", "wlbvfhpnbyectbpw",
                        Email,
                        "Passcode Peminjaman Buku",
                        "BERIKUT PASSCODE PEMINJAMAN BUKU ANDA\n\n\n" + passcodeNumber);

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("Yakin meminjam buku ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mail.execute();
                        startActivity(new Intent(getActivity(), Passcode2.class));
                    }
                });
            }
        });

        getData();
        getEmail();
        return root;

    }

    private void getData(){
        dbKeranjang.child("KeranjangPinjam").child(UID).orderByChild("diminta").equalTo("false").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listKeranjang.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Peminjaman_Model peminjamanModel = dataSnapshot.getValue(Peminjaman_Model.class);
                        listKeranjang.add(peminjamanModel);
                    }
                    adapterKeranjang.notifyDataSetChanged();
                } else {
                    btn_pinjam.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getEmail(){
        dbKeranjang.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Email = snapshot.child("email").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generatePasscode() {
        final int min = 100000;
        final int max = 1000000;
        final int random = new Random().nextInt((max - min) + 1) + min;
        passcodeNumber = random;
    }
}
