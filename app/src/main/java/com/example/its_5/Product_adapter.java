package com.example.its_5;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class Product_adapter extends RecyclerView.Adapter<Product_adapter.MyViewHolder> {

    private Context mContext;
    private List<ProductClass> mData;
    private List<ProductClass> mDataSelected = new ArrayList<>();

    public void setFilteredList(List<ProductClass> filteredList){
        this.mData = filteredList;
        notifyDataSetChanged();
    }

    public Product_adapter(Context mContext, List<ProductClass> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.product_epc_layout, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductClass product;
        product = mData.get(position);
        //Checking Recycler View
        if (!(mDataSelected.isEmpty())){
            if (mDataSelected.contains(product)){holder.Product.setBackgroundColor(Color.BLUE);}
            else {holder.Product.setBackgroundColor(Color.TRANSPARENT);}
        }else {holder.Product.setBackgroundColor(Color.TRANSPARENT);}

        holder.epc.setText(mData.get(position).getEpc());
        holder.Product.setText(mData.get(position).getProduct());
        holder.Product.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!(product.getProduct() == (String) holder.Product.getText())){return true;}
                ColorDrawable Colorcheck = (ColorDrawable) holder.Product.getBackground();
                if( Colorcheck.getColor() == Color.BLUE ){
                    holder.Product.setBackgroundColor(Color.TRANSPARENT);
                    mDataSelected.remove(product);
                    return false;
                }else {
                    holder.Product.setBackgroundColor(Color.BLUE);
                    mDataSelected.add(product);
                }
                return true;
            }
        });
//        holder.Product.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!(product.getProduct() == (String) holder.Product.getText())){return;}
//                ColorDrawable Colorcheck = (ColorDrawable) holder.Product.getBackground();
//                if( Colorcheck.getColor() == Color.BLUE ){
//                    holder.Product.setBackgroundColor(Color.TRANSPARENT);
//                    mDataSelected.remove(product);
//                    return;
//                }else {
//                    holder.Product.setBackgroundColor(Color.BLUE);
//                    mDataSelected.add(product);
//                }
//
//            }
//        });
        // Using Glide library to display image if yo want


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Product;
        TextView epc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Product = itemView.findViewById(R.id.product_textview);
            epc = itemView.findViewById(R.id.epc_textview);


        }
    }

    public List<ProductClass> getSelectedItems(){
        return this.mDataSelected;
    }
}
