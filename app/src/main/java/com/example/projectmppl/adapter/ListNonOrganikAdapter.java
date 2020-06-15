package com.example.projectmppl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmppl.R;
import com.example.projectmppl.model.KantongNonOrganik;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListNonOrganikAdapter extends RecyclerView.Adapter<ListNonOrganikAdapter.ViewHolder> {
    private List<KantongNonOrganik> listKantong;
    private List<String> listKey;

    public ListNonOrganikAdapter(){

    }

    public ListNonOrganikAdapter(List<KantongNonOrganik> listKantong, List<String> listKey){
        this.listKantong = listKantong;
        this.listKey = listKey;

    }



    @NonNull
    @Override
    public ListNonOrganikAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sampah, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListNonOrganikAdapter.ViewHolder holder, int position) {
        KantongNonOrganik kantong = listKantong.get(position);
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        holder.jumlah.setText(String.valueOf(kantong.getJumlah()));
        holder.total.setText(String.valueOf(kantong.getJumlahPoint()));
        holder.jenisSampah.setText(String.valueOf(kantong.getIdSampah()).replaceAll("\\[", "").replaceAll("" +
                "]",""));
        holder.btnHapus.setOnClickListener(view -> {
            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("kantong");
            databaseReference2
                    .child(currentUser)
                    .child("data")
                    .child("nonOrganik")
                    .child(listKey.get(position))
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            removeItem(position);
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return listKantong.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.total_berat)
        TextView jumlah;
        @BindView(R.id.total_poin)
        TextView total;
        @BindView(R.id.jenis_sampah)
        TextView jenisSampah;
        @BindView(R.id.btn_hapus)
        Button btnHapus;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void removeItem(int position) {
        listKantong.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        notifyItemRangeChanged(position, listKantong.size());
    }

    public void removeAllData(){
        listKantong.clear();
        notifyDataSetChanged();
    }
}
