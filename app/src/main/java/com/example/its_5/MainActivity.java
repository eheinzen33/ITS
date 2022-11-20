package com.example.its_5;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String username = getIntent().getStringExtra("username");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton showProductsBtn = findViewById(R.id.btn_rfid);
        Button SearchTagBtn = findViewById(R.id.btn_rfidRead);

        showProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        FetchData fetchData = new FetchData("https://its-database-connection.azurewebsites.net/select_products.php");
//                        if (fetchData.startFetch()) {
//                            if (fetchData.onComplete()) {
//                                String result = fetchData.getResult();
//                                System.out.println(result.getClass());
//                                System.out.println(result);
//                                //End ProgressBar (Set visibility to GONE)
//                            }
//                        }
//                    }
//                });
                Intent intent = new Intent(getApplicationContext(), SearchProductsActivity.class);
                intent.putExtra("username",username);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });

        SearchTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReadEPCActivity.class);
                //intent.putExtra("username",username);
                MainActivity.this.startActivity(intent);
                finish();

            }
        });






    }
}