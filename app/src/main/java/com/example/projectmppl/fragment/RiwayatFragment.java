package com.example.projectmppl.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.projectmppl.R;
import com.example.projectmppl.adapter.ListRiwayatAdapter;
import com.example.projectmppl.model.RiwayatSampah;
import com.example.projectmppl.model.Transaksi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RiwayatFragment extends Fragment {


    private List<Transaksi> listData = new ArrayList<>();
    private List<String>keyTransaksi = new ArrayList<>();
    @BindView(R.id.rv_riwayat)
    RecyclerView recyclerViewData;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private ListRiwayatAdapter listRiwayatAdapter;
    private ArrayList<RiwayatSampah>listKeyTransaksi;
    private ArrayList<String> listKeyRiwayat;

    public RiwayatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_riwayat, container, false);
        ButterKnife.bind(this, view);
        getIdTransaksi();
        initFirebase();
        return view;
    }


    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("transaksipenukaransampah");
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerViewData.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }

    private void getIdTransaksi(){
        showProgress();
        listKeyTransaksi = new ArrayList<>();
        listKeyRiwayat = new ArrayList<>();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("riwayatsampah")
                .child(currentUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot!=null){
                            hideProgress();
                            for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                                RiwayatSampah transaksi = dataItem.getValue(RiwayatSampah.class);
                                listKeyTransaksi.add(transaksi);
                                listKeyRiwayat.add(dataItem.getKey());
                            }

                            loadDataFirebase(listKeyTransaksi,listKeyRiwayat);
                        }else {
                            hideProgress();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadDataFirebase(ArrayList<RiwayatSampah>riwayatSampahs,ArrayList<String>listKeyRiwayat){

        for (int i=0 ; i < riwayatSampahs.size();i++){
            String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("transaksipenukaransampah")
                    .child(currentUser)
                    .child(riwayatSampahs.get(i).getIdTransaksi())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null){
                                hideProgress();
                                Transaksi transaksi = dataSnapshot.getValue(Transaksi.class);
                                listData.add(transaksi);
                                keyTransaksi.add(dataSnapshot.getKey());
                            }
                            listRiwayatAdapter = new ListRiwayatAdapter(listData,keyTransaksi,getContext(),listKeyRiwayat);
                            recyclerViewData.setAdapter(listRiwayatAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }




    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }



    @Override
    public void onPause() {
        super.onPause();
        listData.clear();
        keyTransaksi.clear();
        listRiwayatAdapter = new ListRiwayatAdapter(listData,keyTransaksi,getActivity(),listKeyRiwayat);
        recyclerViewData.setAdapter(listRiwayatAdapter);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
