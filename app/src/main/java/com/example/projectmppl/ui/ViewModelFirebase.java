package com.example.projectmppl.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.projectmppl.data.FirebaseQueryLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class ViewModelFirebase extends androidx.lifecycle.ViewModel {
    private static final DatabaseReference mDatabase = FirebaseDatabase.getInstance()
            .getReference()
            .child("kantong");

    private static final DatabaseReference kondisi = FirebaseDatabase.getInstance()
            .getReference()
            .child("sampah");

    private static final DatabaseReference databaseUser = FirebaseDatabase.getInstance()
            .getReference()
            .child("penukarSampah");

    private static final DatabaseReference dataTransaksi = FirebaseDatabase.getInstance()
            .getReference()
            .child("transaksipenukaransampah");

    private static final DatabaseReference dataTransaksiKupon = FirebaseDatabase.getInstance()
            .getReference()
            .child("transaksikupon");

    private static final DatabaseReference daftarKupon = FirebaseDatabase.getInstance()
            .getReference()
            .child("kupon");

    private static final DatabaseReference riwayatKupon = FirebaseDatabase.getInstance()
            .getReference()
            .child("riwayatkupon");



    private final FirebaseQueryLiveData liveDataKantong = new FirebaseQueryLiveData(mDatabase);
    private final FirebaseQueryLiveData liveDataKondisi = new FirebaseQueryLiveData(kondisi);
    private final FirebaseQueryLiveData liveDataDataUser = new FirebaseQueryLiveData(databaseUser);

    private final FirebaseQueryLiveData liveDataTransaksi = new FirebaseQueryLiveData(dataTransaksi);
    private final FirebaseQueryLiveData liveDataKupon = new FirebaseQueryLiveData(daftarKupon);

    private final FirebaseQueryLiveData liveDataTransaksiKupon = new FirebaseQueryLiveData(dataTransaksiKupon);

    private final FirebaseQueryLiveData liveDataRiwayatKupon = new FirebaseQueryLiveData(riwayatKupon);

    @NonNull
    public LiveData<DataSnapshot> getdataSnapshotLiveData(){
        return liveDataKantong;
    }

    @NonNull
    public LiveData<DataSnapshot> getdataKondisi(){
        return liveDataKondisi;
    }

    @NonNull
    public LiveData<DataSnapshot> getdataUser(){
        return liveDataDataUser;
    }

    @NonNull
    public LiveData<DataSnapshot> getdataTransaksi(){
        return liveDataTransaksi;
    }

    @NonNull
    public LiveData<DataSnapshot> getDaftarKupon(){
        return liveDataKupon;
    }

    @NonNull
    public LiveData<DataSnapshot> getTransaksiKupon(){
        return liveDataTransaksiKupon;
    }

    @NonNull
    public LiveData<DataSnapshot> getRiwayatKupon(){
        return liveDataRiwayatKupon;
    }
}
