package com.example.projectmppl.activity.jenissampah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.projectmppl.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ElektronikActivity extends AppCompatActivity  {
    private View view;
    @BindView(R.id.img_komputer)
    ImageView imgKomputer;
    @BindView(R.id.img_hp)
    ImageView imgHp;
    @BindView(R.id.img_prumah)
    ImageView imgPerRumah;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elektronik);
        ButterKnife.bind(this);

        // SetOnclickListener
        imgKomputer.setOnClickListener(view -> {
            Intent intentKomputer = new Intent(ElektronikActivity.this, KomputerActivity.class);
            startActivity(intentKomputer);
        });
        imgHp.setOnClickListener(view -> {
            Intent intentHp = new Intent(ElektronikActivity.this, HandphoneActivity.class);
            startActivity(intentHp);
        });
        imgPerRumah.setOnClickListener(view -> {
            Intent intentRumah = new Intent(ElektronikActivity.this, PerlengkapanRumahTanggaActivity.class);
            startActivity(intentRumah);
        });
    }
}
