package com.example.projectmppl.data;

import com.example.projectmppl.model.Sampah;

import java.util.ArrayList;

public class KomputerData {
    private static final String[] komputerNames= {
            "Harddisk",
            "Mouse",
            "Monitor",
            "Keyboard",
            "Speaker",
            "Printer",
            "CPU"
    };

    public static ArrayList<Sampah> getListData() {
        ArrayList<Sampah> list = new ArrayList<>();
        for (String komputerName : komputerNames) {
            Sampah komputer = new Sampah();
            komputer.setName(komputerName);
            list.add(komputer);
        }
        return list;
    }
}
