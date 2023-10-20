package com.perpustakaan.eperpus.Admin.Activity;

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
import com.perpustakaan.eperpus.Admin.Fragment.Daftar_Tunggu;
import com.perpustakaan.eperpus.Admin.Fragment.Dashboard_Admin_Fragment;
import com.perpustakaan.eperpus.Admin.Fragment.Data_Anggota;
import com.perpustakaan.eperpus.Admin.Fragment.Data_Buku;
import com.perpustakaan.eperpus.Admin.Fragment.Data_Peminjaman;
import com.perpustakaan.eperpus.Admin.Fragment.Data_Pengembalian;
import com.perpustakaan.eperpus.Admin.Fragment.Kategori;
import com.perpustakaan.eperpus.Admin.Fragment.Permintaan_Pengembalian;
import com.perpustakaan.eperpus.Home.Login_Activity;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Fragment.Dashboard_Fragment;

public class Dashboard_Admin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.nama_user);
        textView.setText("Admin");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Dashboard_Admin_Fragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
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

        switch (item.getItemId()) {

            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Dashboard_Admin_Fragment()).commit();
                break;

            case R.id.nav_dataBuku:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Data_Buku()).commit();
                break;

            case R.id.nav_kategori:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Kategori()).commit();
                break;

            case R.id.nav_dataAnggota:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Data_Anggota()).commit();
                break;

            case R.id.nav_peminjaman:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Data_Peminjaman()).commit();
                break;

            case R.id.nav_pengembalian:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Data_Pengembalian()).commit();
                break;

            case R.id.nav_tunggu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Daftar_Tunggu()).commit();
                break;

            case R.id.nav_kembali:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Permintaan_Pengembalian()).commit();
                break;

            case R.id.nav_logout:
                startActivity(new Intent(Dashboard_Admin.this, Login_Activity.class));
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}