package id.web.babelanja.babelanja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class JualanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jualan);

        getSupportActionBar().setTitle("Jualan Saya");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.button_tambah).setOnClickListener(v -> {
            Intent intent = new Intent(this, TambahJualanActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
