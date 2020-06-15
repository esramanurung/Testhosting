package com.example.projectmppl.data;

import com.example.projectmppl.model.Sampah;

import java.util.ArrayList;

public class RumahTanggaData {
    private static final String[] pakaianNames= {
            "TV",
            "Kulkas",
            "Mesin Cuci",
            "VacuumCleaner",
            "Kamera",
            "Jam",
            "Blender",
            "Dispenser",
            "Setrika"
    };

    public static ArrayList<Sampah> getListData() {
        ArrayList<Sampah> list = new ArrayList<>();
        for (String pakaianName : pakaianNames) {
            Sampah rumahTangga = new Sampah();
            rumahTangga.setName(pakaianName);
            list.add(rumahTangga);
        }
        return list;
    }
}
