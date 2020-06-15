package com.example.projectmppl.model;

import com.example.projectmppl.activity.RiwayatActivity;

public class RiwayatSampah {
    String idTransaksi;


    public RiwayatSampah(){

    }
    public RiwayatSampah(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }
}
