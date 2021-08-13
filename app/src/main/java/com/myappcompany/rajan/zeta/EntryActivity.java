package com.myappcompany.rajan.zeta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EntryActivity extends AppCompatActivity {

    private Button mPrimaryUserButton;
    private Button mSecondaryUserButton;

    public static Intent newIntent(Context context) {
        return new Intent(context, EntryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        mPrimaryUserButton = findViewById(R.id.primary_user_button);
        mSecondaryUserButton = findViewById(R.id.secondary_user_button);

        mPrimaryUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = PrimaryUserLoginActivity.newIntent(EntryActivity.this);
                startActivity(i);
            }
        });

        mSecondaryUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = SecondaryUserLoginActivity.newIntent(EntryActivity.this);
                startActivity(i);
            }
        });
    }
}