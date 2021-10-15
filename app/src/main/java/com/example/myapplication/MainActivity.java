package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSignIn(View view) {
        EditText userName = findViewById(R.id.editTextUsername);
        EditText password = findViewById(R.id.editTextPassword);

        TextView loginInfo = findViewById(R.id.textLoginInfo);
        loginInfo.setText(userName.getText() + " " + password.getText());
    }
}