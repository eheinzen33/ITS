package com.example.its_5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

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
import java.util.List;

import a.a.a.a.e;

public class DataProductActivity extends AppCompatActivity {
    private static String JSON_URL = "https://its-database-connection.azurewebsites.net/select_product_data.php";
    Product_data_adapter product_data_adapter;
    List<ProductDataClass> productRecordList;
    RecyclerView recyclerView;
    TextView productTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ArrayList<String> epcList = getIntent().getStringArrayListExtra("epcList");
        final String username = getIntent().getStringExtra("username");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_data_layout);
        productRecordList = new ArrayList<>();
        productTitle = findViewById(R.id.title_product_data);
        productTitle.setText(epcList.get(0));
        recyclerView = findViewById(R.id.product_history_recyclerview);
        GetData getData = new GetData();
        getData.execute();


//        setContentView(R.layout.product_data_layout);
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                String product[] = new String[1];
//                String fieldName[] = new String[1];
//                fieldName[0] = "productName";
//                if (!(epcList == null || epcList.isEmpty())){
//                    product[0] = epcList.get(0);
//                    PutData putData = new PutData("https://its-database-connection.azurewebsites.net/select_product_data.php","POST",fieldName,product);
//                    if (putData.startPut()) {
//                        if (putData.onComplete()) {
//                            String result = putData.getResult();
//                            System.out.println(result);
//                        }
//                    }
//                }else {System.out.println("No data found...");}
//
//            }
//        });

    }

    public class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String product[] = new String[1];
                String fieldName[] = new String[1];
                fieldName[0] = "productName";
                if (!(productRecordList == null || productRecordList.isEmpty())){
                    product[0] = productRecordList.get(0).getProduct();
                    PutData putData = new PutData("https://its-database-connection.azurewebsites.net/select_product_data.php","POST",fieldName,product);
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
                                for (int i = 0 ; i< jsonArray.length() ; i++){
                                    JSONObject jsonObject1 = null;
                                    try {
                                        jsonObject1 = jsonArray.getJSONObject(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ProductDataClass model = new ProductDataClass();
                                    model.setDate(jsonObject1.getString("date"));
                                    model.setProduct(jsonObject1.getString("epc"));
                                    model.setProduct(jsonObject1.getString("stage"));
                                    model.setProduct(jsonObject1.getString("quantity"));
                                    productRecordList.add(model);
                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(result);
                            }
                        }
                }else {System.out.println("No data found...");}

            }
        });


            PutDataIntoRecyclerView(productRecordList);


            return null;
        }
    }

    private void PutDataIntoRecyclerView(List<ProductDataClass> productList){
        product_data_adapter = new Product_data_adapter(this,productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(product_data_adapter);
    }
}