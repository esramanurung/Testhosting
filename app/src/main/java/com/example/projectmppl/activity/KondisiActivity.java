package com.example.projectmppl.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmppl.R;
import com.example.projectmppl.model.Kantong;
import com.example.projectmppl.ui.ViewModelFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_SHORT;

public class KondisiActivity extends AppCompatActivity implements View.OnClickListener{

    private View view;
    @BindView(R.id.edt_bagus)
    EditText editBagus;
    @BindView(R.id.edt_berat)
    EditText editBerat;
    @BindView(R.id.edt_ringan)
    EditText editRingan;
    @BindView(R.id.kantong)
    Button kantong;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.kantong_activity)
    ConstraintLayout constraintLayout;
    @BindView(R.id.namabarang)
    TextView namaBarang;
    @BindView(R.id.img_sampah)
    ImageView imageSampah;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    int jumlahBagus, jumlahRingan, jumlahBerat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kondisi);
        databaseReference = FirebaseDatabase.getInstance().getReference("kantong");

        ButterKnife.bind(this);
        initFirebase();
        kantong.setOnClickListener(this);
        hideProgress();

        String namaSampah = getIntent().getStringExtra("NamaSampah");
        if (namaSampah.equals("PRT")){
            namaBarang.setText("Perlengkapan Rumah Tangga");
        }else{
            namaBarang.setText(namaSampah);
        }

        showSampah();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (TextUtils.isEmpty(editBagus.getText())){
            editBagus.setText("0");
        }else if (TextUtils.isEmpty(editRingan.getText())){
            jumlahRingan = 0;
        }else if (TextUtils.isEmpty(editBerat.getText())){
            jumlahBerat = 0;
        }
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.kantong) {
            showProgress();
            ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
            LiveData<DataSnapshot> liveData = viewModel.getdataKondisi();
            // Mengambil data yang diberikan oleh pengguna
            String username = firebaseAuth.getCurrentUser().getEmail();
            if (TextUtils.isEmpty(editBagus.getText())){
                editBagus.setText("0");
            }else if (TextUtils.isEmpty(editRingan.getText())){
                editRingan.setText("0");
            }else if (TextUtils.isEmpty(editBerat.getText())){
                editBerat.setText("0");
            }else {
                jumlahBagus = Integer.parseInt(String.valueOf(editBagus.getText()));
                jumlahRingan = Integer.parseInt(String.valueOf(editRingan.getText()));
                jumlahBerat = Integer.parseInt(String.valueOf(editBerat.getText()));
            }
            liveData.observe(this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    // Untuk mengambil idSampah yang diberikan oleh pengguna

                    String idSampah = dataSnapshot
                            .child(getIntent().getStringExtra("JenisSampah"))
                            .child(getIntent().getStringExtra("NamaSampah"))
                            .child("nama")
                            .getValue().toString();

                    String imageUrl = dataSnapshot
                            .child(getIntent().getStringExtra("JenisSampah"))
                            .child(getIntent().getStringExtra("NamaSampah"))
                            .child("gambar")
                            .getValue().toString();

                    String jenisSampah = dataSnapshot
                            .child(getIntent().getStringExtra("JenisSampah"))
                            .child(getIntent().getStringExtra("NamaSampah"))
                            .child("jenis")
                            .getValue().toString();


                    // Menampilkan gambar sampah
                    Picasso.get().load(imageUrl).into(imageSampah);

                    //
                    insertToKantong(idSampah, username, jumlahBerat, jumlahRingan, jumlahBagus);

                }
            });
        }
    }

    private void insertToKantong(String idSampah, String idPengguna, int sampahBerat, int sampahRingan, int sampahBagus){

        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataKondisi();

        liveData.observe(this, dataSnapshot -> {
            int berat = Integer.parseInt(dataSnapshot.child(getIntent().getStringExtra("JenisSampah")).child(getIntent().getStringExtra("NamaSampah")).child("berat").getValue().toString());
            int ringan = Integer.parseInt(dataSnapshot.child(getIntent().getStringExtra("JenisSampah")).child(getIntent().getStringExtra("NamaSampah")).child("ringan").getValue().toString());
            int bagus = Integer.parseInt(dataSnapshot.child(getIntent().getStringExtra("JenisSampah")).child(getIntent().getStringExtra("NamaSampah")).child("bagus").getValue().toString());

            int jumlah = sampahBagus+sampahBerat+sampahRingan;
            int totalPoint = berat*sampahBerat + ringan * sampahRingan + bagus*sampahBagus;

            if (totalPoint!=0){
                String namaSampah = getIntent().getStringExtra("JenisSampah");
                String kategoriSampah = getIntent().getStringExtra("KategoriSampah");

                Kantong kantong = new Kantong( idSampah, jumlah, totalPoint,namaSampah,sampahBagus,sampahRingan,sampahBerat);
                databaseReference.child("kantong")
                        .child(idPengguna.replaceAll("\\.", "_"))
                        .child("data")
                        .child(kategoriSampah)
                        .push()
                        .setValue(kantong);
                hideProgress();
                clearFields();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(KondisiActivity.this);
                alertDialogBuilder.setTitle("Permintaan");
                alertDialogBuilder.setMessage("Sampah Anda telah dimasukkan kedalam Kantong");

                alertDialogBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();

                });
                alertDialogBuilder.show();
            }else {
                Toast.makeText(KondisiActivity.this,"Silahkan isi jumlah yang diinginkan",LENGTH_SHORT).show();
                hideProgress();
            }

        });

    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        editBagus.setEnabled(true);
        editBerat.setEnabled(true);
        editRingan.setEnabled(true);

    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        editBagus.setEnabled(false);
        editBerat.setEnabled(false);
        editRingan.setEnabled(false);

    }

    private void clearFields() {
        editBagus.setText("0");
        editBerat.setText("0");
        editRingan.setText("0");
    }

    private void showSampah(){
        ActionBar actionBar = getSupportActionBar();
        showProgress();
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataKondisi();
        liveData.observe(this, dataSnapshot -> {
            // Untuk mengambil idSampah yang diberikan oleh pengguna

            String imageUrl =dataSnapshot
                    .child(getIntent().getStringExtra("JenisSampah"))
                    .child(getIntent().getStringExtra("NamaSampah"))
                    .child("gambar")
                    .getValue().toString();

            String title =dataSnapshot
                    .child(getIntent().getStringExtra("JenisSampah"))
                    .child(getIntent().getStringExtra("NamaSampah"))
                    .child("jenis")
                    .getValue().toString();
            if (title.equals("PRT")){
                actionBar.setTitle("Perlengkapan Rumah Tangga");
            }
            else if (title.equals("komputer")){
                actionBar.setTitle(title.replace("k","K"));
            }else {
                actionBar.setTitle(title);
            }

            Picasso.get().load(imageUrl).into(imageSampah);
            hideProgress();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
