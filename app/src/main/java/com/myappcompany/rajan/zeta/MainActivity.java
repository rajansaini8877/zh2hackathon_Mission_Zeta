package com.myappcompany.rajan.zeta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String PRIMARY_ACCOUNT = "0cc5e52d-5a7b-459c-8a3a-8d89afd18baf";
    public static final String ZETA_BUSINESS_ACCOUNT = "5d79ad83-de70-4322-a488-3cfe78c33cae";
    public static final String VENDOR_ACCOUNT = "59539cd4-8354-4248-9a2d-6eaf48d7e7a9";

    public static final String VBO_NAME = "savinest";
    public static final String VBO_PHONE_NUMBER = "917903508365";
    public static final String IFI_ID = "140793";
    public static final String IAH_ID = "3e0e1f80-ff7d-4fb1-a305-c11c7dbf58b7";
    public static final String VBO_ID = "b44d6d37-a709-4963-898a-242bc56a6f35";
    public static final String BUNDLE_ID = "b67c8e9d-cbc5-4619-b47f-0329c1f9ff4b";
    public static final String FUNDING_ACCOUNT_ID = "ca550416-85aa-43ad-98c1-a2c8f9437e3b";
    public static final String SANDBOX_ID = "11525";
    public static final String AUTH_TOKEN = "eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwidGFnIjoiT29ISEtheVZEaUFyMzNIS3dRNXF4dyIsImFsZyI6IkExMjhHQ01LVyIsIml2IjoiQ1pUbDczOW5NRTFUWHNEciJ9.y-vkKQMQ2ZU_9N85vF-qxQOeq3lf_GPmGVAegu9foG0.whAkBqImgQBea22Ejm6HGA.5i3mvFtJzp7Li_x4xmneyGCrr_Z9SiRKaTZ9hIduiesntNe1Y05NCsgqA84lrol7cdbtosSzrQNSkPmxifXSfITxJE73C0X8zZzrgvuV3yn-uKbOQcIwIMBNkM5d_0ZnmLVw9z4mTzB7mJqOJsfZJ9qnBnYZBxTug6B52tk_TdP_A6UI0HKAP_8b8IQsE5L36Dq1Vcb0oioyzrQURL_dTtIE0HGBeWiiUzzEpXeuOxJdlTabzePhDTNQcvS5TgfK1g5TSyGOJ0yGIsRgF1GZpQGMeblpG8LsV6nANXEbN6paDM8kAGaABnQkc5KDXIHSu9eXaRO5Bo60UkZ1wPoPQa75yGZq5mn5akaVtPxMlE1utUXFfgiXpp-iwGe_-LWN.DIqfQYQbh6esyRFJyzwkyw";

    private boolean isLoggedIn = false;

    public static boolean isPinFormatted(String pin) {
        Pattern pattern = Pattern.compile("\\d{6}");
        Matcher matcher = pattern.matcher(pin);

        if(matcher.matches()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLoggedIn) {
                    //Home screen
                }
                else {
                    Intent i = EntryActivity.newIntent(MainActivity.this);
                    startActivity(i);
                }
            }
        }, 3000);
    }
}