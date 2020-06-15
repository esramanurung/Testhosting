package com.example.projectmppl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmppl.R;
import com.example.projectmppl.model.Kantong;
import com.example.projectmppl.model.KantongNonOrganik;
import com.example.projectmppl.model.Transaksi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListRiwayatAdapter extends RecyclerView.Adapter<ListRiwayatAdapter.ViewHolder> {
    private List<Transaksi> listTransaksi;
    private List<String> listKey;
    private List<String>listKeyRiwayat;

    public ListRiwayatAdapter(){

    }

    public ListRiwayatAdapter(List<Transaksi> listTransaksi,List<String>listKey,Context context,List<String>listKeyRiwayat){
        this.listTransaksi = listTransaksi;
        this.listKey  = listKey;
        this.listKeyRiwayat = listKeyRiwayat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat, null);
        return new ListRiwayatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi transaksi = listTransaksi.get(position);
        ArrayList<String> daftarElektronik = new ArrayList<>();
        ArrayList<String> daftarNonOrganik = new ArrayList<>();
        ArrayList<String> daftarPakaian = new ArrayList<>();
        int jumlahElektronik = 0;
        int totalBerat = 0;
        int jumlahPakaian = 0;
        holder.tanggalPemesanan.setText(String.valueOf(transaksi.getTanggalRequest()));
        holder.metode.setText(String.valueOf(transaksi.getMetode()));
        holder.elektronik.setText(String.valueOf(transaksi.getKantongElektronik()));
        holder.totalPoin.setText(String.valueOf(transaksi.getTotalPoin()));
        holder.status.setText(String.valueOf(transaksi.getStatus()));
        holder.lokasi.setText(String.valueOf(transaksi.getLokasi()));

        if (String.valueOf(transaksi.getStatus()).equals("Menunggu") || String.valueOf(transaksi.getStatus()).equals("Diterima")){
            holder.status.setTextColor(Color.GREEN);
//            holder.btnHapus.setEnabled(false);
        }else {
            holder.status.setTextColor(Color.RED);
        }

        if (transaksi.getKantongElektronik() == null){
            holder.elektronik.setText("-");
            holder.jumlahElektronik.setText(String.valueOf(0));
        }else{
            for (int index = 0;index<transaksi.getKantongElektronik().size();index++ ){
                Kantong kantong1 = transaksi.getKantongElektronik().get(index);
                daftarElektronik.add(String.valueOf(kantong1.getIdSampah()));
                jumlahElektronik = jumlahElektronik + kantong1.getJumlah();
            }
            holder.elektronik.setText(String.valueOf(daftarElektronik).replaceAll("\\[", "").replaceAll("" +
                    "]",""));
            holder.jumlahElektronik.setText(String.valueOf(jumlahElektronik));
        }


        if (transaksi.getKantongNonOrganiks() == null){
            holder.nonOrganik.setText("-");
            holder.jumlahOrganik.setText(String.valueOf(0));
        }else {
            for (int index = 0;index<transaksi.getKantongNonOrganiks().size();index++ ){
            KantongNonOrganik kantongNonOrganik = transaksi.getKantongNonOrganiks().get(index);
            daftarNonOrganik.add(String.valueOf(kantongNonOrganik.getIdSampah()));
            totalBerat = totalBerat + kantongNonOrganik.getJumlah();
        }

            holder.nonOrganik.setText(String.valueOf(daftarNonOrganik).replaceAll("\\[", "").replaceAll("" +
                    "]",""));
            holder.jumlahOrganik.setText(String.valueOf(totalBerat));
        }

        if (transaksi.getKantongPakaian() == null){
            holder.pakaian.setText("-");
            holder.jumlahPakaian.setText(String.valueOf(0));
        }else {
            for (int index = 0;index<transaksi.getKantongPakaian().size();index++ ){
                Kantong kantongPakaian = transaksi.getKantongPakaian().get(index);
                daftarPakaian.add(String.valueOf(kantongPakaian.getIdSampah()));
                jumlahPakaian = jumlahPakaian + kantongPakaian.getJumlah();
            }

            holder.pakaian.setText(String.valueOf(daftarPakaian).replaceAll("\\[", "").replaceAll("" +
                    "]",""));
            holder.jumlahPakaian.setText(String.valueOf(jumlahPakaian));
        }

        holder.btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("riwayatsampah");
                databaseReference2.child(currentUser)
                .child(listKeyRiwayat.get(position))
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        removeItem(position);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTransaksi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tgl_pemesanan)
        TextView tanggalPemesanan;
        @BindView(R.id.list_nonorganik)
        TextView nonOrganik;
        @BindView(R.id.jumlah_organik)
        TextView jumlahOrganik;
        @BindView(R.id.list_elektronik)
        TextView elektronik;
        @BindView(R.id.jumlah_elektronik)
        TextView jumlahElektronik;
        @BindView(R.id.list_pakaian)
        TextView pakaian;
        @BindView(R.id.jumlah_pakaian)
        TextView jumlahPakaian;
        @BindView(R.id.metode)
        TextView metode;
        @BindView(R.id.total_poin)
        TextView totalPoin;
        @BindView(R.id.lokasi)
        TextView lokasi;
        @BindView(R.id.hapus)
        Button btnHapus;
        @BindView(R.id.status)
        TextView status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void removeItem(int position) {
        listTransaksi.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        notifyItemRangeChanged(position,listTransaksi.size());
    }


}
