package com.example.its_5;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class SignUp extends AppCompatActivity {

    TextInputEditText textImputfullname, textImputemail, textImputusername, textImputpassword;
    Button buttonSignUp;
    TextView textViewLogIn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textImputfullname = findViewById(R.id.fullname);
        textImputemail = findViewById(R.id.email);
        textImputusername = findViewById(R.id.username);
        textImputpassword = findViewById(R.id.password);
        buttonSignUp = findViewById(R.id.btnSignUp);
        textViewLogIn = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);

        textViewLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname, email, username, password;
                fullname = String.valueOf(textImputfullname.getText());
                email = String.valueOf(textImputemail.getText());
                username = String.valueOf(textImputusername.getText());
                password = String.valueOf(textImputpassword.getText());
                if (!fullname.equals("") && !email.equals("") && !username.equals("") && !password.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[4];
                            field[0] = "fullname";
                            field[1] = "email";
                            field[2] = "username";
                            field[3] = "password";
                            //Creating array for data
                            String[] data = new String[4];
                            data[0] = fullname;
                            data[1] = email;
                            data[2] = username;
                            data[3] = password;
                            PutData putData = new PutData("https://its-database-connection.azurewebsites.net/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if (result.equals("Sign Up Success")) {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        System.out.println(result);
                                        Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }


}