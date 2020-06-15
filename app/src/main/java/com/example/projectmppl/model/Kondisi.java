package com.example.projectmppl.model;

public class Kondisi {
    private int bagus;
    private int sedang;
    private int berat;

    public Kondisi(){

    }

    public Kondisi(int bagus, int sedang, int berat) {
        this.bagus = bagus;
        this.sedang = sedang;
        this.berat = berat;
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
}
