package com.example.its_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class SearchOTActivity extends AppCompatActivity {
    ArrayList<String> epcList;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.epcList = getIntent().getStringArrayListExtra("epcList");
        this.username = getIntent().getStringExtra("username");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_otactivity);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DataProductActivity.class);
        intent.putExtra("username",String.valueOf(username));
        intent.putStringArrayListExtra("epcList", epcList);
        SearchOTActivity.this.startActivity(intent);
        this.finish();
    }

}