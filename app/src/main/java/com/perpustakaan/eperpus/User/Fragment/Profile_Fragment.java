package com.perpustakaan.eperpus.User.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Activity.Edit_Profil;
import com.perpustakaan.eperpus.User.Activity.Verifikasi_Email;

public class Profile_Fragment extends Fragment {

    ViewGroup root;
    TextView nama_user, npm_user;
    ImageView user_image;
    DatabaseReference dbUser;
    FirebaseAuth mUser;
    CardView cv_editProfil, cv_verifEmail;
    String UID, foto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_profile,container, false);



        dbUser = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        mUser = FirebaseAuth.getInstance();

        getID();

        FirebaseUser currentUser = mUser.getCurrentUser();
        UID = currentUser.getUid();

        getFoto(new CallbackFoto() {
            @Override
            public void onCallback(String URL) {
                Glide.with(getActivity()).load(URL).into(user_image);
            }
        });

        dbUser.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nama_user.setText(snapshot.child("nama").getValue().toString());
                npm_user.setText(snapshot.child("npm").getValue().toString());
                if (!snapshot.child("foto").equals("empty")){
                    Glide.with(getActivity()).load(snapshot.child("foto").getValue().toString()).into(user_image);
                } else if (snapshot.child("foto").equals("empty")){
                    user_image.setImageResource(R.drawable.ic_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cv_verifEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Verifikasi_Email.class));
            }
        });

        cv_editProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Edit_Profil.class));
            }
        });


        return root;

    }

    private void getID(){
        nama_user = root.findViewById(R.id.nama_user);
        npm_user = root.findViewById(R.id.npm_user);
        cv_editProfil = root.findViewById(R.id.cv_editProfil);
        cv_verifEmail = root.findViewById(R.id.cv_verifEmail);
        user_image = root.findViewById(R.id.user_image);
    }

    private void getFoto(CallbackFoto callbackFoto){
        dbUser.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foto = snapshot.child("foto").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface CallbackFoto{
        void onCallback(String URL);
    }


}
