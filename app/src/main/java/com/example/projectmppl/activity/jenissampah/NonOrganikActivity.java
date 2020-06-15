package com.example.projectmppl.activity.jenissampah;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projectmppl.R;
import com.example.projectmppl.model.KantongNonOrganik;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NonOrganikActivity extends AppCompatActivity {
    private CheckBox chkPlastik,chkKertas,chkBotol,chkBesi,chkAluminium;

    @BindView(R.id.btn_request)
    Button btnRequest;
    @BindView(R.id.totalberat)
    EditText totalBerat;
    private DatabaseReference getReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_organik);
        ButterKnife.bind(this);

        initView();

        btnRequest.setOnClickListener(view -> {
            String totalSampah = totalBerat.getText().toString().trim();
            addKantong(totalSampah);
        });

        getReference = FirebaseDatabase.getInstance().getReference("kantong");


    }

    private void initView(){
        chkPlastik = findViewById(R.id.plastik);
        chkKertas= findViewById(R.id.kertas);
        chkBotol = findViewById(R.id.botol);
        chkAluminium = findViewById(R.id.aluminium);
        chkBesi = findViewById(R.id.besi);
    }

    private void addKantong(String total){
        ArrayList<String> list = getListSampah();
         if (TextUtils.isEmpty(total)){
            Toast.makeText(NonOrganikActivity.this,"Silahkan masukkan berat Sampah",Toast.LENGTH_SHORT).show();
        }else if (String.valueOf(list.size()).equals("0")){
            Toast.makeText(NonOrganikActivity.this,"Silahkan masukkan sampah",Toast.LENGTH_SHORT).show();
        }else if (list ==null && TextUtils.isEmpty(total)){
            Toast.makeText(NonOrganikActivity.this,"Silahkan isi Data",Toast.LENGTH_SHORT).show();
        }
        else{
            int totalSampah = Integer.parseInt(total);
            int jumlahPoint = totalSampah * 100;
            String currentUser = Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()).replaceAll("\\.", "_");
            KantongNonOrganik kantongNonOrganik = new KantongNonOrganik( String.valueOf(list), "NonOrganik", totalSampah, jumlahPoint);
            getReference
                    .child(currentUser)
                    .child("data")
                    .child("nonOrganik")
                    .push()
                    .setValue(kantongNonOrganik);
            chkAluminium.setChecked(false);
            chkPlastik.setChecked(false);
            chkKertas.setChecked(false);
            chkBotol.setChecked(false);
            chkBesi.setChecked(false);


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NonOrganikActivity.this);
            alertDialogBuilder.setTitle("Permintaan");
            alertDialogBuilder.setMessage("Sampah Anda telah dimasukkan kedalam Kantong");

            alertDialogBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                totalBerat.setText("0");
                finish();

            });
            alertDialogBuilder.show();
        }
    }

    private ArrayList<String>getListSampah(){
        ArrayList<String> listSampah = new ArrayList<>();
        if (chkPlastik.isChecked()){
            listSampah.add(chkPlastik.getText().toString());
        }if (chkKertas.isChecked()){
            listSampah.add(chkKertas.getText().toString());
        }if (chkBotol.isChecked()){
            listSampah.add(chkBotol.getText().toString());
        }if (chkBesi.isChecked()){
            listSampah.add(chkBesi.getText().toString());
        }if (chkAluminium.isChecked()){
            listSampah.add(chkAluminium.getText().toString());
        }
        return listSampah;
    }
}
