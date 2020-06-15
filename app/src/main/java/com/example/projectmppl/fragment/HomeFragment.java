package com.example.projectmppl.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectmppl.R;
import com.example.projectmppl.activity.DaftarKupon;
import com.example.projectmppl.activity.KantongActivity;
import com.example.projectmppl.activity.RiwayatActivity;
import com.example.projectmppl.activity.jenissampah.NonOrganikActivity;
import com.example.projectmppl.activity.jenissampah.PakaianActivity;
import com.example.projectmppl.model.RiwayatKupon;
import com.example.projectmppl.ui.ViewModelFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener  {
    @BindView(R.id.kupon)
    CardView cardView;
    @BindView(R.id.poin)
    CardView cardPoin;
    @BindView(R.id.img_nonorganik)
    ImageView nonOrganik;
    @BindView(R.id.img_elektronik)
    ImageView elektronik;
    @BindView(R.id.img_pakaian)
    ImageView pakaian;
    @BindView(R.id.dashboard_iv_kantong)
    ImageView kantong;
    @BindView(R.id.dashboard_tv_garbagechange)
    TextView textView;
    @BindView(R.id.dashboard_tv_name)
    TextView tvName;
    @BindView(R.id.total_poin)
    TextView totalPoin;
    @BindView(R.id.dashboard_iv_profile)
    CircularImageView fotoProfil;
    @BindView(R.id.total_kupon)
    TextView totalKupon;

    private int jumlahKupon = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        cardView.setOnClickListener(this);
        cardPoin.setOnClickListener(this);
        nonOrganik.setOnClickListener(this);
        elektronik.setOnClickListener(this);
        pakaian.setOnClickListener(this);
        kantong.setOnClickListener(this);

        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataUser();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        liveData.observe(this, dataSnapshot -> {
            // Untuk menampilkan dataUser
            String image = dataSnapshot.child(currentUser).child("image").getValue().toString();
            String name = dataSnapshot.child(currentUser).child("nama").getValue().toString();
            String poin = dataSnapshot.child(currentUser).child("poin").getValue().toString();
            tvName.setText(name);
            totalPoin.setText(poin);

            try {
                Picasso.get().load(image).into(fotoProfil);
            }
            catch (Exception e){
                Picasso.get().load(R.drawable.user).into(fotoProfil);
            }
        });
        loadKuponUser();

        return view;

    }

    private void loadKuponUser(){
        jumlahKupon = 0;
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataUser();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        liveData.observe(this, dataSnapshot -> {
            String kupon = dataSnapshot.child(currentUser).child("kupon").getValue().toString();
            try {
                jumlahKupon = Integer.parseInt(kupon);
                totalKupon.setText(kupon);
            }
            catch (Exception e){

            }
        });
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kupon:
                Intent intent = new Intent(getActivity(), DaftarKupon.class);
                startActivity(intent);
                break;
            case R.id.poin:
                Intent intentPoin = new Intent(getActivity(), RiwayatActivity.class);
                startActivity(intentPoin);
                break;

            case R.id.img_nonorganik:
                Intent nonOrganik = new Intent(getActivity(), NonOrganikActivity.class);
                startActivity(nonOrganik);
                break;
            case R.id.img_elektronik:
                Intent elektronik = new Intent(getActivity(), com.example.projectmppl.activity.jenissampah.ElektronikActivity.class);
                startActivity(elektronik);
                break;
            case R.id.img_pakaian:
                Intent pakaian = new Intent(getActivity(), PakaianActivity.class);
                startActivity(pakaian);
                break;
            case R.id.dashboard_iv_kantong:
                Intent kantong = new Intent(getActivity(), KantongActivity.class);
                kantong.putExtra("saveData","remove");
                kantong.putExtra("removeData","remove");
                kantong.putExtra("key","keyProduct");
                kantong.putExtra("position",-1);
                kantong.putExtra("jenis","sampah");
                startActivity(kantong);
        }

    }



}
