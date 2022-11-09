package com.example.its_5;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button showProductsBtn = findViewById(R.id.buttonProducts);

        showProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FetchData fetchData = new FetchData("https://its-database-connection.azurewebsites.net/select_products.php");
                        if (fetchData.startFetch()) {
                            if (fetchData.onComplete()) {
                                String result = fetchData.getResult();
                                System.out.println(result.getClass());
                                System.out.println(result);
                                //End ProgressBar (Set visibility to GONE)
                            }
                        }
                    }
                });
            }
        });







    }
}