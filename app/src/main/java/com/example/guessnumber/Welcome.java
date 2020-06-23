package com.example.guessnumber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Welcome extends AppCompatActivity {
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        view= findViewById(R.id.welcome);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMain();
            }
        });
    }
    private void gotoMain(){

        //從這裡跳到MainAct的類別 （因為還沒有物件實體 所以寫類別）
        Intent intent = new Intent(this,MainActivity.class);
        //用intent 去startActivity
        startActivity(intent);
        finish();

    }
}
