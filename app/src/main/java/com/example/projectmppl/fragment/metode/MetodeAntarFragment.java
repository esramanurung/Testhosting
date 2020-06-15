package com.example.projectmppl.fragment.metode;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.projectmppl.R;
import com.example.projectmppl.activity.KantongActivity;
import com.example.projectmppl.activity.MainActivity;
import com.example.projectmppl.adapter.ListKantongAdapter;

import com.example.projectmppl.adapter.ListRiwayatAdapter;

import com.example.projectmppl.adapter.ListNonOrganikAdapter;

import com.example.projectmppl.model.Kantong;
import com.example.projectmppl.model.KantongNonOrganik;
import com.example.projectmppl.model.RiwayatSampah;
import com.example.projectmppl.model.Transaksi;
import com.example.projectmppl.ui.ViewModelFirebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MetodeAntarFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReferenceRiwayat;
    private FirebaseDatabase firebaseDatabase;
    private ListKantongAdapter listKantongAdapter = new ListKantongAdapter();

    private ArrayList<Kantong>listKey;
    private ArrayList<KantongNonOrganik>kantongNonOrganiks;
    private ArrayList<Kantong>inputPakaian;
    private int totalPoint;
    private ArrayList<String> listKeyRiwayat;
    private String keyRiwayat;

    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.btn_request)
    Button btnRequest;
    @BindView(R.id.spinner)
    Spinner lokasiSpinner;




    public MetodeAntarFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_metode_antar, container, false);
        ButterKnife.bind(this,view);

        hideProgress();
        initFirebase();

        btnRequest.setOnClickListener(this::onClick);
        return view;

    }



    private void addTransaksi(Transaksi transaksi){
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        showProgress();
        saveRiwayat();
        firebaseDatabase
                .getReference()
                .child("transaksipenukaransampah")
                .child(currentUser)
                .push()
                .setValue(transaksi)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgress();
                        Intent intent = new Intent(getActivity(), KantongActivity.class);
                        intent.putExtra("saveData","removeData");
                        intent.putExtra("removeData","remove");
                        startActivity(intent);
                    }
                });

    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("transaksipenukaransampah");
        databaseReferenceRiwayat  = FirebaseDatabase.getInstance().getReference("riwayat");
        firebaseDatabase = FirebaseDatabase.getInstance();
    }


    private void loadDataFirebase() {
        initFirebase();
        String lokasi = lokasiSpinner.getSelectedItem().toString().trim();
        if (lokasi.equals("Lokasi")){
            Toast.makeText(getContext(),"Silahkan Masukkan Lokasi",Toast.LENGTH_SHORT).show();
        }
        else {
            String metode = "Antar";
            String status = "Menunggu";
            String datetime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            showProgress();
            String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
            ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
            LiveData<DataSnapshot> liveData = viewModel.getdataSnapshotLiveData();

            kantongNonOrganiks = new ArrayList<>();
            listKey = new ArrayList<>();
            inputPakaian = new ArrayList<>();
            totalPoint = 0;

            liveData.observe(this, dataSnapshot -> {
                if (dataSnapshot != null){

                    hideProgress();
                    for (DataSnapshot dataItem : dataSnapshot.child(currentUser).child("data").child("elektronik").getChildren()) {
                        Kantong kantong = dataItem.getValue(Kantong.class);
                        listKey.add(kantong);
                        totalPoint = totalPoint+kantong.getJumlahPoint();
                    }

                    for (DataSnapshot dataItem : dataSnapshot.child(currentUser).child("data").child("nonOrganik").getChildren()) {
                        KantongNonOrganik kantongNonOrganik = dataItem.getValue(KantongNonOrganik.class);
                        kantongNonOrganiks.add(kantongNonOrganik);
                        totalPoint = totalPoint+kantongNonOrganik.getJumlahPoint();
                    }
                    for (DataSnapshot dataItem : dataSnapshot.child(currentUser).child("data").child("pakaian").getChildren()) {
                        Kantong kantongPakaian = dataItem.getValue(Kantong.class);
                        inputPakaian.add(kantongPakaian);
                        totalPoint = totalPoint+kantongPakaian.getJumlahPoint();
                    }

                    if (totalPoint != 0){
                        Transaksi transaksi = new Transaksi("url", listKey, metode, status, datetime, totalPoint, lokasi, kantongNonOrganiks,inputPakaian);
                        addTransaksi(transaksi);
                    }
                }
            });
        }


    }



    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Tag", "FragmentAntar.onPause() has been called.");
    }


    public void saveRiwayat() {
        listKeyRiwayat = new ArrayList<>();
        keyRiwayat = "";
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.", "_");
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataTransaksi();
        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    for (DataSnapshot dataItem : dataSnapshot.child(currentUser).getChildren()) {
                        keyRiwayat = dataItem.getKey();
                    }
                    RiwayatSampah riwayatSampah = new RiwayatSampah(keyRiwayat);
                    saveIdTransaksi(currentUser,riwayatSampah);
                }
            }
        });
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        lokasiSpinner.setEnabled(false);
        btnRequest.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (view == btnRequest){
            loadDataFirebase();
        }

    }

    private void saveIdTransaksi(String currentUser, RiwayatSampah riwayatSampah){
            HashMap<String, Object> result = new HashMap<>();
            result.put(currentUser,riwayatSampah);

            firebaseDatabase.getReference()
                    .child("riwayatsampah")
                    .child(currentUser)
                    .push()
                    .setValue(riwayatSampah)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }


}
