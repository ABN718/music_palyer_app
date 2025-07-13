//package com.example.musicplayer;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class Sign_Up extends AppCompatActivity {
//EditText ed_pass , ed_user , ed_phone;
//Button butt_signup;
//TextView text_signin;
//String PREF_USER_NAME = "USER",  PREF_USER_PASS = "PASS";
//SharedPreferences.Editor sharedPer;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign__up);
//        getSupportActionBar().hide();
//
//        ed_pass = findViewById(R.id.pass_edit);
//        ed_phone = findViewById(R.id.phone_edit);
//        ed_user = findViewById(R.id.user_edit);
//        butt_signup = findViewById(R.id.signup_butto);
//        text_signin = findViewById(R.id.text_in);
//        text_signin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Sign_Up.this , loginActivity.class);
//                startActivity(intent);
//            }
//        });
//        butt_signup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(cheek_info(ed_user.getText().toString() , ed_pass.getText().toString() , ed_phone.getText().toString()))
//                {
//                    Toast.makeText(Sign_Up.this, "Welcome to our Application", Toast.LENGTH_SHORT).show();
//                    ed_pass.setText("");
//                    ed_user.setText("");
//                    ed_phone.setText("");
//                }
//            }
//        });
//
//
//    }
//    public boolean cheek_info(String user , String pass , String phone)
//    {
//        if(user.isEmpty()&& pass.isEmpty() && phone.isEmpty() )
//        {
//
//            ed_user.setError("Username is Empty");
//            ed_pass.setError("Password is Empty");
//            ed_phone.setError("phone is Empty");
//            Toast.makeText(this, "Username and Password  is Empty", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        else if(user.isEmpty())
//        {
//            ed_user.setError("Username is Empty");
//            Toast.makeText(this, "Username  is Empty", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        else if(pass.isEmpty())
//        {
//            ed_pass.setError("Password is Empty");
//            Toast.makeText(this, "Password is Empty", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else if(phone.isEmpty())
//        {
//            ed_phone.setError("phone is Empty");
//            Toast.makeText(this, "phone is Empty", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else{
//            PreferenceManager.getDefaultSharedPreferences(this).edit()
//                    .putString(PREF_USER_NAME, user)
//                    .putString(PREF_USER_PASS, pass)
//                    .apply();
//            return true;
//        }
//
//
//    }
//
//}