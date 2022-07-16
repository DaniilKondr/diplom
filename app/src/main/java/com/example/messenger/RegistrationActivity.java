package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;
    private EditText edtEmail,edtPassword;
    private TextView tvChange;
    ProgressDialog loadingBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnLogin=(Button) findViewById(R.id.login_btn);
        btnRegister=(Button) findViewById(R.id.register2_btn);
        edtEmail=(EditText) findViewById(R.id.email_input);
        edtPassword=(EditText)findViewById(R.id.password_input);
        tvChange=(TextView) findViewById(R.id.changeTV);

        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();

//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;



        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvChange.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
            }
        });
        
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInAccount();
            }
        });

    }

    private void signInAccount() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Введите Email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Вход в аккаунт");
            loadingBar.setMessage("Пожалуйста подождите");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){


                                Intent intent =new Intent(RegistrationActivity.this,MainActivity.class);
                                startActivity(intent);

                                loadingBar.dismiss();
                                Toast.makeText(RegistrationActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this, "Ошибка "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void createAccount() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Введите Email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста подождите");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Intent intent =new Intent(RegistrationActivity.this,MainActivity.class);
                                startActivity(intent);

                                Toast.makeText(RegistrationActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();

                                loadingBar.dismiss();
                                finish();

                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this, "Ошибка "+message, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

    }
}