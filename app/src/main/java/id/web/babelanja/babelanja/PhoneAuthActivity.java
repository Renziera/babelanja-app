package id.web.babelanja.babelanja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class PhoneAuthActivity extends AppCompatActivity {

    EditText phoneNumber;
    EditText kode;
    Button button_lanjut;
    Button button_kirim;
    Button button_kirimUlang;
    View isi_nomor;
    View isi_kode;

    String verificationId;
    String nomor;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verifikasi");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        phoneNumber = findViewById(R.id.tv_phoneNumber);
        kode = findViewById(R.id.tv_kode);
        button_lanjut = findViewById(R.id.button_lanjut);
        button_kirim = findViewById(R.id.button_kirim);

        button_lanjut.setVisibility(View.GONE);
        kode.setVisibility(View.GONE);

        button_kirim.setOnClickListener(v -> {
            verifyPhoneNumber();
        });

        button_lanjut.setOnClickListener(v -> {
            verifyOTP();
        });

    }

    private void verifyPhoneNumber(){
        //Ingat pakai +6281322209955 di emulator, code nya 123456
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
                        Timber.d("SMS Verification suceeded: %s", phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(PhoneAuthActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        Timber.d("onVerificationFailed: %s", e.getMessage());
                        e.printStackTrace();
                        phoneNumber.setVisibility(View.VISIBLE);
                        button_kirim.setVisibility(View.VISIBLE);
                        button_lanjut.setVisibility(View.GONE);
                        kode.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        phoneNumber.setVisibility(View.GONE);
                        button_kirim.setVisibility(View.GONE);
                        button_lanjut.setVisibility(View.VISIBLE);
                        kode.setVisibility(View.VISIBLE);
                        Toast.makeText(PhoneAuthActivity.this, "SMS kode telah terkirim", Toast.LENGTH_SHORT).show();
                        verificationId = s;
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        verificationId = s;
                        Timber.d("onCodeAutoRetrievalTimeOut");
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
                        Timber.d("SMSCredential:success");
                        Toast.makeText(PhoneAuthActivity.this, "OTP Berhasil", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(PhoneAuthActivity.this, MainActivity.class);
                        firebaseAuth.getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID);
                        startActivity(intent);
                        finish();
                    } else {
                        // Sign in failed
                        Timber.d(task.getException(), "signInWithCredential:failure");
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(PhoneAuthActivity.this, "Kode yang anda masukkan salah", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PhoneAuthActivity.this, "Terjadi kesalahan. Apakah nomor anda sudah pernah didaftarkan ?", Toast.LENGTH_SHORT).show();
                        }
                        phoneNumber.setVisibility(View.VISIBLE);
                        button_kirim.setVisibility(View.VISIBLE);
                        button_lanjut.setVisibility(View.GONE);
                        kode.setVisibility(View.GONE);
                    }
                });
    }

    private void updateDatabase(String phoneNumber){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("toko")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("telepon", phoneNumber,
                        "email", FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        "timestamp_join", FieldValue.serverTimestamp())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(PhoneAuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
