package com.example.projectmppl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.projectmppl.R;
import com.example.projectmppl.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.input_name)
    EditText editTextName;
    @BindView(R.id.input_email)
    EditText editTextEmail;
    @BindView(R.id.input_noHp)
    EditText editTextNoHP;
    @BindView(R.id.input_pekerjaan)
    Spinner spinnerPekerjaan;
    @BindView(R.id.radio_genre)
    RadioGroup radioGroup;

    @BindView(R.id.input_sandi)
    EditText editTextPassword;
    @BindView(R.id.input_konfir_password)
    EditText editTextConfirmPassword;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    private RadioButton radioButton;

    FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference getReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initFirebase();
        getReference = FirebaseDatabase.getInstance().getReference("penukarSampah");
        hideProgress();
        spinnerPekerjaan.setPrompt("Pekerjaan");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick({R.id.btn_register})
    public void onClick(Button button) {
        switch (button.getId()) {
            case R.id.btn_register:
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String noHP = editTextNoHP.getText().toString().trim();
                String pekerjaan = spinnerPekerjaan.getSelectedItem().toString().trim();
                int jenisKelamin = radioGroup.getCheckedRadioButtonId();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                radioButton = findViewById(jenisKelamin);

                String genre = (String) radioButton.getText();
                signup(name, email, noHP, pekerjaan, genre, password, confirmPassword);
                break;
        }
    }

    private void signup(String name, String email, String noHP, String pekerjaan, String jenisKelamin, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_message_name_empty, Snackbar.LENGTH_LONG)
                    .show();
        } else if (TextUtils.isEmpty(email)) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_message_username_empty, Snackbar.LENGTH_LONG)
                    .show();
        }  else if (TextUtils.isEmpty(noHP)) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_message_noHP_empty, Snackbar.LENGTH_LONG)
                    .show();
        } else if (TextUtils.isEmpty(pekerjaan)|| pekerjaan.equals("Pekerjaan")) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_message_pekerjaan_empty, Snackbar.LENGTH_LONG)
                    .show();
        } else if (TextUtils.isEmpty(password)) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_message_password_empty, Snackbar.LENGTH_LONG)
                    .show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_message_confirmPassword_empty, Snackbar.LENGTH_LONG)
                    .show();
        } else if (!password.equals(confirmPassword)) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_message_passwordNotEqual_empty, Snackbar.LENGTH_LONG)
                    .show();
        }
        else {
            showProgress();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideProgress();
                            if (task.isSuccessful()) {
                                //  signup success
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                alertDialogBuilder.setTitle("Daftar");
                                alertDialogBuilder.setMessage("Akun Anda berhasil di daftar. Silahkan masuk menggunakan akun Anda");

                                // Save new User
                                addUSer(new User(name,email,noHP,pekerjaan,jenisKelamin,password, "",0,0));
                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                });
                                alertDialogBuilder.show();
                            } else {
                                task.getException().printStackTrace();
                                //  signup fail
                                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Proses pendaftaran gagal, silahkan coba lagi", Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snackbar.dismiss();
                                    }
                                });
                                snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                                snackbar.show();
                            }
                        }
                    });
        }
    }

    public void addUSer(User user){
        getReference
                .child(user.getEmail()
                        .replaceAll("\\.", "_"))
                .setValue(user);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }
}
