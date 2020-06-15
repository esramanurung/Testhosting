package com.example.projectmppl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmppl.R;
import com.example.projectmppl.activity.RiwayatActivity;
import com.example.projectmppl.model.RiwayatKupon;
import com.example.projectmppl.model.TransaksiKupon;
import com.example.projectmppl.ui.ViewModelFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListRiwayatKuponAdapter extends RecyclerView.Adapter<ListRiwayatKuponAdapter.ViewHolder> {
    private ArrayList<TransaksiKupon> transaksiKupons;
    private ArrayList<String> listKey;
    private Context context;
    private ArrayList<String> listKeyRiwayat;
    private int currentKupon;
    private int updateKupon;
    private ArrayList<RiwayatKupon>riwayatKupons;
    public ListRiwayatKuponAdapter(ArrayList<TransaksiKupon> transaksiKupons, Context context, ArrayList<String>listKey, ArrayList<String>listKeyRiwayat){
        this.transaksiKupons = transaksiKupons;
        this.context = context;
        this.listKey = listKey;
        this.listKeyRiwayat = listKeyRiwayat;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_kupon, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       holder.status.setText(transaksiKupons.get(position).getStatus());
       holder.tglPenukaran.setText(transaksiKupons.get(position).getTglPemesanan());
       holder.tglPemakaian.setText(transaksiKupons.get(position).getTglPemakaian());
       String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
       FirebaseDatabase
                .getInstance()
                .getReference()
                .child("kupon")
                .child(transaksiKupons.get(position).getIdKupon())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if (dataSnapshot!=null){
                          try {
                              String jenisKupon = dataSnapshot.child("jeniskupon").getValue().toString();
                              String jumlahpoin = dataSnapshot.child("jumlahpoin").getValue().toString();
                              String idKupon = dataSnapshot.child("idkupon").getValue().toString();
                              holder.jnsKupon.setText(jenisKupon);
                              holder.poin.setText(jumlahpoin);
                              holder.idKupon.setText(idKupon);

                          }catch (Exception e){

                          }
                      }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });



       holder.btnHapus.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("riwayatkupon");
               databaseReference2
                       .child(currentUser)
                       .child(listKeyRiwayat.get(position))
                       .removeValue()
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               itemRemove(position);
                               updateKuponUser();
                           }
                       });
           }
       });




    }

    @Override
    public int getItemCount() {
        return transaksiKupons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.tgl_penukaran)
        TextView tglPenukaran;
        @BindView(R.id.tgl_pemakaian)
        TextView tglPemakaian;
        @BindView(R.id.poin)
        TextView poin;
        @BindView(R.id.idkupon)
        TextView idKupon;
        @BindView(R.id.jns_kupon)
        TextView jnsKupon;
        @BindView(R.id.hapus)
        Button btnHapus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    private void itemRemove(int position){
        transaksiKupons.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, transaksiKupons.size());
        notifyDataSetChanged();

    }


    private void updateJumlahKuponUser(String key,int value){
        HashMap<String, Object> result = new HashMap<>();
        result.put(key, value);

        FirebaseDatabase.getInstance().getReference()
                .child("penukarSampah")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.", "_")).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateKuponUser(){
        riwayatKupons = new ArrayList<>();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("riwayatkupon")
                .child(currentUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null){
                            for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                                RiwayatKupon riwayatKupon = dataItem.getValue(RiwayatKupon.class);
                                riwayatKupons.add(riwayatKupon);
                            }
                            updateKupon = riwayatKupons.size();
                        }
                        updateJumlahKuponUser("kupon", updateKupon);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


}
