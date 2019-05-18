package id.babelanja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class JualanActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    JualanAdapter jualanAdapter;
    List<DocumentSnapshot> documentSnapshots = new ArrayList<>();

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

        recyclerView = findViewById(R.id.recycler_jualan);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        jualanAdapter = new JualanAdapter();
        recyclerView.setAdapter(jualanAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore.getInstance()
                .collection("toko")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("item")
                .orderBy("nama", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    documentSnapshots = queryDocumentSnapshots.getDocuments();
                    jualanAdapter.notifyDataSetChanged();
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

    class JualanAdapter extends RecyclerView.Adapter<JualanAdapter.JualanViewHolder>{

        @NonNull
        @Override
        public JualanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(JualanActivity.this).inflate(R.layout.item, parent, false);
            return new JualanViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull JualanViewHolder holder, int position) {
            DocumentSnapshot documentSnapshot = documentSnapshots.get(position);
            holder.tv_nama.setText(documentSnapshot.getString("nama"));
            holder.button_edit.setOnClickListener(v -> {
                Intent intent = new Intent(JualanActivity.this, TambahJualanActivity.class);
                intent.putExtra("edit", true);
                intent.putExtra("id", documentSnapshot.getId());
                startActivity(intent);
            });
            Glide.with(JualanActivity.this)
                    .load(((List<String>) documentSnapshot.get("foto")).get(0))
                    .centerCrop()
                    .into(holder.imageView);
            holder.cardView.setOnClickListener(v -> {
                Toast.makeText(JualanActivity.this, "Ditekan", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return documentSnapshots.size();
        }

        class JualanViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            TextView tv_nama;
            Button button_edit;
            CardView cardView;

            public JualanViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                tv_nama = itemView.findViewById(R.id.tv_nama);
                button_edit = itemView.findViewById(R.id.button_edit);
                cardView = itemView.findViewById(R.id.card);
            }
        }
    }
}
