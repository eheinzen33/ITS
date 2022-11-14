package com.example.its_5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Product_data_adapter extends RecyclerView.Adapter<Product_data_adapter.MyViewHolder> {
    private Context mContext;
    private List<ProductDataClass> mData;

    public Product_data_adapter(Context mContext, List<ProductDataClass> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public Product_data_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.product_data_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Product_data_adapter.MyViewHolder holder, int position) {

        ProductDataClass productRecord;
        productRecord = mData.get(position);

        holder.date.setText(productRecord.getDate());
        holder.product.setText(productRecord.getProduct());
        holder.stage.setText(productRecord.getStage());
        holder.quantity.setText(productRecord.getQuantity());




    }

    @Override
    public int getItemCount() {return mData.size();}

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView product;
        TextView stage;
        TextView quantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_row);
            product = itemView.findViewById(R.id.product_data_row);
            stage = itemView.findViewById(R.id.stage_row);
            quantity = itemView.findViewById(R.id.quantity_row);
        }
    }
}
