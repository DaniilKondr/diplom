package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button btn_saveInfo;
    private EditText edt_userName, edt_status;
    private CircleImageView circleImageView;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btn_saveInfo=(Button) findViewById(R.id.save_user_information_btn);
        edt_userName=(EditText) findViewById(R.id.set_user_name);
        edt_status=(EditText) findViewById(R.id.set_user_status);
        circleImageView=(CircleImageView) findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        currentUserID= mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        btn_saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateInfo();
            }
        });

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.hasChild("name")){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveUserStatus = snapshot.child("status").getValue().toString();

                            edt_userName.setText(retrieveUserName);
                            edt_status.setText(retrieveUserStatus);
                        }
                        else{
                            Toast.makeText(SettingsActivity.this, "Введите свое имя", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void UpdateInfo() {
        String setName = edt_userName.getText().toString();
        String setStatus = edt_status.getText().toString();
        
        if(TextUtils.isEmpty(setName)){
            Toast.makeText(this, "Введиет имя", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(setStatus)){
            Toast.makeText(this, "Введиет статус", Toast.LENGTH_SHORT).show();
        }

        else{
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setName);
            profileMap.put("status",setStatus);

            rootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SettingsActivity.this, "Информация обновлена", Toast.LENGTH_SHORT).show();

                                Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
                                startActivity(mainIntent);
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Ошибка " +message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}