package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class loginActivity extends AppCompatActivity {
    EditText usernametext,passwordtext;
    Button loginbutton;
    String PREF_USER_NAME = "USER",  PREF_USER_PASS = "PASS";
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        String username =  PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_USER_NAME,"");
        String password =  PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_USER_PASS , "");
        Toast.makeText(this, username+"  " +password, Toast.LENGTH_SHORT).show();

        usernametext = findViewById(R.id.user_edit);
        passwordtext = findViewById(R.id.pass_edit);
        loginbutton = findViewById(R.id.login_butto);
        textView = findViewById(R.id.sign_up);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkuser(usernametext.getText().toString(), passwordtext.getText().toString());
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this , Sign_Up.class);
                startActivity(intent);
            }
        });




    }

    void checkuser(String user , String pass)
    {
        String username =  PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_USER_NAME, "");
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_USER_PASS, "");
        if(user.equals("abdo")  && pass.equals("123")) {
            usernametext.setText("");
            passwordtext.setText("");
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(loginActivity.this,MainActivity.class);
            intent.putExtra("user",user);
            intent.putExtra("pass",pass);
            startActivity(intent);
        }
        else if(user.equals(username )&& pass.equals(password))
        {
            usernametext.setText("");
            passwordtext.setText("");
            Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(loginActivity.this , MainActivity.class);
            intent.putExtra("user",user);
            intent.putExtra("pass",pass);
            startActivity(intent);
        }
        else if(user.isEmpty()&& pass.isEmpty()){

            usernametext.setError("Username is Empty");
            passwordtext.setError("Password is Empty");
            Toast.makeText(this, "Username and Password  is Empty", Toast.LENGTH_SHORT).show();
        } else if(user.isEmpty()){
            usernametext.setError("Username is Empty");
            Toast.makeText(this, "Username  is Empty", Toast.LENGTH_SHORT).show();
        }else if(pass.isEmpty()){
            passwordtext.setError("Password is Empty");
            Toast.makeText(this, "Password is Empty", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "UserName or Password Wrong", Toast.LENGTH_SHORT).show();
        }
    }
}