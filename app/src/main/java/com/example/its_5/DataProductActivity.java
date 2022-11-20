package com.example.its_5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.its_5.Adapters.Product_data_adapter;
import com.example.its_5.DataClasses.ProductDataClass;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataProductActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://its-database-connection.azurewebsites.net/select_product_data.php";
    Product_data_adapter product_data_adapter;
    List<ProductDataClass> productRecordList;
    ArrayList<String> epcList;
    String username;
    RecyclerView recyclerView;
    TextView productTitle;
    ProgressBar progressBar;
    TextView lastDate;
    TextView lastStage;
    TextView lastQuantity;
    Button searchOT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.epcList = getIntent().getStringArrayListExtra("epcList");
        this.username = getIntent().getStringExtra("username");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_data_layout);
        searchOT = findViewById(R.id.btn_search_ot);
        productRecordList = new ArrayList<>();
        productTitle = findViewById(R.id.title_product_data);
        productTitle.setText(epcList.get(0));
        recyclerView = findViewById(R.id.product_history_recyclerview);
        lastDate = findViewById(R.id.last_date_data);
        lastStage = findViewById(R.id.last_stage_data);
        lastQuantity = findViewById(R.id.last_quantity_data);
        progressBar = findViewById(R.id.progress_product_data_search);
        //Activate Progress bar until all data is loaded.
        progressBar.setVisibility(View.VISIBLE);
        GetData getData = new GetData();
        getData.execute();
        searchOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchOTActivity.class);
                intent.putExtra("username",String.valueOf(username));
                intent.putStringArrayListExtra("epcList", epcList);
                DataProductActivity.this.startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), SearchProductsActivity.class);
        intent.putExtra("username",String.valueOf(username));
        intent.putExtra("epcList",String.valueOf(epcList));
        DataProductActivity.this.startActivity(intent);
        this.finish();
    }

    public class GetData {
        public void execute() {
            String product[] = new String[1];
            String fieldName[] = new String[1];
            fieldName[0] = "productName";
            if (!(epcList == null || epcList.isEmpty())) {
                product[0] = epcList.get(0);
                PutData putData = new PutData("https://its-database-connection.azurewebsites.net/select_product_data.php", "POST", fieldName, product);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        try {
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = null;
                                try {
                                    jsonObject1 = jsonArray.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ProductDataClass model = new ProductDataClass();
                                model.setDate(jsonObject1.getString("date"));
                                model.setProduct(jsonObject1.getString("epc"));
                                model.setStage(jsonObject1.getString("stage"));
                                model.setQuantity(jsonObject1.getString("quantity"));
                                productRecordList.add(model);
                                PutDataIntoRecyclerView(productRecordList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(result);
                    }
                }
            } else {
                System.out.println("No data found...");
            }

        }

    }


    private void PutDataIntoRecyclerView(List<ProductDataClass> productList){
        product_data_adapter = new Product_data_adapter(this,productList);
        Integer lastItem = productList.size() - 1;
        lastDate.setText(productList.get(lastItem).getDate());
        lastStage.setText(productList.get(lastItem).getStage());
        lastQuantity.setText(productList.get(lastItem).getQuantity());
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(product_data_adapter);
    }
}