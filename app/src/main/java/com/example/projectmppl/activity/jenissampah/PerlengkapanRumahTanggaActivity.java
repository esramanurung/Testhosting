package com.example.projectmppl.activity.jenissampah;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.projectmppl.R;
import com.example.projectmppl.adapter.ListSampahAdapter;
import com.example.projectmppl.data.RumahTanggaData;
import com.example.projectmppl.model.Sampah;

import java.util.ArrayList;

public class PerlengkapanRumahTanggaActivity extends AppCompatActivity {
    private RecyclerView rvRumahTangga;
    private final ArrayList<Sampah> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perlengkapan_rumah_tangga);

        rvRumahTangga = findViewById(R.id.rv_rumahtangga);
        rvRumahTangga.setHasFixedSize(true);

        list.addAll(RumahTanggaData.getListData());

        showRecycleView();
    }

    private void showRecycleView(){
        rvRumahTangga.setLayoutManager(new LinearLayoutManager(this));
        ListSampahAdapter listSampahAdapter = new ListSampahAdapter(list,this,"PRT","elektronik");
        rvRumahTangga.setAdapter(listSampahAdapter);
    }

}
