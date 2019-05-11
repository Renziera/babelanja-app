package id.web.babelanja.babelanja;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("toko").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       DocumentSnapshot document = task.getResult();
                       FirebaseStorage storage = FirebaseStorage.getInstance();
                       storage.getReferenceFromUrl(document.getString("foto")).delete();
                   }else{
                       Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                       finish();
                   }
                });
    }
}
