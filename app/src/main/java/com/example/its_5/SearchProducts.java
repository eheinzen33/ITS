package com.example.its_5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchProducts extends AppCompatActivity {

    //JSON Link to the data.
    private static String JSON_URL = "https://its-database-connection.azurewebsites.net/select_products.php";
    Product_adapter adapter;
    List<ProductClass> productList;
    RecyclerView recyclerView;
    Button btnStart;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        searchView  = findViewById(R.id.searchBarProducts);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });
        productList = new ArrayList<>();
        recyclerView = findViewById(R.id.p_recyclerView);
        btnStart = findViewById(R.id.btn_searchP);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetData getData = new GetData();
                getData.execute();

            }
        });

    }

    private void filterList(String s) {


        List<ProductClass> filterList = new ArrayList<>();
        for (ProductClass product : productList){
            if (product.getProduct().toLowerCase().contains(s.toLowerCase())){
                filterList.add(product);

            }
        }
        if (filterList.isEmpty()){
            Toast.makeText(this, "No product found",Toast.LENGTH_SHORT).show();
        }else {
            adapter.setFilteredList(filterList);

        }
    }

    public class GetData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            String current = "";

            try {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();


                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while (data !=-1){
                        current += (char) data;
                        data = isr.read();
                    }

                    return current;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null){
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return current;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                //JSONObject jsonObject = new JSONObject(s);
                //JSONArray jsonArray = jsonObject.getJSONArray("products");
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0 ; i< jsonArray.length() ; i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    ProductClass model = new ProductClass();

                    model.setUser_ID(jsonObject1.getString("User_ID"));
                    model.setEpc(jsonObject1.getString("epc"));
                    model.setProduct(jsonObject1.getString("product_name"));

                    productList.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PutDataIntoRecyclerView(productList);


        }
    }

    private void PutDataIntoRecyclerView(List<ProductClass> productList){
        adapter = new Product_adapter(this,productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
    }


}