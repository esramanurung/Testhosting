package com.example.projectmppl.model;

public class TransaksiKupon {
    private String idKupon;
    private String status;
    private String tglPemesanan;
    private String tglPemakaian;

    public TransaksiKupon(){

    }


    public TransaksiKupon(String idKupon, String status, String tglPemesanan, String tglPemakaian) {
        this.idKupon = idKupon;
        this.status = status;
        this.tglPemesanan = tglPemesanan;
        this.tglPemakaian = tglPemakaian;
    }

    public String getIdKupon() {
        return idKupon;
    }

    public void setIdKupon(String idKupon) {
        this.idKupon = idKupon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTglPemesanan() {
        return tglPemesanan;
    }

    public void setTglPemesanan(String tglPemesanan) {
        this.tglPemesanan = tglPemesanan;
    }

    public String getTglPemakaian() {
        return tglPemakaian;
    }

    public void setTglPemakaian(String tglPemakaian) {
        this.tglPemakaian = tglPemakaian;
    }
}
