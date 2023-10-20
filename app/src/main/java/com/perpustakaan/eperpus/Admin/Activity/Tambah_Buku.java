package com.perpustakaan.eperpus.Admin.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Class.Buku_Model;
import com.squareup.picasso.Picasso;

public class Tambah_Buku extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText et_kodeBuku, et_judulBuku, et_penertbitBuku, et_tahunTerbit, et_pengarangBuku, et_bahasaBuku, et_halamanBuku, et_stokBuku;
    private Spinner spin_kategori;
    private Button btn_submit;
    private RelativeLayout rlFoto;
    private TextView tv_pilihFoto;
    private DatabaseReference dbBuku;
    private StorageReference storageFoto;
    private StorageTask mUploadTask;
    private Uri mImageUri;
    private String KodeBuku, JudulBuku, PenerbitBuku, TahunTerbit, PengarangBuku, BahasaBuku, HalamanBuku, StokBuku, KategoriBuku, urlFoto;
    private ArrayAdapter<CharSequence> adapterKategori;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_buku);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dbBuku = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        storageFoto = FirebaseStorage.getInstance().getReference();

        getID();
        spinnerKategori();

        rlFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFoto(view);
            }
        });
//<------TOMBOL TAMBAH BUKU------->

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationClick(view);
                AlertDialog alertDialog = new AlertDialog.Builder(Tambah_Buku.this)
                        .setMessage("Yakin menambah buku ini ?")
                        .setPositiveButton("Ya", null)
                        .setNegativeButton("Tidak", null)
                        .show();

                Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkEmpty();
                        startActivity(new Intent(Tambah_Buku.this, Dashboard_Admin.class));
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void getID() {
        et_kodeBuku = findViewById(R.id.et_kodeBuku);
        et_judulBuku = findViewById(R.id.et_judulBuku);
        et_penertbitBuku = findViewById(R.id.et_penerbitBuku);
        et_tahunTerbit = findViewById(R.id.et_tahunTerbit);
        et_pengarangBuku = findViewById(R.id.et_pengarangBuku);
        et_bahasaBuku = findViewById(R.id.et_bahasaBuku);
        et_halamanBuku = findViewById(R.id.et_halamanBuku);
        et_stokBuku = findViewById(R.id.et_stokBuku);
        spin_kategori = findViewById(R.id.spin_kategori);
        btn_submit = findViewById(R.id.btn_submit);
        rlFoto = findViewById(R.id.rlfoto);
        tv_pilihFoto = findViewById(R.id.tv_pilihFoto);
    }

    private void getText() {
        KodeBuku = et_kodeBuku.getText().toString();
        JudulBuku = et_judulBuku.getText().toString();
        PenerbitBuku = et_penertbitBuku.getText().toString();
        TahunTerbit = et_tahunTerbit.getText().toString();
        PengarangBuku = et_pengarangBuku.getText().toString();
        BahasaBuku = et_bahasaBuku.getText().toString();
        HalamanBuku = et_halamanBuku.getText().toString();
        StokBuku = et_stokBuku.getText().toString();
    }

    private void checkEmpty() {
        getText();
        if (KodeBuku.isEmpty() || JudulBuku.isEmpty() || PenerbitBuku.isEmpty() || TahunTerbit.isEmpty() ||
                PengarangBuku.isEmpty() || BahasaBuku.isEmpty() || HalamanBuku.isEmpty() || StokBuku.isEmpty()) {
            Toast.makeText(this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else {
            inputData();
        }
    }

    //<------FUNGSI INPUT BUKU------->

    private void inputData() {
        uploadFile(new CallbackFoto() {
            @Override
            public void onCallback(String URL) {
                Buku_Model bukuModel = new Buku_Model(JudulBuku, StokBuku, PengarangBuku, urlFoto, BahasaBuku, KodeBuku, TahunTerbit, PenerbitBuku, KategoriBuku, HalamanBuku);
                dbBuku.child("Buku").child(KodeBuku).setValue(bukuModel);
                Toast.makeText(Tambah_Buku.this, "Buku berhasil ditambah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void spinnerKategori() {
        adapterKategori = ArrayAdapter.createFromResource(this, R.array.KategoriBuku, android.R.layout.simple_spinner_item);
        adapterKategori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_kategori.setAdapter(adapterKategori);
        spin_kategori.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spin_kategori = (Spinner) adapterView;
        KategoriBuku = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
// MEMBUKA FILE FOTO

    public void uploadFoto(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri);
            tv_pilihFoto.setText(String.valueOf(mImageUri));

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

// UPLOAD FILE FOTO

    private void uploadFile(CallbackFoto callbackFoto) {
        if (mImageUri != null) {
            StorageReference fileReference = storageFoto.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlFoto = uri.toString();
                                    callbackFoto.onCallback(urlFoto);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Tambah_Buku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            urlFoto = "Tidak ada";
            callbackFoto.onCallback(urlFoto);
        }
    }

    private void animationClick(View view){
        Context context = this;
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_click));
    }

    public interface CallbackFoto{
        void onCallback(String URL);
    }
}