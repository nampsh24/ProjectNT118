package com.example.projectnt118;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btn_signup, btn_signin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_signup = findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale);
                v.startAnimation(animation);
                Toast.makeText(MainActivity.this, "Sign Up", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        btn_signin = findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale);
                v.startAnimation(animation);
                Toast.makeText(MainActivity.this, "Sign In", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }
}