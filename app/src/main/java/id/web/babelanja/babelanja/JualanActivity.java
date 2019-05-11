package id.web.babelanja.babelanja;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class JualanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jualan);

        getSupportActionBar().setTitle("Jualan Saya");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
