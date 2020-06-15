package com.example.projectmppl.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmppl.R;
import com.example.projectmppl.activity.DaftarKupon;
import com.example.projectmppl.activity.PenukaranKupon;
import com.example.projectmppl.model.Kantong;
import com.example.projectmppl.model.Kupon;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListKuponAdapter extends RecyclerView.Adapter<ListKuponAdapter.ViewHolder>{
   private ArrayList<Kupon> kupons;
   private Context context;
   private Boolean kuponSaya;

   public ListKuponAdapter(ArrayList<Kupon> kupons, Context context,Boolean kuponSaya)
   {
       this.kupons = kupons;
       this.context = context;
       this.kuponSaya = kuponSaya;
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kupon, null);
        return new ListKuponAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(kupons.get(position).getUrl()).fit().into(holder.imgKupon);
        holder.namaKupon.setText(kupons.get(position).getNamakupon());

        if (kuponSaya){
            holder.imgKupon.setEnabled(false);
        }else{
            holder.imgKupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PenukaranKupon.class);
                    intent.putExtra(PenukaranKupon.KUPON_CLASS,(Parcelable)kupons.get(position));
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return kupons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       @BindView(R.id.img_kupon)
        ImageView imgKupon;
       @BindView(R.id.nama_kupon)
        TextView namaKupon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }




}
