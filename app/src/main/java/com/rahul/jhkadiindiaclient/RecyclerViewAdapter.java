package com.rahul.jhkadiindiaclient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public RecyclerViewAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycleview_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.recTitle.setText(dataList.get(position).getProductName());
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImag);
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("productName",dataList.get(holder.getAdapterPosition()).getProductName());
                intent.putExtra("productRate",dataList.get(holder.getAdapterPosition()).getProductRate());
                intent.putExtra("dataImage",dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("discountRate",dataList.get(holder.getAdapterPosition()).getProductDiscountrate());
                intent.putExtra("productDescription",dataList.get(holder.getAdapterPosition()).getProductDescription());
                intent.putExtra("key",dataList.get(holder.getAdapterPosition()).getKey());
                Log.d("TAG", "RecycleView: key:== "+dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchlist){
        dataList = searchlist;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView recImag;
        TextView recTitle;
        CardView recCard;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recImag = itemView.findViewById(R.id.recImage);
            recCard = itemView.findViewById(R.id.recCard);
            recTitle = itemView.findViewById(R.id.rectitle);
        }
    }

}
