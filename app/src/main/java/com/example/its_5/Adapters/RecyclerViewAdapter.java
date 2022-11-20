package com.example.its_5.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.its_5.DataClasses.TAG;
import com.example.its_5.R;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MultiViewHolder> {



    //1 MultiAdapter Class with implementations of its methods
    private Context context;
    private ArrayList<TAG> EPCList;

    public RecyclerViewAdapter(Context context, ArrayList<TAG> EPCList) {
        this.context = context;
        this.EPCList = EPCList;

    }

    public void setTags(ArrayList<TAG> EPCList){
        //this.EPCList = new ArrayList<>();
        this.EPCList = new ArrayList<>();
        this.EPCList = EPCList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MultiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_epc,parent,false);
        return new MultiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiViewHolder holder, int position) {
        holder.bind(EPCList.get(position));
    }

    @Override
    public int getItemCount() {
        return EPCList.size();
    }
//2 Viewholder Class

    class MultiViewHolder extends RecyclerView.ViewHolder{
        private TextView Product;
        private TextView Quantity;
        public MultiViewHolder (@NonNull View itemView){
            super(itemView);
            Product = itemView.findViewById(R.id.Product);
            Quantity = itemView.findViewById(R.id.Quantity);
        }
        // Getting the selected items
        void bind(final TAG epc){
            if (epc.IsChecked()){Product.setBackgroundColor(Color.RED);} else {Product.setBackgroundColor(Color.TRANSPARENT);}
            Product.setText((String) epc.getProduct());
            Quantity.setText(String.valueOf(epc.getQuantity()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!epc.IsChecked()){
                        //Quantity.setTextColor(Color.RED);
                        Product.setBackgroundColor(Color.RED);
                        epc.setChecked();
                    } else  {
                        epc.uncheck();
                        //Quantity.setTextColor(Color.BLACK);
                        Product.setBackgroundColor(Color.TRANSPARENT);
                    }

                }
            });
        }
    }


    // getting All Items Selected

    public ArrayList<TAG> getAll(){return EPCList;}

    //Getting seleted when btn clicked

    public ArrayList<TAG> getSeletected(){
        ArrayList<TAG> selectedItems = new ArrayList<>();
        for (int i=0; i<EPCList.size(); i++){
            if (EPCList.get(i).IsChecked()){
                selectedItems.add(EPCList.get(i));
            }
        }
        return selectedItems;
    }











}