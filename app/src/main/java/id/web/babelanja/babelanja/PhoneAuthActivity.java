package id.web.babelanja.babelanja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    EditText phoneNumber;
    EditText kode;
    Button button_ok;
    Button button_kirim;

    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        getSupportActionBar().hide();

        phoneNumber = findViewById(R.id.tv_nomor);
        kode = findViewById(R.id.tv_kode);
        button_ok = findViewById(R.id.button_ok);
        button_kirim = findViewById(R.id.button_kirim);

        button_ok.setVisibility(View.GONE);
        kode.setVisibility(View.GONE);

        button_kirim.setOnClickListener(v -> {
            verifyPhoneNumber();
        });

        button_ok.setOnClickListener(v -> {
            verifyOTP();
        });

    }

    private void verifyPhoneNumber(){
        //Ingat pakai +6281322209955 di emulator, code nya 234765
        String number = phoneNumber.getText().toString();
        if(number.isEmpty()){
            Toast.makeText(this, "Nomor HP tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if(number.startsWith("0")){
            number = "+62" + number.substring(1);
        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Log.d("Hmm", "SMS Verification suceeded: " + phoneAuthCredential);
                        linkCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(PhoneAuthActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        Log.d("Hmm", "onVerificationFailed: " + e.getMessage());
                        e.printStackTrace();
                        phoneNumber.setVisibility(View.VISIBLE);
                        button_kirim.setVisibility(View.VISIBLE);
                        button_ok.setVisibility(View.GONE);
                        kode.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        phoneNumber.setVisibility(View.GONE);
                        button_kirim.setVisibility(View.GONE);
                        button_ok.setVisibility(View.VISIBLE);
                        kode.setVisibility(View.VISIBLE);
                        Toast.makeText(PhoneAuthActivity.this, "SMS kode telah terkirim", Toast.LENGTH_SHORT).show();
                        verificationId = s;
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        verificationId = s;
                        Log.d("Hmm", "onCodeAutoRetrievalTimeOut");
                    }
                }
        );
    }

    private void verifyOTP(){
        String token = kode.getText().toString();
        if(token.isEmpty()){
            Toast.makeText(this, "Token tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, token);
        linkCredential(credential);
    }

    private void linkCredential(PhoneAuthCredential credential){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Hmm", "SMSCredential:success");
                        Toast.makeText(PhoneAuthActivity.this, "OTP Berhasil", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(PhoneAuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Sign in failed
                        Log.d("Hmm", "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(PhoneAuthActivity.this, "Kode yang anda masukkan salah", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PhoneAuthActivity.this, "Terjadi kesalahan. Apakah nomor anda sudah pernah didaftarkan ?", Toast.LENGTH_SHORT).show();
                        }
                        phoneNumber.setVisibility(View.VISIBLE);
                        button_kirim.setVisibility(View.VISIBLE);
                        button_ok.setVisibility(View.GONE);
                        kode.setVisibility(View.GONE);
                    }
                });
    }
}
