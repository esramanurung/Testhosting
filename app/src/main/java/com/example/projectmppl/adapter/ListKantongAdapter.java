package com.example.projectmppl.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.projectmppl.activity.KantongActivity;
import com.example.projectmppl.fragment.KantongFragment;
import com.example.projectmppl.model.Kantong;
import com.example.projectmppl.ui.ViewModelFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListKantongAdapter extends RecyclerView.Adapter<ListKantongAdapter.ViewHolder> {
    private List<Kantong> listKantong;
    OnRemovedListener mCallback;
    private String jenis;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    private Context context;
    private List<String>keySampah;

    public ListKantongAdapter(){

    }

    public ListKantongAdapter(List<Kantong> listKantong,List<String>keySampah, Context context,String jenis){
        this.listKantong = listKantong;
        this.context = context;
        this.keySampah = keySampah;
        this.jenis = jenis;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sampah_elektronik, null);
        return new ViewHolder(view);
    }

    public interface OnRemovedListener{
        void RemoveClicked(String key, int position, String jenis);
    }

    public void setOnShareClickedListener(OnRemovedListener mCallback) {
        this.mCallback = mCallback;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        Kantong kantong = listKantong.get(position);
        holder.jumlah.setText(String.valueOf(kantong.getJumlah()));
        holder.total.setText(String.valueOf(kantong.getJumlahPoint()));
        holder.jenisSampah.setText(String.valueOf(kantong.getIdSampah()).replaceAll("\\[", "").replaceAll("" +
                "]",""));
        holder.btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("kantong");
                databaseReference2
                        .child(currentUser)
                        .child("data")
                        .child(jenis)
                        .child(keySampah.get(position))
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                removeItem(position);
                            }
                        });
            }
        });
        holder.kondisiBagus.setText(String.valueOf(kantong.getBagus()));
        holder.kondisiBerat.setText(String.valueOf(kantong.getBerat()));
        holder.kondisiRingan.setText(String.valueOf(kantong.getSedang()));
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
        @BindView(R.id.kondisi_bagus)
        TextView kondisiBagus;
        @BindView(R.id.kondisi_ringan)
        TextView kondisiRingan;
        @BindView(R.id.kondisi_berat)
        TextView kondisiBerat;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void removeItem(int position) {
        listKantong.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,listKantong.size());
        notifyDataSetChanged();

    }

    public void removeAllData(){
        listKantong.clear();
        notifyDataSetChanged();
    }
}
