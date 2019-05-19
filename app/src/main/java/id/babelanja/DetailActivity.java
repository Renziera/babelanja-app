package id.babelanja;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    private ImageView[] imageViews = new ImageView[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setTitle("Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View.OnClickListener listener = v -> Toast.makeText(this, "Belum tersedia", Toast.LENGTH_SHORT).show();
        findViewById(R.id.button_kirim_pesan).setOnClickListener(listener);
        findViewById(R.id.button_beli).setOnClickListener(listener);

        TextView tv_harga = findViewById(R.id.tv_harga);
        TextView tv_deskripsi = findViewById(R.id.tv_deskripsi);

        imageViews[0] = findViewById(R.id.imageView1);
        imageViews[1] = findViewById(R.id.imageView2);
        imageViews[2] = findViewById(R.id.imageView3);
        imageViews[3] = findViewById(R.id.imageView4);
        imageViews[4] = findViewById(R.id.imageView5);
        imageViews[5] = findViewById(R.id.imageView6);
        imageViews[6] = findViewById(R.id.imageView7);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.document(getIntent().getStringExtra("path"));

        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                   if(!documentSnapshot.exists()){
                       Toast.makeText(this, "Jualan sudah dihapus", Toast.LENGTH_SHORT).show();
                       finish();
                   }
                    getSupportActionBar().setTitle(documentSnapshot.getString("nama"));
                    tv_harga.setText(documentSnapshot.getString("harga"));
                    tv_deskripsi.setText(documentSnapshot.getString("deskripsi"));
                    List<String> imageUrl = (List<String>) documentSnapshot.get("foto");
                    for (int i = 0; i < imageUrl.size(); i++) {
                        Glide.with(this)
                                .load(imageUrl.get(i))
                                .transform(new FitCenter(), new RoundedCorners(64))
                                .into(imageViews[i]);
                    }

                    findViewById(R.id.button_keranjang).setOnClickListener(v -> {
                        Toast.makeText(this, "Sedang proses", Toast.LENGTH_SHORT).show();
                        Map<String, Object> data = new HashMap<>();
                        data.put("nama", documentSnapshot.getString("nama"));
                        data.put("harga", documentSnapshot.getString("harga"));
                        data.put("foto", imageUrl.get(0));
                        data.put("ref", documentSnapshot.getReference());
                        data.put("timestamp", FieldValue.serverTimestamp());

                        db.collection("toko")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("keranjang")
                                .add(data)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        Toast.makeText(this, "Berhasil masuk keranjang", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Timber.d(e);
                    Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
