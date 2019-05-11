package id.web.babelanja.babelanja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.UUID;

import timber.log.Timber;

public class BantuanActivity extends AppCompatActivity {

    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bantuan");

        imageButton = findViewById(R.id.imageButton);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("toko").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       DocumentSnapshot document = task.getResult();
                       Glide.with(BantuanActivity.this)
                               .load(document.getString("foto"))
                               .centerInside()
                               .into(imageButton);

                   }
                });

        imageButton.setOnClickListener(v -> {
            ImagePicker.create(this)
                    .theme(R.style.AppThemeNoActionBar)
                    .single()
                    .start();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(ImagePicker.shouldHandle(requestCode, resultCode, data)){
            Image image = ImagePicker.getFirstImageOrNull(data);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReference().child("asd").putFile(Uri.fromFile(new File(image.getPath())));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
