package id.babelanja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class KeranjangFragment extends Fragment {

    RecyclerView recyclerView;
    KeranjangAdapter adapter;
    List<DocumentSnapshot> documentSnapshots = new ArrayList<>();

    public KeranjangFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keranjang, container, false);

        adapter = new KeranjangAdapter();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);

        queryDatabase();

        return view;
    }

    private void queryDatabase(){
        FirebaseFirestore.getInstance()
                .collection("toko")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("keranjang")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    documentSnapshots = queryDocumentSnapshots.getDocuments();
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Timber.d(e);
                    Toast.makeText(getActivity(), "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                });
    }

    class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.KeranjangViewHolder>{

        @NonNull
        @Override
        public KeranjangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item, parent, false);
            return new KeranjangViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull KeranjangViewHolder holder, int position) {
            DocumentSnapshot ds  = documentSnapshots.get(position);
            holder.tv_nama.setText(ds.getString("nama"));
            holder.tv_harga.setText(ds.getString("harga"));
            holder.card.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("path", ds.getDocumentReference("ref").getPath());
                startActivity(intent);
            });
            Glide.with(getActivity())
                    .load(ds.getString("foto"))
                    .centerCrop()
                    .into(holder.imageView);
            holder.button_hapus.setOnClickListener(v -> ds.getReference().delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Berhasil hapus", Toast.LENGTH_SHORT).show();
                        queryDatabase();
                    })
                    .addOnFailureListener(e -> {
                        Timber.d(e);
                        Toast.makeText(getActivity(), "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                    }));
        }

        @Override
        public int getItemCount() {
            return documentSnapshots.size();
        }

        class KeranjangViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            TextView tv_nama;
            TextView tv_harga;
            CardView card;
            Button button_hapus;

            public KeranjangViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                tv_nama = itemView.findViewById(R.id.tv_nama);
                tv_harga = itemView.findViewById(R.id.tv_harga);
                card = itemView.findViewById(R.id.card);
                button_hapus = itemView.findViewById(R.id.button_hapus);

                imageView.setVisibility(View.VISIBLE);
                tv_nama.setVisibility(View.VISIBLE);
                tv_harga.setVisibility(View.VISIBLE);
                card.setVisibility(View.VISIBLE);
                button_hapus.setVisibility(View.VISIBLE);
            }
        }
    }
}
