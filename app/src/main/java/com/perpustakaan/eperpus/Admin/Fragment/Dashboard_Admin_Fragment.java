package com.perpustakaan.eperpus.Admin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.Admin.Activity.Tambah_Anggota;
import com.perpustakaan.eperpus.Admin.Activity.Tambah_Buku;
import com.perpustakaan.eperpus.Admin.Activity.Tambah_Kategori;
import com.perpustakaan.eperpus.Admin.Adapter.Permintaan_Adapter;
import com.perpustakaan.eperpus.Admin.Class.Anggota_Model;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Buku_Model;
import com.perpustakaan.eperpus.User.Class.Peminjaman_Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import papaya.in.sendmail.SendMail;

public class Dashboard_Admin_Fragment extends Fragment {

    ViewGroup root;
    Permintaan_Adapter adapterPemintaan;
    ArrayList<Peminjaman_Model> listPermintaan;
    RecyclerView rc_listPermintaan;
    FirebaseAuth userAuth;
    DatabaseReference dbPermintaan;
    String uidUser = "empty", npmUser = "empty", namaUser = "empty";
    Peminjaman_Model peminjamanModel = new Peminjaman_Model();
    TextView txt_lihatSemua;
    CardView cv_tambahBuku, cv_tambahAnggota, cv_tambahKategori;
    TextView total_pinjam, total_pengembalian, total_stokBuku;
    String tanggalPinjam, tanggalKembali, Email, Key;
    Date x, y, lastSendEmail;
    long diff, Denda;
    int totalStok = 0;
    String stok;
    int i;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard_admin, container, false);

        getID();
        rc_listPermintaan.setHasFixedSize(true);
        rc_listPermintaan.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, true));
        listPermintaan = new ArrayList<>();
        adapterPemintaan = new Permintaan_Adapter(getActivity(), listPermintaan);
        rc_listPermintaan.setAdapter(adapterPemintaan);
        userAuth = FirebaseAuth.getInstance();

        dbPermintaan = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        getData();
        getCurrentDate();
        test();


        txt_lihatSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment someFragment = new Daftar_Tunggu();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, someFragment); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();

            }
        });

        cv_tambahBuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Tambah_Buku.class));
            }
        });

        cv_tambahAnggota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Tambah_Anggota.class));
            }
        });

        cv_tambahKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Tambah_Kategori.class));
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        getTotalPinjam();
        getTotalPengembalian();
        getTotalStok();
    }

    private void getID() {
        rc_listPermintaan = root.findViewById(R.id.rc_listPermintaan);
        txt_lihatSemua = root.findViewById(R.id.txt_lihatSemua);
        cv_tambahAnggota = root.findViewById(R.id.cv_tambahAnggota);
        cv_tambahBuku = root.findViewById(R.id.cv_tambahBuku);
        cv_tambahKategori = root.findViewById(R.id.cv_tambahKategori);
        total_pengembalian = root.findViewById(R.id.total_pengembalian);
        total_pinjam = root.findViewById(R.id.total_pinjam);
        total_stokBuku = root.findViewById(R.id.total_stokBuku);
    }

    private void getData() {
        dbPermintaan.child("Peminjaman").orderByChild("status").equalTo("Menunggu dikonfirmasi admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPermintaan.clear();
                i = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        peminjamanModel = dataSnapshot.getValue(Peminjaman_Model.class);
                        listPermintaan.add(peminjamanModel);
                        dbPermintaan.child("Users").child(peminjamanModel.getPeminjam()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    namaUser = snapshot.child("nama").getValue().toString();
                                    npmUser = snapshot.child("npm").getValue().toString();
                                    listPermintaan.get(i).setPeminjam(namaUser);
                                    i += 1;
                                    peminjamanModel.setPeminjam(namaUser);
                                    adapterPemintaan.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    adapterPemintaan.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalPinjam() {
        dbPermintaan.child("Peminjaman").orderByChild("status").equalTo("Telah dikonfirmasi admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    total_pinjam.setText(String.valueOf(snapshot.getChildrenCount()));
                } else {
                    total_pinjam.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalPengembalian() {
        dbPermintaan.child("Peminjaman").orderByChild("status").equalTo("Telah dikembalikan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    total_pengembalian.setText(String.valueOf(snapshot.getChildrenCount()));
                } else {
                    total_pengembalian.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalStok() {
        dbPermintaan.child("Buku").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Buku_Model bukuModel = dataSnapshot.getValue(Buku_Model.class);
                    totalStok = totalStok + Integer.parseInt(bukuModel.getStokBuku());
                    total_stokBuku.setText(String.valueOf(totalStok));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        tanggalPinjam = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 7);
        tanggalKembali = sdf.format(calendar.getTime());
    }

    private void test() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dbPermintaan.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    lastSendEmail = sdf.parse(snapshot.child("lastSendEmail").getValue().toString());
                    y = sdf.parse(tanggalPinjam); //current date
                    long o = (y.getTime() - lastSendEmail.getTime()) / 86400000;
                    if (o == 1) {
                        dbPermintaan.child("Admin").child("sendEmailDaily").setValue(false);
                        // get data pengembalian buku
                        dbPermintaan.child("Peminjaman").orderByChild("status").equalTo("Telah dikonfirmasi admin").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    try {
                                        //get tanggal kembali & current date
                                        x = sdf.parse(dataSnapshot.child("tanggalKembali").getValue().toString()); // tanggal kembali
                                        diff = (x.getTime() - y.getTime()) / 86400000;
                                        Toast.makeText(getActivity(), String.valueOf(diff), Toast.LENGTH_SHORT).show();
                                        // check selisih hari pengembalian
                                        if (diff == 0 || diff == 1) {
                                            // get UID peminjam
                                            String UID = dataSnapshot.child("peminjam").getValue().toString();
                                            //get email peminjam
                                            dbPermintaan.child("Users").child(UID).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Email = snapshot.getValue().toString();
                                                    // send email ke peminjam
                                                    /*SendMail mail = new SendMail("rizki01tebe@gmail.com", "wlbvfhpnbyectbpw",
                                                            Email,
                                                            "Batas Peminjaman Buku",
                                                            "Batas peminjaman buku anda adalah" + " " + x +
                                                                    "\nMohon mengembalikan buku sebelum tanggal" + " " + x);
                                                    mail.execute();*/
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        } else if (diff < 0) {
                                            String key = dataSnapshot.getKey();
                                            String UID = dataSnapshot.child("peminjam").getValue().toString();
                                            Denda = Math.abs(diff * 500);
                                            String denda = "Rp " + Denda;
                                            dbPermintaan.child("Peminjaman").child(key).child("denda").setValue(denda);

                                            //get email peminjam
                                            dbPermintaan.child("Users").child(UID).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Email = snapshot.getValue().toString();
                                                    // send email ke peminjam
                                                    SendMail mail = new SendMail("rizki01tebe@gmail.com", "wlbvfhpnbyectbpw",
                                                            Email,
                                                            "Batas Peminjaman Buku",
                                                            "Batas peminjaman buku anda adalah" + " " + x +
                                                                    "\nMohon mengembalikan buku sebelum tanggal" + " " + x);
                                                    mail.execute();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        } else {
                                            continue;
                                        }
                                        Key = dataSnapshot.getKey();
                                        dbPermintaan.child("Peminjaman").child(Key).child("denda").setValue(String.valueOf(Denda));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                dbPermintaan.child("Admin").child("sendEmailDaily").setValue(true);
                                dbPermintaan.child("Admin").child("lastSendEmail").setValue(tanggalPinjam);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
