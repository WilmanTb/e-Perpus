package com.perpustakaan.eperpus.User.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.perpustakaan.eperpus.Admin.Activity.Dashboard_Admin;
import com.perpustakaan.eperpus.Admin.Activity.Tambah_Buku;
import com.perpustakaan.eperpus.R;
import com.squareup.picasso.Picasso;

public class Edit_Profil extends AppCompatActivity {

    ImageView foto_user;
    Button btn_editFoto, btn_uploadFoto;
    DatabaseReference dbUsers;
    String urlFoto;
    FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference storageFoto;
    private StorageTask mUploadTask;
    int x = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        foto_user = findViewById(R.id.foto_user);
        btn_editFoto = findViewById(R.id.btn_editFoto);
        btn_uploadFoto = findViewById(R.id.btn_uploadFoto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbUsers = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UID = currentUser.getUid();
        getFoto();

        storageFoto = FirebaseStorage.getInstance().getReference();

        btn_editFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFoto(view);
            }
        });

        btn_uploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageUri != null) {
                    uploadFile(new CallbackFoto() {
                        @Override
                        public void onCallback(String URL) {
                            dbUsers.child(UID).child("foto").setValue(urlFoto);
                            Toast.makeText(Edit_Profil.this, "Foto berhasil di upload", Toast.LENGTH_SHORT).show();
                            mImageUri = null;
                        }
                    });
                }else {
                    Toast.makeText(Edit_Profil.this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFoto() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UID = currentUser.getUid();
        dbUsers.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("foto").getValue().toString().equals("empty")) {
                    foto_user.setImageResource(R.drawable.ic_profile);
                } else if (!snapshot.child("foto").getValue().toString().equals("empty")) {
                    Glide.with(Edit_Profil.this).load(snapshot.child("foto").getValue().toString()).into(foto_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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

            Picasso.get().load(mImageUri).into(foto_user);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

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
                            Toast.makeText(Edit_Profil.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            urlFoto = "Tidak ada";
            callbackFoto.onCallback(urlFoto);
        }
    }

    public interface CallbackFoto{
        void onCallback(String URL);
    }
}