package com.example.projectmppl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Kupon implements Parcelable {
    private String idkupon;
    private String  jeniskupon;
    private int jumlahpoin;
    private String namakupon;
    private String url;

    public Kupon(){

    }

    public Kupon(String idkupon, String jeniskupon, int jumlahpoin, String namakupon, String url) {
        this.idkupon = idkupon;
        this.jeniskupon = jeniskupon;
        this.jumlahpoin = jumlahpoin;
        this.namakupon = namakupon;
        this.url = url;
    }

    public String getIdkupon() {
        return idkupon;
    }

    public void setIdkupon(String idkupon) {
        this.idkupon = idkupon;
    }

    public String getJeniskupon() {
        return jeniskupon;
    }

    public void setJeniskupon(String jeniskupon) {
        this.jeniskupon = jeniskupon;
    }

    public int getJumlahpoin() {
        return jumlahpoin;
    }

    public void setJumlahpoin(int jumlahpoin) {
        this.jumlahpoin = jumlahpoin;
    }

    public String getNamakupon() {
        return namakupon;
    }

    public void setNamakupon(String namakupon) {
        this.namakupon = namakupon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idkupon);
        parcel.writeString(jeniskupon);
        parcel.writeString(namakupon);
        parcel.writeString(url);
        parcel.writeInt(jumlahpoin);

    }

    // Perhatikan method yang dipanggil pada objek in
    protected Kupon(Parcel in) {
        this.idkupon = in.readString();
        this.jeniskupon = in.readString();
        this.namakupon = in.readString();
        this.url = in.readString();
        this.jumlahpoin = in.readInt();
    }

    // Cukup sesuaikan nama objeknya
    public static final Parcelable.Creator<Kupon> CREATOR = new Parcelable.Creator<Kupon>() {
        @Override
        public Kupon createFromParcel(Parcel source) {
            return new Kupon(source);
        }

        @Override
        public Kupon[] newArray(int size) {
            return new Kupon[size];
        }
    };
}
