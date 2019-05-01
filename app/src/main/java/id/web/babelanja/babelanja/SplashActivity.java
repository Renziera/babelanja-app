package id.web.babelanja.babelanja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        Timber.plant(new Timber.DebugTree());

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            Intent intent;
            if(user == null){
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }else{
                String phoneNumber = user.getPhoneNumber();
                if("".equals(phoneNumber) || phoneNumber == null || "null".equals(phoneNumber)){
                    intent = new Intent(SplashActivity.this, PhoneAuthActivity.class);
                }else{
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
            }
            startActivity(intent);
            finish();
        }, 500);
    }
}
