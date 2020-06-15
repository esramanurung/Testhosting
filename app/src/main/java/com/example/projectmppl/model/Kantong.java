package com.example.projectmppl.model;

public class Kantong  {
    private String idSampah;
    private String jenisSampah;
    private int jumlah;
    private int jumlahPoint;
    private int bagus;
    private int sedang;
    private int berat;

    public Kantong(){

    }

    public Kantong( String idSampah, int jumlah, int jumlahPoint, String jenisSampah, int bagus, int ringan, int berat ) {
        this.idSampah = idSampah;
        this.jumlah = jumlah;
        this.jumlahPoint = jumlahPoint;
        this.jenisSampah = jenisSampah;
        this.berat = berat;
        this.sedang = ringan;
        this.bagus = bagus;
    }


    public int getBagus() {
        return bagus;
    }

    public void setBagus(int bagus) {
        this.bagus = bagus;
    }

    public int getSedang() {
        return sedang;
    }

    public void setSedang(int sedang) {
        this.sedang = sedang;
    }

    public int getBerat() {
        return berat;
    }

    public void setBerat(int berat) {
        this.berat = berat;
    }

    public String getJenisSampah() {
        return jenisSampah;
    }

    public void setJenisSampah(String jenisSampah) {
        this.jenisSampah = jenisSampah;
    }


    public String getIdSampah() {
        return idSampah;
    }

    public void setIdSampah(String idSampah) {
        this.idSampah = idSampah;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getJumlahPoint() {
        return jumlahPoint;
    }

    public void setJumlahPoint(int jumlahPoint) {
        this.jumlahPoint = jumlahPoint;
    }
}
