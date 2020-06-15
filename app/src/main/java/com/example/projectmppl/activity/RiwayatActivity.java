package com.example.projectmppl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmppl.R;
import com.example.projectmppl.adapter.ListRiwayatKuponAdapter;
import com.example.projectmppl.model.RiwayatKupon;
import com.example.projectmppl.model.TransaksiKupon;
import com.example.projectmppl.ui.ViewModelFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RiwayatActivity extends AppCompatActivity {

    @BindView(R.id.rv_riwayat_kupon)
    RecyclerView rvRiwayat;
    @BindView(R.id.poin_user)
    TextView poinUser;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ListRiwayatKuponAdapter listRiwayatAdapter ;
    private ArrayList<TransaksiKupon> transaksiKupons = new ArrayList<>();
    private ArrayList<String> listKey  = new ArrayList<>();
    private ArrayList<RiwayatKupon>listKeyTransaksi;
    private ArrayList<String> listKeyRiwayat;
    private int updateKupon = 0;
    private int currentKupon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);
        ButterKnife.bind(this);
        initRecycleView();
        initFirebase();
        getDataRiwayatTransaksi();
        loadPoinUser();


    }



    private void getDataRiwayatTransaksi(){
        showProgress();
        listKeyTransaksi = new ArrayList<>();
        listKeyRiwayat = new ArrayList<>();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("riwayatkupon")
                .child(currentUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot!=null){
                            hideProgress();
                            for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                                RiwayatKupon transaksi = dataItem.getValue(RiwayatKupon.class);
                                listKeyTransaksi.add(transaksi);
                                listKeyRiwayat.add(dataItem.getKey());
                            }
                            loadDataRiwayat(listKeyTransaksi,listKeyRiwayat);
                        }else {
                            hideProgress();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadDataRiwayat(ArrayList<RiwayatKupon>riwayatKupon,ArrayList<String>listKeyRiwayat){
        for (int i  = 0; i<riwayatKupon.size(); i++){
            String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("transaksikupon")
                    .child(currentUser)
                    .child(riwayatKupon.get(i).getIdTransaksi())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                hideProgress();
                                TransaksiKupon transaksiKupon = dataSnapshot.getValue(TransaksiKupon.class);
                                transaksiKupons.add(transaksiKupon);
                                listKey.add(dataSnapshot.getKey());
                            }
                                hideProgress();
                                listRiwayatAdapter = new ListRiwayatKuponAdapter(transaksiKupons,RiwayatActivity.this,listKey,listKeyRiwayat);
                                rvRiwayat.setAdapter(listRiwayatAdapter);
//                                listRiwayatAdapter.setOnShareClickedListener(this);

                        }



                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }




    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void initRecycleView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvRiwayat.setLayoutManager(linearLayoutManager);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void loadPoinUser(){
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataUser();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        liveData.observe(this, dataSnapshot -> {
            String poin = dataSnapshot.child(currentUser).child("poin").getValue().toString();
            try {
                poinUser.setText(poin);
            }
            catch (Exception e){

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
