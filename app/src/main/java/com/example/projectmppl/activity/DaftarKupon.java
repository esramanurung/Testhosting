package com.example.projectmppl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmppl.R;
import com.example.projectmppl.adapter.ListKantongAdapter;
import com.example.projectmppl.adapter.ListKuponAdapter;
import com.example.projectmppl.adapter.ListRiwayatKuponAdapter;
import com.example.projectmppl.model.Kantong;
import com.example.projectmppl.model.Kupon;
import com.example.projectmppl.model.RiwayatKupon;
import com.example.projectmppl.model.TransaksiKupon;
import com.example.projectmppl.ui.ViewModelFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DaftarKupon extends AppCompatActivity {
    @BindView(R.id.rv_kuponsaya)
    RecyclerView rvKuponSaya;
    @BindView(R.id.rv_indomaret)
    RecyclerView rvIndomaret;
    @BindView(R.id.rv_koperasi)
    RecyclerView rvKoperasi;
    @BindView(R.id.rv_keasramaan)
    RecyclerView rvKeasramaan;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.poin_user)
    TextView poinUser;
    ListKuponAdapter listKuponAdapter;
    ListKuponAdapter listKuponKoperasi;
    ListKuponAdapter listKuponKemahasiswaan;
    ListKuponAdapter listKuponSaya;
    ArrayList<Kupon> indomaret;
    ArrayList<Kupon> koperasi;
    ArrayList<Kupon> kemahasiswaan;
    ArrayList<Kupon> kuponSaya;
    private ArrayList<RiwayatKupon>listKeyTransaksi;
    private ArrayList<String> idKupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_kupon);
        ButterKnife.bind(this);
        initRecycleView();
        loadDataIndomaret();
        loadDataKeasramaan();
        loadDataKoperasi();
        loadPoinUser();
        getDataRiwayat();
    }

    public void initRecycleView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager linearLayoutIndomaret = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager linearLayoutKoperasi = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager linearLayoutKeasramaan = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        rvKuponSaya.setLayoutManager(linearLayoutManager);
        rvIndomaret.setLayoutManager(linearLayoutIndomaret);
        rvKoperasi.setLayoutManager(linearLayoutKoperasi);
        rvKeasramaan.setLayoutManager(linearLayoutKeasramaan);
    }

    public void loadDataIndomaret(){
        showProgress();
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getDaftarKupon();
        indomaret = new ArrayList<>();
        liveData.observe(this, dataSnapshot -> {
            if (dataSnapshot!=null){
                hideProgress();
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    try {
                        Kupon kupon = dataItem.getValue(Kupon.class);
                        if (kupon.getJeniskupon().equals("Indomaret")){
                            indomaret.add(kupon);
                        }
                    }catch (Exception e){

                    }
                }
                listKuponAdapter = new ListKuponAdapter(indomaret,this,false);
                rvIndomaret.setAdapter(listKuponAdapter);
            }
        });
    }

    public void loadDataKoperasi(){
        showProgress();
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getDaftarKupon();
        koperasi = new ArrayList<>();
        liveData.observe(this, dataSnapshot -> {
            if (dataSnapshot!=null){
                hideProgress();
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    try {
                        Kupon kupon = dataItem.getValue(Kupon.class);
                        if (kupon.getJeniskupon().equals("Koperasi")){
                            koperasi.add(kupon);
                        }
                    }catch (Exception e){

                    }
                }
                listKuponKoperasi = new ListKuponAdapter(koperasi,this,false);
                rvKoperasi.setAdapter(listKuponKoperasi);
            }
        });
    }


    public void loadDataKeasramaan(){
        showProgress();
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getDaftarKupon();
        kemahasiswaan = new ArrayList<>();
        liveData.observe(this, dataSnapshot -> {
            if (dataSnapshot!=null){
                hideProgress();
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    try {
                        Kupon kupon = dataItem.getValue(Kupon.class);
                        if (kupon.getJeniskupon().equals("Sanksi Sosial")){
                            kemahasiswaan.add(kupon);
                        }
                    }catch (Exception e){

                    }
                }
                listKuponKemahasiswaan = new ListKuponAdapter(kemahasiswaan,this,false);
                rvKeasramaan.setAdapter(listKuponKemahasiswaan);
            }

        });
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
    protected void onPause() {
        super.onPause();
        try {
            indomaret.clear();
            kemahasiswaan.clear();
            koperasi.clear();
            kuponSaya.clear();
            listKeyTransaksi.clear();
            idKupon.clear();
        }catch (Exception e){

        }
    }

    private void getDataRiwayat(){
        showProgress();
        listKeyTransaksi = new ArrayList<>();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getRiwayatKupon();
        liveData.observe(this, dataSnapshot -> {
            if (dataSnapshot!=null){
                hideProgress();
                for (DataSnapshot dataItem : dataSnapshot.child(currentUser).getChildren()) {
                    RiwayatKupon transaksi = dataItem.getValue(RiwayatKupon.class);
                    listKeyTransaksi.add(transaksi);
                }
                loadDataTransaksi(listKeyTransaksi);
            }else {
                hideProgress();
            }
        });
    }



    private void loadDataTransaksi(ArrayList<RiwayatKupon>riwayatKupon){
        idKupon = new ArrayList<>();
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
                                idKupon.add(transaksiKupon.getIdKupon());
                            }
                            hideProgress();
                            loadKupon(idKupon);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }


    }
//    private void loadKupon(ArrayList<String>transaksiKupons){
//        kuponSaya = new ArrayList<>();
//        for (int i  = 0; i<transaksiKupons.size(); i++){
//            String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
//            FirebaseDatabase
//                    .getInstance()
//                    .getReference()
//                    .child("kupon")
//                    .child(transaksiKupons.get(i))
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot!=null){
//                                hideProgress();
//                                Kupon kupon = dataSnapshot.getValue(Kupon.class);
//                                kuponSaya.add(kupon);
//                            }
//                            listKuponSaya = new ListKuponAdapter(kuponSaya,DaftarKupon.this);
//                            rvKuponSaya.setAdapter(listKuponSaya);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//        }
//
//    }

    private void loadKupon(ArrayList<String>transaksiKupons){
        kuponSaya = new ArrayList<>();
        for (int i  = 0; i<transaksiKupons.size(); i++){
            showProgress();
            ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
            LiveData<DataSnapshot> liveData = viewModel.getDaftarKupon();
            int finalI = i;
            liveData.observe(this, dataSnapshot -> {
                try {
                    if (dataSnapshot!=null){
                        hideProgress();
                        Kupon kupon = dataSnapshot.child(transaksiKupons.get(finalI)).getValue(Kupon.class);
                        kuponSaya.add(kupon);
                    }
                    listKuponSaya = new ListKuponAdapter(kuponSaya,DaftarKupon.this,true);
                    rvKuponSaya.setAdapter(listKuponSaya);
                }catch (Exception e){

                }
            });
        }


    }

    public void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }
}
