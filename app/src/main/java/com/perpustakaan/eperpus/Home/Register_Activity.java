package com.perpustakaan.eperpus.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perpustakaan.eperpus.R;
import com.perpustakaan.eperpus.User.Activity.Dashboard_User_Activity;

import java.util.Calendar;
import java.util.HashMap;

public class Register_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText et_NPM, et_Nama, et_Email, et_noHp, et_Alamat, et_Password, et_confirmPassword;
    private Button btn_Daftar, datePickerButton;
    private Spinner spin_jk;
    private String NPM = "", Nama = "", Email = "", Alamat= "", NoHp = "", Password = "", ConfirmPassword = "", TanggalLahir = "", JenisKelamin ="" ,UID;
    private ImageView btn_back;
    private TextView txt_login;
    private DatabaseReference dbRegister;
    private FirebaseAuth userAuth;
    private DatePickerDialog datePicker;
    private ArrayAdapter<CharSequence> adapterJenisKelamin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getId();
        initDatePicker();
        getJenisKelamin();

        dbRegister = FirebaseDatabase
                .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        btn_Daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                checkEmpty();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                startActivity(new Intent(Register_Activity.this, LandingPage_Activity.class));
                finish();
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimation(view);
                startActivity(new Intent(Register_Activity.this, Login_Activity.class));
                finish();
            }
        });
    }

    private void getId() {
        et_NPM = findViewById(R.id.et_NPM);
        et_Nama = findViewById(R.id.et_Nama);
        et_Alamat = findViewById(R.id.et_Alamat);
        et_Email = findViewById(R.id.et_email);
        et_Password = findViewById(R.id.et_Password);
        et_noHp = findViewById(R.id.et_noHp);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        datePickerButton = findViewById(R.id.datePickerButton);
        spin_jk = findViewById(R.id.spin_jk);
        btn_back = findViewById(R.id.btn_back);
        btn_Daftar = findViewById(R.id.btn_Daftar);
        txt_login = findViewById(R.id.txt_login);

    }

    private void getString(){
        NPM = et_NPM.getText().toString();
        Nama = et_Nama.getText().toString();
        Alamat = et_Alamat.getText().toString();
        Email = et_Email.getText().toString();
        NoHp = et_noHp.getText().toString();
        Password = et_Password.getText().toString();
        ConfirmPassword = et_confirmPassword.getText().toString();

    }

    private void checkEmpty() {
        getString();
        if (NPM.isEmpty() || Nama.isEmpty() || Alamat.isEmpty() || Email.isEmpty() || NoHp.isEmpty() || Password.isEmpty() || ConfirmPassword.isEmpty()){
            Toast.makeText(this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else{
            checkPassword();
        }
    }

    private void checkPassword() {
        if (Password.length() < 8){
            Toast.makeText(this, "Panjang password minimal 8 karakter", Toast.LENGTH_SHORT).show();
        } else if (!ConfirmPassword.equals(Password)){
            Toast.makeText(this, "Password yang anda masukkan tidak sesuai", Toast.LENGTH_SHORT).show();
        } else{
            registerUser(NPM, Nama, Alamat, Email, NoHp, Password, TanggalLahir, JenisKelamin);
        }
    }

    private void registerUser(String npm, String nama, String alamat, String email, String noHp, String password, String tanggalLahir, String jenisKelamin) {
        userAuth = FirebaseAuth.getInstance();
        userAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    UID = currentUser.getUid();
                    DatabaseReference dbRegister = FirebaseDatabase
                            .getInstance("https://eperpus-2fe42-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference().child("Users").child(UID);
                    HashMap hashMap = new HashMap<>();
                    hashMap.put("npm", npm);
                    hashMap.put("nama", nama);
                    hashMap.put("alamat", alamat);
                    hashMap.put("email", email);
                    hashMap.put("noHp", noHp);
                    hashMap.put("password", password);
                    hashMap.put("tanggalLahir", tanggalLahir);
                    hashMap.put("jenisKelamin", jenisKelamin);
                    hashMap.put("foto", "empty");
                    dbRegister.setValue(hashMap);
                    Toast.makeText(Register_Activity.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register_Activity.this, Dashboard_User_Activity.class));
                    finish();
                }else {
                    Toast.makeText(Register_Activity.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                datePickerButton.setText("TESTING");
                month = month + 1;
                TanggalLahir = makeDateString(day, month, year);
                datePickerButton.setText(TanggalLahir);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePicker = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void onCLick(View view) {
        datePicker.show();
    }

    private void clickAnimation(View view){
        Context context = this;
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_click));
    }

    private void getJenisKelamin(){
        adapterJenisKelamin = ArrayAdapter.createFromResource(this, R.array.JenisKelamin, android.R.layout.simple_spinner_item);
        adapterJenisKelamin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_jk.setAdapter(adapterJenisKelamin);
        spin_jk.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spin_jk =(Spinner) adapterView;
        JenisKelamin = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}