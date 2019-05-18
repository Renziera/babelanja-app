package id.babelanja;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

public class TambahJualanActivity extends AppCompatActivity {

    boolean edit;
    String uid;

    ImageButton button_pick;
    ImageView[] foto = new ImageView[7];
    List<Image> imageList = new ArrayList<>();
    List<String> urlList = new ArrayList<>();

    EditText tv_nama;
    Spinner spinnerKategori;
    EditText tv_deskripsi;
    EditText tv_harga;
    EditText tv_stok;
    ToggleButton toggle;
    Button button_unggah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_jualan);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        edit = getIntent().getBooleanExtra("edit", false);

        Button button_hapus = findViewById(R.id.button_hapus);

        if(edit){
            getSupportActionBar().setTitle("Edit Jualan");
            button_hapus.setOnClickListener(v -> {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage("Apakah anda yakin ingin menghapus jualan ini ?")
                    .setPositiveButton("Ya, saya yakin", (dialog, which) -> {
                        deleteItem();
                    })
                    .setNeutralButton("Tidak", null)
                    .show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.merah));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.abu));
            });
        }else{
            getSupportActionBar().setTitle("Tambah Jualan");
            button_hapus.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_pick = findViewById(R.id.button_pick);
        foto[0] = findViewById(R.id.foto_1);
        foto[1] = findViewById(R.id.foto_2);
        foto[2] = findViewById(R.id.foto_3);
        foto[3] = findViewById(R.id.foto_4);
        foto[4] = findViewById(R.id.foto_5);
        foto[5] = findViewById(R.id.foto_6);
        foto[6] = findViewById(R.id.foto_7);

        Glide.with(this)
                .load(new ColorDrawable(getResources().getColor(R.color.hijauMuda)))
                .transform(new RoundedCorners(64))
                .into(button_pick);

        button_pick.setOnClickListener(v -> {
            ImagePicker.create(this)
                    .returnMode(ReturnMode.NONE)
                    .limit(7)
                    .theme(R.style.AppThemeNoActionBar)
                    .toolbarDoneButtonText("OK")
                    .toolbarImageTitle("Pilih foto")
                    .start();
        });

        tv_nama = findViewById(R.id.tv_nama);
        tv_deskripsi = findViewById(R.id.tv_deskripsi);
        tv_harga = findViewById(R.id.tv_harga);
        tv_stok = findViewById(R.id.tv_stok);
        toggle = findViewById(R.id.toggle);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        button_unggah = findViewById(R.id.button_unggah);

        tv_harga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString()
                        .replace("Rp", "")
                        .replace(",00", "")
                        .replace(",", "")
                        .replace("R", "")
                        .replace("p", "")
                        .replace(".", "");
                if(input.length() > 0){
                    long angka = Long.parseLong(input);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                    String result = formatter.format(angka);
                    result = result + ",00";
                    tv_harga.removeTextChangedListener(this);
                    tv_harga.setText(result);
                    tv_harga.setSelection(result.indexOf(","));
                    tv_harga.addTextChangedListener(this);
                }
            }
        });

        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ArrayAdapter<CharSequence> adapter;
            if(isChecked){
                ((TextInputLayout) tv_nama.getParent().getParent()).setHint("Nama Jasa");
                tv_stok.setVisibility(View.GONE);
                adapter = ArrayAdapter.createFromResource(this, R.array.jasa, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKategori.setAdapter(adapter);
            }else{
                ((TextInputLayout) tv_nama.getParent().getParent()).setHint("Nama Barang");
                tv_stok.setVisibility(View.VISIBLE);
                adapter = ArrayAdapter.createFromResource(this, R.array.barang, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKategori.setAdapter(adapter);
            }
        });

        button_unggah.setOnClickListener(v -> {
            if(validasi()){
                button_unggah.setEnabled(false);
                Toast.makeText(this, "Sedang proses", Toast.LENGTH_SHORT).show();
                uploadGambar();
            }
        });

        if(edit){
            button_unggah.setText("Simpan");
            DocumentReference docRef = FirebaseFirestore.getInstance()
                    .collection("toko").document(uid)
                    .collection("item").document(getIntent().getStringExtra("id"));
            docRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.getBoolean("barang")){
                            tv_stok.setText(documentSnapshot.getLong("stok").toString());
                            spinnerKategori.setSelection(Arrays.asList(getResources().getStringArray(R.array.barang))
                                    .indexOf(documentSnapshot.getString("kategori")));
                        }else{
                            toggle.setChecked(true);
                            spinnerKategori.setSelection(Arrays.asList(getResources().getStringArray(R.array.jasa))
                                    .indexOf(documentSnapshot.getString("kategori")));
                        }
                        tv_nama.setText(documentSnapshot.getString("nama"));
                        tv_deskripsi.setText(documentSnapshot.getString("deskripsi"));
                        tv_harga.setText(documentSnapshot.getString("harga"));
                        urlList = (List<String>) documentSnapshot.get("foto");
                        for (int i = 0; i < urlList.size(); i++) {
                            foto[i].setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(urlList.get(i))
                                    .transform(new CenterCrop(), new RoundedCorners(64))
                                    .into(foto[i]);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }

    private boolean validasi(){
        if(imageList.isEmpty() && !edit){
            Toast.makeText(this, "Cantumkan minimal 1 foto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tv_nama.getText().toString().isEmpty()){
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tv_deskripsi.getText().toString().isEmpty()){
            Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tv_harga.getText().toString().isEmpty()){
            Toast.makeText(this, "Harga tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tv_stok.getText().toString().isEmpty() && !toggle.isChecked()){
            Toast.makeText(this, "Stok tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadGambar(){
        if(imageList.isEmpty() && edit){
            updateDatabase();
            return;
        }
        urlList.clear();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference stoRef = storage.getReference(uid);

        for (Image image : imageList) {
            StorageReference ref = stoRef.child(UUID.randomUUID().toString());
            ref.putFile(Uri.fromFile(new File(image.getPath())))
                    .continueWithTask(task -> ref.getDownloadUrl())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            urlList.add(task.getResult().toString());
                            updateDatabase();
                        }else{
                            for (String url : urlList) {
                                storage.getReferenceFromUrl(url).delete();
                                urlList.remove(url);
                            }
                            Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                            button_unggah.setEnabled(true);
                        }
                    });
        }

    }

    private void updateDatabase(){
        if(urlList.size() != imageList.size() && !edit){
            return;
        }

        Timber.d("Upload gambar sebanyak:%s", urlList.size());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        Map<String, Object> data = new HashMap<>();
        data.put("nama", tv_nama.getText().toString());
        data.put("deskripsi", tv_deskripsi.getText().toString());
        data.put("harga", tv_harga.getText().toString());
        if(!toggle.isChecked()){
            data.put("stok", Integer.parseInt(tv_stok.getText().toString()));
        }
        data.put("barang", !toggle.isChecked());
        data.put("kategori", spinnerKategori.getSelectedItem().toString());
        data.put("foto", urlList);

        DocumentReference tokoRef;

        if(edit){
            data.put("timestamp_update", FieldValue.serverTimestamp());
            tokoRef = db.collection("toko")
                    .document(uid).collection("item").document(getIntent().getStringExtra("id"));
            tokoRef.get()
                    .continueWithTask(task -> {
                        DocumentReference itemRef = task.getResult().getDocumentReference("ref");
                        batch.update(tokoRef, data);
                        batch.update(itemRef, data);
                        return batch.commit();
                    })
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Berhasil update jualan", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Timber.d(e);
                        Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                        button_unggah.setEnabled(true);
                    });
        }else{
            data.put("timestamp_create", FieldValue.serverTimestamp());
            data.put("rating", 0);

            tokoRef = db.collection("toko")
                    .document(uid).collection("item").document();
            DocumentReference itemRef = db.collection("item").document();

            data.put("ref", itemRef);
            batch.set(tokoRef, data);

            data.put("ref", tokoRef);
            data.put("id_toko", uid);
            batch.set(itemRef, data);
            batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Berhasil tambah jualan", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Timber.d(e);
                        Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                        button_unggah.setEnabled(true);
                    });
        }
    }

    private void deleteItem(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();
        db.collection("toko")
                .document(uid)
                .collection("item")
                .document(getIntent().getStringExtra("id"))
                .get()
                .continueWithTask(task -> {
                    DocumentReference itemRef = task.getResult().getDocumentReference("ref");
                    DocumentReference tokoRef = task.getResult().getReference();
                    urlList = (List<String>) task.getResult().get("foto");
                    for (int i = 1; i < urlList.size(); i++) {
                        FirebaseStorage.getInstance().getReferenceFromUrl(urlList.get(i)).delete();
                    }
                    batch.delete(itemRef);
                    batch.delete(tokoRef);
                    return batch.commit();
                })
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Berhasil menghapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Timber.d(e);
                    Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(ImagePicker.shouldHandle(requestCode, resultCode, data)){
            for (ImageView imgview : foto) {
                imgview.setVisibility(View.GONE);
            }

            imageList = ImagePicker.getImages(data);

            for (int i = 0; i < imageList.size(); i++) {
                foto[i].setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageList.get(i).getPath())
                        .transform(new CenterCrop(), new RoundedCorners(64))
                        .into(foto[i]);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
