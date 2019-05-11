package id.web.babelanja.babelanja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
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
    TextView tv_nomorPreview;

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

        if(getIntent().getBooleanExtra("edit", false)){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setTitle("Verifikasi");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        phoneNumber = findViewById(R.id.tv_phoneNumber);
        kode = findViewById(R.id.tv_kode);
        button_lanjut = findViewById(R.id.button_lanjut);
        button_kirim = findViewById(R.id.button_kirim);
        button_kirimUlang = findViewById(R.id.button_kirimUlang);
        isi_kode = findViewById(R.id.isi_kode);
        isi_nomor = findViewById(R.id.isi_nomor);
        tv_nomorPreview = findViewById(R.id.tv_nomorPreview);

        isi_nomor.setVisibility(View.VISIBLE);
        isi_kode.setVisibility(View.GONE);

        button_kirimUlang.setOnClickListener(v -> {
            isi_nomor.setVisibility(View.VISIBLE);
            isi_kode.setVisibility(View.GONE);
            button_kirim.setEnabled(true);
            button_lanjut.setEnabled(true);
        });

        button_kirim.setOnClickListener(v -> {
            verifyPhoneNumber();
            button_kirim.setEnabled(false);
        });

        button_lanjut.setOnClickListener(v -> {
            verifyOTP();
            button_lanjut.setEnabled(false);
        });

    }

    private void verifyPhoneNumber(){
        //Ingat pakai +6281322209955 di emulator, code nya 123456
        String number = phoneNumber.getText().toString();
        if(number.isEmpty()){
            Toast.makeText(this, "Nomor telepon tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if(number.startsWith("0")){
            number = "+62" + number.substring(1);
        }

        nomor = number;
        tv_nomorPreview.setText("Kode verifikasi telah dikirim melalui SMS ke nomor\n" + nomor);
        kode.setText("");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Timber.d("SMS Verification suceeded: %s", phoneAuthCredential);
                        updateDatabase();
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(PhoneAuthActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        Timber.d("onVerificationFailed: %s", e.getMessage());
                        e.printStackTrace();
                        isi_nomor.setVisibility(View.VISIBLE);
                        isi_kode.setVisibility(View.GONE);
                        button_kirim.setEnabled(true);
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        isi_nomor.setVisibility(View.GONE);
                        isi_kode.setVisibility(View.VISIBLE);
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
                        firebaseAuth.getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID);
                        updateDatabase();
                    } else {
                        // Sign in failed
                        Timber.d(task.getException(), "signInWithCredential:failure");
                        Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        isi_nomor.setVisibility(View.VISIBLE);
                        isi_kode.setVisibility(View.GONE);
                        button_kirim.setEnabled(true);
                        button_lanjut.setEnabled(true);
                    }
                });
    }

    private void updateDatabase(){
        Toast.makeText(PhoneAuthActivity.this, "OTP Berhasil", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(getIntent().getBooleanExtra("edit", false)){
            db.collection("toko")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .update("telepon", nomor,
                            "timestamp_update_phone", FieldValue.serverTimestamp())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(this, "Berhasil mengubah nomor telepon", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Timber.d(task.getException());
                            Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                            isi_nomor.setVisibility(View.VISIBLE);
                            isi_kode.setVisibility(View.GONE);
                            button_kirim.setEnabled(true);
                            button_lanjut.setEnabled(true);
                        }
                    });
        }else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Map<String, Object> data = new HashMap<>();
            data.put("nama", user.getDisplayName());
            data.put("email", user.getEmail());
            data.put("telepon", nomor);
            data.put("foto", user.getPhotoUrl().toString());
            data.put("timestamp_sign_up", FieldValue.serverTimestamp());

            db.collection("toko")
                    .document(user.getUid())
                    .set(data)
                    .addOnCompleteListener(task -> {
                       if(task.isSuccessful()){
                           Intent intent = new Intent(PhoneAuthActivity.this, ProfilActivity.class);
                           startActivity(intent);
                           finish();
                       }else{
                           Timber.d(task.getException());
                           Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                           isi_nomor.setVisibility(View.VISIBLE);
                           isi_kode.setVisibility(View.GONE);
                           button_kirim.setEnabled(true);
                           button_lanjut.setEnabled(true);
                       }
                    });
        }

    }
}
