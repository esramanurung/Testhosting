package com.example.projectmppl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.projectmppl.fragment.HomeFragment;
import com.example.projectmppl.R;
import com.example.projectmppl.fragment.ProfileFragment;
import com.example.projectmppl.fragment.RiwayatFragment;
import com.example.projectmppl.fragment.TentangFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private String key = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inisialisasi BottomNavigaionView
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        key = getIntent().getStringExtra("keyTransaksi");
        if (key != null){
            removeRiwayat(key);
        }

        ProfileFragment myFragment = new ProfileFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout_content_dashboard, new HomeFragment(), HomeFragment.class.getSimpleName())
                .commit();

    }

    private void removeRiwayat(String key){
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("transaksipenukaransampah");
        databaseReference2.child(currentUser)
                .child(key)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {

            case R.id.action_beranda:
                fragment = new HomeFragment();
                break;
            case R.id.action_riwayat:
                fragment = new RiwayatFragment();
                break;
            case R.id.action_profil:
                fragment = new ProfileFragment();

                break;
            case R.id.action_tentang:
                fragment = new TentangFragment();
                break;
        }

        return loadFragment(fragment);
    }

    // method untuk load fragment yang sesuai
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout_content_dashboard, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}

