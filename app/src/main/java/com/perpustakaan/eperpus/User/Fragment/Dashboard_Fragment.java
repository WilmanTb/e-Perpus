package com.perpustakaan.eperpus.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Adapter.Buku_Adapter;
import com.perpustakaan.eperpus.User.Class.Buku_Model;

import java.util.ArrayList;

public class Dashboard_Fragment extends Fragment {

    ViewGroup root;
    Spinner spin_kategori;
    ArrayAdapter<CharSequence> adapterKategori;
    String kategoriBuku;
    RecyclerView rc_listBuku;
    ArrayList<Buku_Model> listBuku;
    Buku_Adapter bukuAdapter;
    DatabaseReference dbBuku;
    ArrayList<String> arrayListKategori = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);

        getID();
        rc_listBuku.setHasFixedSize(true);
        rc_listBuku.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        listBuku = new ArrayList<>();
        bukuAdapter = new Buku_Adapter(getActivity(), listBuku);
        rc_listBuku.setAdapter(bukuAdapter);

        dbBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Buku");

        spinnerKategori();

        return root;
    }

    private void getID() {
        spin_kategori = root.findViewById(R.id.spin_kategori);
        rc_listBuku = root.findViewById(R.id.rc_listBuku);
    }

    private void spinnerKategori() {
        DatabaseReference dbKategori = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        dbKategori.child("Daftar Kategori").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListKategori.clear();
                arrayListKategori.add(0, "Semua Kategori");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    arrayListKategori.add(dataSnapshot.child("kategori").getValue(String.class));
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, arrayListKategori);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_kategori.setAdapter(arrayAdapter);
                spin_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spin_kategori = (Spinner) adapterView;
                        kategoriBuku = adapterView.getItemAtPosition(i).toString();
                        if (kategoriBuku.equals("Semua Kategori")) {
                            getAllDataBuku();
                        } else {
                            getBukuByCategory();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllDataBuku() {
        dbBuku.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBuku.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Buku_Model buku_model = dataSnapshot.getValue(Buku_Model.class);
                    listBuku.add(buku_model);
                }
                bukuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBukuByCategory() {
        dbBuku.orderByChild("kategori").equalTo(kategoriBuku).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBuku.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Buku_Model buku_model = dataSnapshot.getValue(Buku_Model.class);
                    listBuku.add(buku_model);
                }
                bukuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
