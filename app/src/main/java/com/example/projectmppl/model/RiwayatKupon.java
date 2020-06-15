package com.example.projectmppl.model;

public class RiwayatKupon {
    private String idTransaksi;

    public RiwayatKupon(){

    }
    public RiwayatKupon(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }
}
