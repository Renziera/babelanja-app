package id.babelanja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SearchActivity extends AppCompatActivity {

    private Button button_terbaru;
    private Button button_terpopuler;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText tv_search;
    private ImageButton button_search;

    private boolean terbaru;
    private int selectedKategori;
    private String searchString;
    private List<DocumentSnapshot> searchResult = new ArrayList<>();
    private PencarianAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setTitle("Pencarian");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        button_terbaru = findViewById(R.id.button_terbaru);
        button_terpopuler = findViewById(R.id.button_terpopuler);
        tv_search = findViewById(R.id.tv_search);
        button_search = findViewById(R.id.button_search);

        adapter = new PencarianAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);

        button_search.setOnClickListener(v -> validasi());
        tv_search.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                validasi();
                return true;
            }
            return false;
        });

        ScrollView scrollViewBarang = findViewById(R.id.scrollViewBarang);
        ScrollView scrollViewJasa = findViewById(R.id.scrollViewJasa);
        ToggleButton toggle = findViewById(R.id.toggle);
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                scrollViewBarang.setVisibility(View.GONE);
                scrollViewJasa.setVisibility(View.VISIBLE);
            }else{
                scrollViewBarang.setVisibility(View.VISIBLE);
                scrollViewJasa.setVisibility(View.GONE);
            }
        });

        changeState(button_terbaru);

        int kategori = getIntent().getIntExtra("kategori", 0);
        if(kategori != 0){
            pilihKategori(findViewById(kategori));
        }

        searchString = getIntent().getStringExtra("search");
        if(searchString == null){
            searchString = "";
        }
        pencarian();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void pilihKategori(View v){
        if(selectedKategori != 0){
            findViewById(selectedKategori).setBackgroundColor(Color.TRANSPARENT);
        }
        selectedKategori = v.getId();
        v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void validasi(){
        if(tv_search.getText().toString().length() < 3){
            Toast.makeText(this, "Pencarian minimum 3 karakter", Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        searchString = tv_search.getText().toString();
        pencarian();
    }

    private void pencarian(){
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference colRef = FirebaseFirestore.getInstance().collection("item");
        Query query;
        if(terbaru){
            query = colRef.orderBy("timestamp_create", Query.Direction.DESCENDING);
        }else{
            query = colRef.orderBy("rating", Query.Direction.DESCENDING);
        }
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    searchResult.clear();

                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                        if(ds.getString("nama").toLowerCase().contains(searchString.toLowerCase())){
                            searchResult.add(ds);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Timber.d(e);
                    Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> progressBar.setVisibility(View.GONE));
    }

    public void changeState(View v){
        if(v.getId() == R.id.button_terbaru){
            button_terpopuler.setTextColor(getResources().getColor(R.color.colorPrimary));
            button_terpopuler.setBackgroundColor(Color.TRANSPARENT);
            button_terbaru.setTextColor(Color.WHITE);
            button_terbaru.setBackgroundResource(R.drawable.bg_button);
            terbaru = true;
        }else{
            button_terbaru.setTextColor(getResources().getColor(R.color.colorPrimary));
            button_terbaru.setBackgroundColor(Color.TRANSPARENT);
            button_terpopuler.setTextColor(Color.WHITE);
            button_terpopuler.setBackgroundResource(R.drawable.bg_button);
            terbaru = false;
        }
    }

    class PencarianAdapter extends RecyclerView.Adapter<PencarianAdapter.PencarianViewHolder>{

        @NonNull
        @Override
        public PencarianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.item, parent, false);
            return new PencarianViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PencarianViewHolder holder, int position) {
            DocumentSnapshot ds = searchResult.get(position);
            holder.tv_nama.setText(ds.getString("nama"));
            holder.tv_harga.setText(ds.getString("harga"));
            Glide.with(SearchActivity.this)
                    .load(((List<String>)ds.get("foto")).get(0))
                    .centerCrop()
                    .into(holder.imageView);
            holder.card.setOnClickListener(v -> {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("path", ds.getReference().getPath());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return searchResult.size();
        }

        class PencarianViewHolder extends RecyclerView.ViewHolder{

            TextView tv_nama;
            TextView tv_harga;
            RatingBar ratingBar;
            CardView card;
            ImageView imageView;

            public PencarianViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_nama = itemView.findViewById(R.id.tv_nama);
                tv_harga = itemView.findViewById(R.id.tv_harga);
                ratingBar = itemView.findViewById(R.id.ratingBar);
                card = itemView.findViewById(R.id.card);
                imageView = itemView.findViewById(R.id.imageView);
                tv_nama.setVisibility(View.VISIBLE);
                tv_harga.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                card.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }
}
