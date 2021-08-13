package com.myappcompany.rajan.zeta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class PrimaryUserLoginActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mSubmitButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ProgressDialog mProgressDialog;

    public static Intent newIntent(Context context) {
        return new Intent(context, PrimaryUserLoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_user_login);

        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mSubmitButton = findViewById(R.id.submit_button);
        mProgressDialog = new ProgressDialog(PrimaryUserLoginActivity.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(PrimaryUserLoginActivity.this, "Both fields are mandatory!", Toast.LENGTH_LONG).show();
                    return;
                }

                login(email, password);
            }
        });
    }

    private void login(String email, String password) {

        mProgressDialog.show();

        db.collection("primary_users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(PrimaryUserLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {
                            authenticate(email, password);
                        }
                    }
                });
    }

    private void authenticate(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if(mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }

                        if(task.isSuccessful()) {
                            Toast.makeText(PrimaryUserLoginActivity.this, "Logged in!", Toast.LENGTH_LONG).show();
                            Intent i = PrimaryUserHomeActivity.newIntent(PrimaryUserLoginActivity.this);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(PrimaryUserLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}