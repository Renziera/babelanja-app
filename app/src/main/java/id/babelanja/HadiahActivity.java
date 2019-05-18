package id.babelanja;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class HadiahActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hadiah);

        getSupportActionBar().setTitle("Hadiah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
