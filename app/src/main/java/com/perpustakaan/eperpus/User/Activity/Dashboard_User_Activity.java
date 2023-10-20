package com.perpustakaan.eperpus.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.Admin.Activity.Dashboard_Admin;
import com.perpustakaan.eperpus.Home.Login_Activity;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Fragment.Dashboard_Fragment;
import com.perpustakaan.eperpus.User.Fragment.Keranjang_Pinjam;
import com.perpustakaan.eperpus.User.Fragment.Peminjaman_Fragment;
import com.perpustakaan.eperpus.User.Fragment.Pengembalian_Fragment;
import com.perpustakaan.eperpus.User.Fragment.Profile_Fragment;
import com.perpustakaan.eperpus.User.Fragment.Tunggu_Fragment;

public class Dashboard_User_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private String namaUser, npmUser;
    private DatabaseReference dbUser;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);

        Dashboard_Admin.i = 1;

        mAuth = FirebaseAuth.getInstance();

        dbUser = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.nama_user);
        textView.setText(namaUser);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Dashboard_Fragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userData();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Dashboard_Fragment()).commit();
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Profile_Fragment()).commit();
                break;

            case R.id.nav_peminjaman:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Peminjaman_Fragment()).commit();
                break;

            case R.id.nav_pengembalian:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Pengembalian_Fragment()).commit();
                break;

            case R.id.nav_tunggu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Tunggu_Fragment()).commit();
                break;

            case R.id.nav_keranjangPinjam:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Keranjang_Pinjam()).commit();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                startActivity(new Intent(Dashboard_User_Activity.this, Login_Activity.class));
                finish();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void userData(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UID = currentUser.getUid();
        dbUser.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    npmUser = snapshot.child("npm").getValue().toString();
                    namaUser = snapshot.child("nama").getValue().toString();
                    drawerLayout = findViewById(R.id.drawer_layout);
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(Dashboard_User_Activity.this);
                    View headerView = navigationView.getHeaderView(0);
                    TextView textView = (TextView) headerView.findViewById(R.id.nama_user);
                    TextView textView2 = (TextView) headerView.findViewById(R.id.npm_user);
                    textView.setText(namaUser);
                    textView2.setText(npmUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}