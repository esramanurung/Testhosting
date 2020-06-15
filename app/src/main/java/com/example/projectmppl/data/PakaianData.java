package com.example.projectmppl.data;

import com.example.projectmppl.model.Sampah;

import java.util.ArrayList;

public class PakaianData {
    private static final String[] pakaianNames= {
            "Kaos",
            "Kemeja",
            "Jaket",
            "Selimut",
            "Sepatu",
            "Tas"
    };

    public static ArrayList<Sampah> getListData() {
        ArrayList<Sampah> list = new ArrayList<>();
        for (String pakaianName : pakaianNames) {
            Sampah pakaian = new Sampah();
            pakaian.setName(pakaianName);
            list.add(pakaian);
        }
        return list;
    }
}
