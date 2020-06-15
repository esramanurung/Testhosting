package com.example.projectmppl.activity.jenissampah;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.projectmppl.R;
import com.example.projectmppl.adapter.ListSampahAdapter;
import com.example.projectmppl.data.PakaianData;
import com.example.projectmppl.model.Sampah;

import java.util.ArrayList;

public class PakaianActivity extends AppCompatActivity {
    private RecyclerView rvPakaian;
    private final ArrayList<Sampah> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pakaian);

        rvPakaian = findViewById(R.id.rv_pakaian);
        rvPakaian.setHasFixedSize(true);

        list.addAll(PakaianData.getListData());
        showRecylerView();
    }

    private void showRecylerView(){
        rvPakaian.setLayoutManager(new LinearLayoutManager(this));
        ListSampahAdapter listSampahAdapter = new ListSampahAdapter(list,this,"Pakaian","pakaian");
        rvPakaian.setAdapter(listSampahAdapter);
    }
}
