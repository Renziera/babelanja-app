package id.web.babelanja.babelanja;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        Timber.plant(new Timber.DebugTree());

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            if(user == null){
                Intent intent;
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }else{
                String uid = user.getUid();
                Timber.d("UID: %s", uid);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("toko").document(uid).get()
                        .addOnCompleteListener(task -> {
                           if(task.isSuccessful()){
                               Intent intent;
                               DocumentSnapshot document = task.getResult();
                               if(document.exists()){
                                   intent = new Intent(SplashActivity.this, MainActivity.class);
                               }else{
                                   intent = new Intent(SplashActivity.this, PhoneAuthActivity.class);
                               }
                               startActivity(intent);
                               finish();
                           }else{
                               Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                               finish();
                           }
                        });
            }
        }, 100);
    }
}
