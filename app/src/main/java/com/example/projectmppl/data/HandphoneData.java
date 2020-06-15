package com.example.projectmppl.data;

import com.example.projectmppl.model.Sampah;

import java.util.ArrayList;

public class HandphoneData {
    private static final String[] sampahNames= {
            "Telepon Seluler",
            "Charger",
            "Casing",
            "PowerBank",
            "Earphone"
    };

    public static ArrayList<Sampah> getListData() {
        ArrayList<Sampah> list = new ArrayList<>();
        for (String sampahName : sampahNames) {
            Sampah handphone = new Sampah();
            handphone.setName(sampahName);
            list.add(handphone);
        }
        return list;
    }
}
