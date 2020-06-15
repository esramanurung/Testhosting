package com.example.projectmppl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.projectmppl.R;
import com.example.projectmppl.adapter.ListNonOrganikAdapter;
import com.example.projectmppl.fragment.KantongFragment;
import com.example.projectmppl.fragment.metode.MetodeAntarFragment;
import com.example.projectmppl.fragment.metode.MetodeJemputFragment;
import com.example.projectmppl.model.Kantong;
import com.example.projectmppl.model.KantongNonOrganik;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.ButterKnife;

@SuppressWarnings("UnusedAssignment")
public class KantongActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private KantongFragment kantongFragment;
    private final MetodeAntarFragment metodeAntarFragment = new MetodeAntarFragment();
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ListNonOrganikAdapter listNonOrganikAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kantong);
        ButterKnife.bind(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        kantongFragment = new KantongFragment();
        loadKantongFragment(new KantongFragment());
        loadFragment(metodeAntarFragment);
        listNonOrganikAdapter = new ListNonOrganikAdapter();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String receive = getIntent().getStringExtra("saveData");
        String batal = getIntent().getStringExtra("removeData");
        String key = getIntent().getStringExtra("key");
        String jenis = getIntent().getStringExtra("jenis");

        if (Objects.requireNonNull(receive).equals("removeData")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Permintaan");
            alertDialogBuilder.setMessage("Sampah Anda sedang diproses. Terimakasih");
            alertDialogBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                removeData();
                Intent intent = new Intent(KantongActivity.this, MainActivity.class);
                startActivity(intent);


            });
            alertDialogBuilder.show();
        }

        if (batal.equals("removeData")){
            batalData(key,jenis);
        }
    }

    private void removeData(){
        firebaseDatabase
                .getReference()
                .child("kantong")
                .child(firebaseAuth.getCurrentUser().getEmail().replaceAll("\\.", "_"))
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void batalData(String key,String jenis){
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("kantong");
        databaseReference2.child(currentUser)
                .child("data")
                .child(jenis)
                .child(key)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }





    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragmentAntar = new MetodeAntarFragment();
        Fragment fragmentJemput= new MetodeJemputFragment();
        switch (item.getItemId()) {
            case R.id.action_antar:
                fragmentAntar = new MetodeAntarFragment();
                return loadFragment(fragmentAntar);
            case R.id.action_jemput:
                fragmentJemput = new MetodeJemputFragment();
                return loadFragment(fragmentJemput);

        }

        return true;

    }

    // method untuk load fragment yang sesuai
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout_content_dashboard, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void loadKantongFragment(Fragment fragment){
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString("removeData", "remove");
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_content_kantong, fragment,"MY_FRAGMENT")
                    .commit();
        }
    }

}
