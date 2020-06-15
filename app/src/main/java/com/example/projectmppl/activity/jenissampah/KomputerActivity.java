package com.example.projectmppl.activity.jenissampah;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.projectmppl.R;
import com.example.projectmppl.adapter.ListSampahAdapter;
import com.example.projectmppl.data.KomputerData;
import com.example.projectmppl.model.Sampah;

import java.util.ArrayList;

public class KomputerActivity extends AppCompatActivity {
    private RecyclerView rvKomputer;
    private final ArrayList<Sampah> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komputer);

        rvKomputer = findViewById(R.id.rv_komputer);
        rvKomputer.setHasFixedSize(true);

        list.addAll(KomputerData.getListData());
        showRecylerview();


    }

    private void showRecylerview(){
        rvKomputer.setLayoutManager(new LinearLayoutManager(this));
        ListSampahAdapter listSampahAdapter = new ListSampahAdapter(list,this,"komputer","elektronik");
        rvKomputer.setAdapter(listSampahAdapter);
    }
}
