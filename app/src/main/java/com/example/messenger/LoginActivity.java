package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private Button next_btn, accept_btn, logintitle, register_btn;
    private EditText edtPhone, edtVerification;
    private TextView tv2;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        next_btn=(Button) findViewById(R.id.login_btn_next);
        accept_btn=(Button) findViewById(R.id.login_btn_accept);
        register_btn=(Button) findViewById(R.id.register_btn);
        edtVerification=(EditText) findViewById(R.id.login_verification);
        edtPhone=(EditText) findViewById(R.id.login_phone);
        tv2=(TextView)findViewById(R.id.tv2);
        logintitle=(Button)findViewById(R.id.login_title);
        mAuth=FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next_btn.setVisibility(View.INVISIBLE);
                accept_btn.setVisibility(View.VISIBLE);
                edtVerification.setVisibility(View.VISIBLE);
                edtPhone.setVisibility(View.INVISIBLE);
                tv2.setVisibility(View.INVISIBLE);
                logintitle.setVisibility(View.INVISIBLE);
                register_btn.setVisibility(View.INVISIBLE);

                String phoneNumber = edtPhone.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(LoginActivity.this,"Введите номер телефона",Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle("Проверка номера");
                    loadingBar.setMessage("Пожалуйста подождите");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }


        });

        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verifCode=edtVerification.getText().toString();
                if(TextUtils.isEmpty(verifCode)){
                    Toast.makeText(LoginActivity.this, "Введите код", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.setTitle("Проверка кода");
                    loadingBar.setMessage("Пожалуйста подождите");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verifCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(registerIntent);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();

                Toast.makeText(LoginActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                next_btn.setVisibility(View.VISIBLE);
                accept_btn.setVisibility(View.INVISIBLE);
                edtVerification.setVisibility(View.INVISIBLE);
                edtPhone.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                logintitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();

                Toast.makeText(LoginActivity.this, "Код отправлен", Toast.LENGTH_SHORT).show();

                next_btn.setVisibility(View.INVISIBLE);
                accept_btn.setVisibility(View.VISIBLE);
                edtVerification.setVisibility(View.VISIBLE);
                edtPhone.setVisibility(View.INVISIBLE);
                tv2.setVisibility(View.INVISIBLE);
                logintitle.setVisibility(View.INVISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Проверка успешна", Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Ошибка проверки номера", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}